package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.*;
import de.cneubauer.domain.dao.CreditorDao;
import de.cneubauer.domain.dao.DocumentCaseDao;
import de.cneubauer.domain.dao.KeywordDao;
import de.cneubauer.domain.dao.LegalPersonDao;
import de.cneubauer.domain.dao.impl.CreditorDaoImpl;
import de.cneubauer.domain.dao.impl.DocumentCaseDaoImpl;
import de.cneubauer.domain.dao.impl.KeywordDaoImpl;
import de.cneubauer.domain.dao.impl.LegalPersonDaoImpl;
import de.cneubauer.domain.helper.InvoiceInformationHelper;
import de.cneubauer.domain.helper.TableContentFileHelper;
import de.cneubauer.domain.helper.TableEndFileHelper;
import de.cneubauer.ml.LearningService;
import de.cneubauer.ml.Model;
import de.cneubauer.ocr.hocr.*;
import de.cneubauer.util.DocumentCaseSet;
import de.cneubauer.util.config.ConfigHelper;
import de.cneubauer.util.enumeration.CaseKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Christoph Neubauer on 20.10.2016.
 * This service contains methods for extracting multiple information from a processed form
 * Output is a possible invoice filled with as much information as possible
 */
public class DataExtractorService implements Runnable {
    private String leftHeader;
    private String rightHeader;
    private String body;
    private String footer;
    private HocrDocument table;
    private double confidence = ConfigHelper.getConfidenceRate();
    private HocrDocument document;
    private DocumentCaseDao caseDao;
    private LegalPersonDao legalPersonDao;
    private List<Creditor> creditorList;
    private List<Keyword> keywordList;
    private List<LegalPerson> list;
    private DocumentCaseSet caseSet;
    public boolean extractInvoice;
    private Invoice threadInvoice;
    private List<Record> threadRecord;

    /**
     * Constructor of the DataExtractorService class
     * @param parts  the resulting strings from the ocr in the following order:
     * <p><ul>
     *    <li>[0]: left header string</li>
     *    <li>[1]: right header string</li>
     *    <li>[2]: body string</li>
     *    <li>[3]: footer string</li>
     *    <li>[4]: hocr document string</li>
     * </ul></p>
     */
    public DataExtractorService(HocrDocument hocrDocument, String[] parts) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "Using confidence level: " + confidence*100 + "%");
        this.document = hocrDocument;
        this.caseDao = new DocumentCaseDaoImpl();
        this.legalPersonDao = new LegalPersonDaoImpl();
        this.list = this.legalPersonDao.getAll();

        CreditorDao creditorDao = new CreditorDaoImpl();
        this.creditorList = creditorDao.getAll();

        KeywordDao keywordDao = new KeywordDaoImpl();
        this.keywordList = keywordDao.getAll();

        this.leftHeader = parts[0];
        this.rightHeader = parts[1];
        this.body = parts[2];
        this.footer = parts[3];
        //this.table = new HocrDocument(parts[5]);
    }

    private Invoice extractInvoiceInformationFromHocr() {
        Invoice result = new Invoice();
        result.setCreditor(this.getLegalPersonFromDatabase(this.getHocrDoument(), true));
        if (result.getCreditor() != null) {
            result = this.getCaseInformation(result);
            if (result.getInvoiceNumber() == null || result.getInvoiceNumber().length() == 0) {
                result.setInvoiceNumber(this.findInvoiceNumber());
            }
            if (result.getIssueDate().toLocalDate().compareTo(LocalDate.now()) == 0) {
                result.setIssueDate(this.findIssueDate());
            }
            if (result.getDebitor() == null || result.getDebitor().getName().length() == 0) {
                result.setDebitor(this.findDebitor());
            }
        } else {
            String invNo = this.findInvoiceNumber();
            result.setInvoiceNumber(invNo);
            result.setIssueDate(this.findIssueDate());
            result.setDebitor(this.findDebitor());
            // try again
            if (result.getDebitor() == null) {
                result.setDebitor(this.getLegalPersonFromDatabase(this.getHocrDoument(), false));
            }
        }
        if (this.findSkontoInformation()) {
            result.setHasSkonto(true);
            result.setSkonto(this.findSkontoValue());
        }
        result.setDeliveryDate(this.findDeliveryDate(result.getIssueDate()));

        InvoiceInformationHelper helper = this.findInvoiceValues();

        result.setLineTotal(helper.getLineTotal());
        result.setChargeTotal(helper.getChargeTotal());
        result.setAllowanceTotal(helper.getAllowanceTotal());
        result.setTaxBasisTotal(helper.getTaxBasisTotal());
        result.setTaxTotal(helper.getTaxTotal());
        result.setGrandTotal(helper.getGrandTotal());

        return result;
    }

    private LegalPerson findDebitor() {
        String person = null;
        String[] words = this.leftHeader.split("\\n");
        for (String word : words) {
            if (word.contains("Herr") || word.contains("Frau")) {
                // using substring removes pretitle
                person = word.split(" ")[1] + " " + word.split(" ")[2];
                break;
            }
        }
        if (person != null) {
            return new LegalPerson(person);
        }
        return null;
    }

    private Invoice getCaseInformation(Invoice result) {
        List<DocumentCase> cases = this.caseDao.getAllByCreditorName(result.getCreditor().getName());
        if (cases.size() == 0) {
            String invNo = this.findInvoiceNumber();
            result.setInvoiceNumber(invNo);
            result.setIssueDate(this.findIssueDate());
            result.setDebitor(this.getLegalPersonFromDatabase(this.getHocrDoument(), false));
            // return if no cases exist yet
            return result;
        }
        List<DocumentCase> invoiceNo = new LinkedList<>();
        List<DocumentCase> docType = new LinkedList<>();
        List<DocumentCase> invoiceDate = new LinkedList<>();
        List<DocumentCase> buyer = new LinkedList<>();
        List<DocumentCase> seller = new LinkedList<>();
        int highestCase = 0;
        this.caseSet = new DocumentCaseSet();

        for (DocumentCase documentCase : cases) {
            if (documentCase.getKeyword().getId() == 1) {
                docType.add(documentCase);
            } else if (documentCase.getKeyword().getId() == 2) {
                invoiceNo.add(documentCase);
            } else if (documentCase.getKeyword().getId() == 3) {
                invoiceDate.add(documentCase);
            } else if (documentCase.getKeyword().getId() == 4) {
                seller.add(documentCase);
            } else if (documentCase.getKeyword().getId() == 5) {
                buyer.add(documentCase);
            }
            if (documentCase.getCaseId() > highestCase) {
                highestCase = documentCase.getCaseId();
            }
        }

        // new highest case:
        highestCase = highestCase+1;

        Creditor creditor = null;
        for (Creditor c : this.creditorList) {
            if (c.getLegalPerson().getId() == result.getCreditor().getId()) {
                creditor = c;
                break;
            }
        }

        HocrElement possibleType = this.findInCase(docType);
        if (possibleType != null && possibleType.getPosition() != null) {
            if (possibleType.getValue().toLowerCase().contains("rechnung")) {
                this.caseSet.setDocumentTypeCase(new DocumentCase(creditor, highestCase, keywordList.get(0), possibleType.getPosition()));
            }
        }

        HocrElement possibleInvoiceNo = this.findInCase(invoiceNo);
        if (possibleInvoiceNo != null) {
            result.setInvoiceNumber(possibleInvoiceNo.getValue());
            this.caseSet.setInvoiceNoCase(new DocumentCase(creditor, highestCase, keywordList.get(1), possibleInvoiceNo.getPosition()));
        }

        HocrElement possibleInvoiceDate = this.findInCase(invoiceDate);
        String date = null;
        if (possibleInvoiceDate != null) {
            date = possibleInvoiceDate.getValue();
            this.caseSet.setInvoiceDateCase(new DocumentCase(creditor, highestCase, keywordList.get(2), possibleInvoiceDate.getPosition()));
        }

        try {
            if (date != null) {
                String[] parts = date.split("\\.");
                LocalDate ld = LocalDate.of(Integer.valueOf(parts[2]), Integer.valueOf(parts[1]), Integer.valueOf(parts[0]));
                result.setIssueDate(java.sql.Date.valueOf(ld));
            } else {
                result.setIssueDate(java.sql.Date.valueOf(LocalDate.now()));
            }
        } catch (Exception e) {
            result.setIssueDate(java.sql.Date.valueOf(LocalDate.now()));
        }

        if (creditor != null) {
            HocrElement possibleCreditor = this.findInCase(seller);
            if (possibleCreditor != null) {
                this.caseSet.setSellerCase(new DocumentCase(creditor, highestCase, keywordList.get(3), possibleCreditor.getPosition()));
            }
        }

        HocrElement possibleDebitor = this.findInCase(buyer);
        String debitor = null;
        if (possibleDebitor != null && possibleDebitor.getValue() != null) {
            debitor = possibleDebitor.getValue().toLowerCase().trim();
        }
        if (debitor != null) {
            for (LegalPerson p : this.list) {
                String personName = p.getName().toLowerCase().trim();
                if(personName.equals(debitor) || this.refineSearch(debitor, personName)) {
                    result.setDebitor(p);
                    this.caseSet.setBuyerCase(new DocumentCase(creditor, highestCase, keywordList.get(4), possibleDebitor.getPosition()));
                    break;
                }
            }
        }
        /*
        if (possibleDebitor != null) {
            this.caseSet.setBuyerCase(new DocumentCase(creditor, highestCase, keywordList.get(4), possibleDebitor.getPosition()));
        }*/
        return result;
    }

    private HocrElement findInCase(List<DocumentCase> cases) {
        // TODO: make parameter to repell or attract position
        for (DocumentCase docCase : cases) {
            if (docCase.getIsCorrect()) {
                String[] position = docCase.getPosition().split("\\+");
                // 0: startX, 1: startY, 2: endX, 3: endY
                int[] pos = new int[] {Integer.valueOf(position[0]), Integer.valueOf(position[1]), Integer.valueOf(position[2]), Integer.valueOf(position[3])};

                HocrElement possibleArea = this.document.getPage(0).getByPosition(pos, 50);
                if (possibleArea != null) {
                    HocrParagraph possibleParagraph = (HocrParagraph) possibleArea.getByPosition(pos, 30);
                    if (possibleParagraph != null) {
                        HocrLine possibleLine = (HocrLine) possibleParagraph.getByPosition(pos, 30);
                        if (possibleLine != null) {
                            HocrWord possibleWord = (HocrWord) possibleLine.getByPosition(pos, 10);
                            if (possibleWord != null) {
                                return possibleWord;
                            } else {
                                // refine to multiple words, pixel threshold only a few pixels since we are searching for word
                                possibleWord = possibleLine.getWordsByPosition(pos, 10);
                                return possibleWord;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private List<Record> extractAccountingRecordInformationFromHocr() {
        List<Record> records = new LinkedList<>();
        boolean endReached;
        LegalPerson possibleCreditor = this.getLegalPersonFromDatabase(this.getHocrDoument(), true);

        if (possibleCreditor != null) {
            Creditor creditor = null;
            for (Creditor c : this.creditorList) {
                if (c.getLegalPerson().getId() == possibleCreditor.getId()) {
                    creditor = c;
                    break;
                }
            }

            List<DocumentCase> cases = this.caseDao.getAllByCreditorName(possibleCreditor.getName());

            List<DocumentCase> positionCase = new LinkedList<>();
            int highestCase = 0;
            this.caseSet = new DocumentCaseSet();

            for (DocumentCase documentCase : cases) {
                if (documentCase.getKeyword().getId() == CaseKey.POSITION) {
                    positionCase.add(documentCase);
                }
                if (documentCase.getCaseId() > highestCase) {
                    highestCase = documentCase.getCaseId();
                }
            }

            int minStartX = 2000;
            int minStartY = 2000;
            int possibleMaxX = 0;
            int possibleMaxY = 0;

            // getting starting values of Y and X positions in the document as well as possible endings
            for (DocumentCase position : positionCase) {
                String pos = position.getPosition();
                // 0: startX, 1: startY, 2: endX, 3: endY
                int startX = Integer.valueOf(pos.split("\\+")[0]);
                minStartX = startX < minStartX ? startX : minStartX;
                int startY = Integer.valueOf(pos.split("\\+")[1]);
                minStartY = startY < minStartY ? startY : minStartY;
                int endX = Integer.valueOf(pos.split("\\+")[2]);
                possibleMaxX = endX > possibleMaxX ? endX : possibleMaxX;
                int endY = Integer.valueOf(pos.split("\\+")[3]);
                possibleMaxY = endY > possibleMaxY ? endY : possibleMaxY;
            }

            List<HocrElement> words = this.document.getPage(0).getRecursiveElementsByPosition(new int[]{minStartX, minStartY, possibleMaxX, possibleMaxY}, 20);

            // remove hocrarea and only use words
            /*List<HocrElement> lines = new LinkedList<>();
            for (HocrElement ele : parts) {
                if (ele instanceof HocrArea) {
                    List<HocrElement> paragraphs = ele.getSubElements();
                    for (HocrElement par : paragraphs) {
                        lines.addAll(par.getSubElements());
                    }
                }
                else if (ele instanceof HocrParagraph) {
                    lines.addAll(ele.getSubElements());
                }
                else {
                    lines.add(ele);
                }
            }*/

            // new highest case:
            highestCase = highestCase + 1;

            LearningService service = new LearningService();
            List<HocrElement> lines = new LinkedList<>();
            for (HocrElement word : words) {
                lines.add(word.getParent());
            }
            for (HocrElement line : lines) {
                for (HocrElement word : words) {
                    String position = word.getValue();
                    endReached = this.lineContainsTableEndInformation(position);
                    if (!endReached) {
                        Record r = new Record();
                        String recordLine = this.removeFinancialInformationFromRecordLine(position);
                        double value = this.getValueFromLine(line.getValue());

                        Model m = service.getMostLikelyModel(recordLine);
                        if (m != null) {
                            r.setEntryText(m.getPosition());
                            r.setRecordAccounts(m.getAsAccountRecord(value));
                            r.setProbability(m.getProbability());
                            records.add(r);
                            this.caseSet.addPositionCase(new DocumentCase(creditor, highestCase, keywordList.get(5), position));
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            return records;
        }
        return null;
    }

    /**
     * Uses scanned page and looks for several information regarding accounting records
     * @return  returns a list of all AccountingRecords that has been found on the page
     */
    private List<Record> extractAccountingRecordInformation() {
        List<Record> records = new LinkedList<>();
        int index = 0;
        boolean found = false;
        boolean endReached;
        this.body = this.body.replace("\n\n", "\n");
        String[] lines = this.body.split("\n");
        while (!found && lines.length > index) {
            String line = lines[index];
            if (this.lineContainsTableInformation(line)) {
                found = true;
                LearningService service = new LearningService();
                while (lines.length > index + 1) {
                    String nextLine = lines[index + 1];
                    if(!this.nextLineContainsValue(nextLine) && lines.length > index + 1) {
                        // go on until we find a line with value or end of file reached
                        index++;
                    }
                    else {
                        // now we have a line with position information
                        // or the end if there are keywords like "Betrag" or "Summe"
                        // check these before going on
                        endReached = this.lineContainsTableEndInformation(nextLine);
                        if (!endReached) {
                            Record r = new Record();
                            String recordLine = this.removeFinancialInformationFromRecordLine(nextLine);
                            double value = this.getValueFromLine(nextLine);

                            Model m = service.getMostLikelyModel(recordLine);
                            //RecordTrainingEntry entry = this.recordInLearningFile(recordLine);
                            if (m == null) {
                                r.setEntryText(nextLine);
                            } else {
                                r.setEntryText(m.getPosition());
                                r.setRecordAccounts(m.getAsAccountRecord(value));
                                r.setProbability(m.getProbability());
                            }
                            records.add(r);
                            index++;
                        } else {
                            break;
                        }
                    }
                }
            } else {
                index++;
            }
        }

        // Index out of bounds, Map String String doesn't exist anymore
        //List<RecordTrainingEntry> values = AccountFileHelper.getAllRecords();
        //Map<String, String> values = AccountFileHelper.getConfig();
        /*for (Record r : records) {
            for (RecordTrainingEntry entry : values) {// key : values..keySet()) {
                String key = entry.getPosition();
                if (StringUtils.getLevenshteinDistance(key, r.getEntryText()) < this.getConfidence()) {
                    // distance right, take the entry
                    r.addRecordTrainingEntry(entry);
                    //for (Account a : accountsLeft) {
                        /*if (a.getAccountNo().equals(values.get(key))) {
                            AccountRecord accountRecord = new AccountRecord();
                            accountRecord.setAccount(a);
                            r.getRecordAccounts().add(accountRecord);
                        }
                    //}
                }
            }*/
        //}
        return records;
    }

    private double getValueFromLine(String pos) {
        int euroPos = 0;
        if (pos.toLowerCase().contains("eur")) {
            euroPos = pos.toLowerCase().lastIndexOf("eur");

        } else if (pos.toLowerCase().contains("€")) {
            euroPos = pos.toLowerCase().lastIndexOf("€");
        }
        try {
            pos = pos.substring(0, euroPos);
            String[] parts = pos.split(" ");
            if (parts[parts.length - 1].contains(",")) {
                parts[parts.length - 1] = parts[parts.length - 1].replace(",", ".");
            }
            double value = Double.valueOf(parts[parts.length -1]);
            return value;
        } catch (Exception e) {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Unable to parse double value, using default");
            return 0;
        }
    }

    /**
     * Takes a line and searches for keywords that are usually in the header of an invoice table
     * @param line the line to be searched
     * @return true if the line contains table information
     */
    private boolean lineContainsTableInformation(String line) {
        line = line.toLowerCase().trim();
        boolean result = false;
        // first iteration: search for some strings:
        List<String> valuesToCheck = TableContentFileHelper.getValues();
        for (String value : valuesToCheck) {
            result = (result || line.contains(value));
        }
        if (result) { return true; }
        else {
            // second iteration: we have to refine the search because we didn't find any of those words
            if (this.refineSearch(line.split(" "), valuesToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Takes a line and searches for keywords that are usually in the end of an invoice table
     * @param line the line to be searched
     * @return true if the line contains table end information
     */
    private boolean lineContainsTableEndInformation(String line) {
        line = line.toLowerCase().trim();
        boolean result = false;
        // first iteration: search for some strings:
        List<String> valuesToCheck = TableEndFileHelper.getValues();
        for (String value : valuesToCheck) {
            result = (result || line.contains(value));
        }
        if (result) { return true; }
        else {
            // second iteration: we have to refine the search because we didn't find any of those words
            if (this.refineSearch(line.split(" "), valuesToCheck)) {
                return true;
            }
        }
        return false;
    }

    private boolean refineSearch(String[] line, List<String> valuesToCheck) {
        boolean result = false;
        for (String string : line) {
            for (String value : valuesToCheck) {
                result = result || this.refineSearch(string, value);
            }
            if (result) { return true; }
        }
        return false;
    }

    /**
     * Removes unnecessary columns with financial information
     * @param line  the line that should be checked
     * @return  the original line cleared by financial information
     */
    private String removeFinancialInformationFromRecordLine(String line) {
        line = line.replace("EUR", "");
        line = line.replace("€","");
        // regex that replaces all occurances of numbers up to 100 million + "," and two digits afterwards
        Pattern p = Pattern.compile("\\d{1,9}(,\\d{2})");
        Matcher m = p.matcher(line);

        line = m.replaceAll("");
        return line;
    }

    /**
     * Searches for various financial information and returns them as an InvoiceInformationHelper class
     * @return  an InvoiceInformationHelper containing the found values
     */
    private InvoiceInformationHelper findInvoiceValues() {
        InvoiceInformationHelper result = new InvoiceInformationHelper();
        try {
            String lineTotal = this.findValueInString(new String[] { "Zwischensumme" }, this.footer);
            if (lineTotal.contains(",")) {
                String[] values = lineTotal.split(",");
                lineTotal = values[0] + "." + values[1];
            }
            result.setLineTotal(Double.valueOf(lineTotal));
        } catch (Exception ex) {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find line total in OCR. Using default value");
            result.setLineTotal(0);
        }
        try {
            String taxBasis = this.findValueInString(new String[]{"Nettobetrag", "Netto", "Nettosumme"}, this.footer);
            if (taxBasis.contains(",")) {
                String[] values = taxBasis.split(",");
                taxBasis = values[0] + "." + values[1];
            }
            result.setTaxBasisTotal(Double.valueOf(taxBasis));
            if (Double.valueOf(taxBasis) > 0 && result.getLineTotal() == 0) {
                result.setLineTotal(Double.valueOf(taxBasis));
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find tax basis total in OCR. Using default value");
            result.setTaxBasisTotal(0);
        }
        try {
            String tax = this.findValueInString(new String[] { "MwSt", "USt", "Mehrwertsteuer" }, this.footer);
            if (tax.contains(",")) {
                String[] values = tax.split(",");
                tax = values[0] + "." + values[1];
            }
            result.setTaxTotal(Double.valueOf(tax));
        } catch (Exception ex) {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find tax total in OCR. Using default value");
            result.setTaxTotal(0);
        }
        try {
            String grandTotal = this.findValueInString(new String[] { "Gesamtbetrag", "Gesamt", "Rechnungsbetrag"}, this.footer);

            if (grandTotal.length() == 0) {
                // second approach:
                for (String line : this.footer.split("\n")) {
                    if (this.getAverageDistanceOfSearchConditions(line, new String[] {"Zu zahlender Betrag"}) < 0.2) {
                        grandTotal = line.replaceAll("[^0-9]+","");
                        break;
                    }
                }
            }
            if (grandTotal.contains(",")) {
                String[] values = grandTotal.split(",");
                grandTotal = values[0] + "." + values[1];
            }
            result.setGrandTotal(Double.valueOf(grandTotal));
        } catch (Exception ex) {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find grand total in OCR. Using default value");
            result.setGrandTotal(0);
        }
        if (result.getTaxTotal() > 0 && result.getTaxBasisTotal() > 0 && result.getGrandTotal() == 0) {
            // otherwise use the values of tax basis + tax
            result.setGrandTotal(result.getTaxBasisTotal() + result.getTaxTotal());
        }
        // TODO: do we need to find these fields?..yup!
        result.setChargeTotal(0);
        result.setAllowanceTotal(0);
        return result;
    }

    /**
     * Searchs for the delivery date in the string
     * @return  the date that has been found, or the current date if nothing has been found
     */
    private java.sql.Date findDeliveryDate(java.sql.Date issueDate) {
        String date = this.findValueInString(new String[] { "Lieferdatum"}, this.rightHeader);
        Calendar cal = this.convertStringToCalendar(date);

        Date deliveryDate = cal.getTime();
        java.sql.Date result;
        if (deliveryDate.getTime() > 0) {
            result = new java.sql.Date(deliveryDate.getTime());
        } else {
            result = issueDate;
            Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find delivery date in OCR. Using issue date");
        }
        return result;
    }

    /**
     * Searches for Skonto in the invoice
     * @return  the found skonto value of there is Skonto, 0.0 otherwise
     */
    private double findSkontoValue() {
        String skonto;
        if (!ConfigHelper.isDebugMode()) {
            try {
                String[] lines = this.footer.split("\n");
                for (String line : lines) {
                    skonto = this.findSkontoString(line);
                    if (skonto != null) {
                        // normally skonto information starts with "Bei Zahlung innerhalb.. gewähren wir X% Skonto
                        if (skonto.contains("%")) {
                            skonto = skonto.split("%")[0];
                        }
                        return Double.valueOf(skonto);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find skonto value in OCR. Using default value");
                return 0;
            }
        } else {
            try {
            for (HocrElement area : this.document.getPage(0).getSubElements()) {
                for (HocrElement p : area.getSubElements()) {
                    for (HocrElement line : p.getSubElements()) {
                        HocrLine currentLine = (HocrLine) line;
                        String lineAsString = currentLine.getWordsAsString();
                        skonto = this.findSkontoString(lineAsString);
                        if (skonto != null) {
                            if (skonto.contains("%")) {
                                // normally skonto information starts with "Bei Zahlung innerhalb.. gewähren wir X% Skonto
                                skonto = skonto.split("%")[0];
                            }
                            return Double.valueOf(skonto);
                        }
                    }
                }
            }
        } catch(Exception ex){
                Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find skonto value in OCR. Using default value");
                return 0;
            }
        }
        return 0;
    }

    private String findSkontoString(String line) {
        // try again with levenshtein distance
        int amountOfChanges = StringUtils.getLevenshteinDistance(line, "Bei Zahlung innerhalb von Tagen gewähren wir %");
        double ratio = ((double) amountOfChanges) / (Math.max(line.length(), "Bei Zahlung innerhalb von Tagen gewähren wir %".length() + 2));
        // take the line if ratio > 80%
        if (ratio < confidence) {
            int startIndex = line.indexOf("gewähren wir");
            return line.substring(startIndex + 1, startIndex + 3);
        }
        return null;
    }

    /**
     * @return  true if "Skonto" is in the footer text, false if otherwise
     */
    private boolean findSkontoInformation() {
        String text = "";
        if (!ConfigHelper.isDebugMode()) {
            text = this.findValueInString(new String[]{"Skonto"}, this.footer);
        } else {
            for (HocrElement area : this.document.getPage(0).getSubElements()) {
                HocrArea currentArea = (HocrArea) area;
                for (String word : currentArea.getAllWordsInArea()) {
                    if (word.contains("Skonto")) {
                        return true;
                    }
                }
            }
        }
        return text.length() > 0;
    }

    private LegalPerson getLegalPersonFromDatabase(HocrDocument document, boolean searchForCreditor) {
        LegalPersonDao dao = new LegalPersonDaoImpl();
        List<LegalPerson> list = dao.getAll();

        List<String> lines = new LinkedList<>();
        for (HocrElement area : document.getPage(0).getSubElements()) {
            for (HocrElement paragraph : area.getSubElements()) {
                for (HocrElement line : paragraph.getSubElements()) {
                    HocrLine currentLine = (HocrLine) line;
                    lines.add(currentLine.getWordsAsString());
                }
            }
        }

        if (searchForCreditor) {
            for (String line : lines) {
                // As almost all creditors are companies, we can try to improve this line if we find some corporate information
                // ocr errors, such as a whole line containing not only the company, but also their address can be made more accurate
                // therefore, we search for corporate information. If we find any, we will only take the first part of the string
                int idx = this.findCorporateFormIndex(line);
                if (idx > 0) {
                    line = line.substring(0, idx);
                }
                String compareLine = line.trim().toLowerCase();
                for (Creditor c : creditorList) {
                    String creditor = c.getName().trim().toLowerCase();
                    if (compareLine.contains(creditor) || refineSearch(compareLine, creditor)) {
                        return c.getLegalPerson();
                    }
                }
            }
        }
        else {
            for (String line : lines) {
                for (LegalPerson p : list) {
                    if (p.getName() != null) {
                        String person = p.getName().trim().toLowerCase();
                        String toCheck = line.trim().toLowerCase();
                        if (toCheck.contains(person) || this.refineSearch(toCheck, person)) {
                            return p;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean refineSearch(String checkString, String compareWith) {
        // refine search if we have some ocr probs
        /* calculation explanation:
         * Comparing "18:1 Telecom GmbH" with "Telekom"
         * Levenshtein-Distance would be: 11
         * String length: 17
         * 11 of 17 are incorrect (65%) which means 35% correct
         * When confidence 80% we need less errors, maximum: 0/17 -> 0% < 20%
        */
        if (checkString == null || compareWith == null) {
            return false;
        }
        double confidenceRate = ConfigHelper.getConfidenceRate();
        double distance = StringUtils.getLevenshteinDistance(checkString.toLowerCase().trim(), compareWith.toLowerCase().trim());
        double comparison = distance / checkString.length();
        return comparison < confidenceRate;
    }

    /**
     * Searches for the issue date in the right header of the document
     * @return  the issue date if it has been found, the current date if not
     */
    private java.sql.Date findIssueDate() {
        String date = this.findValueInString(new String[] { "Datum", "Rechnungsdatum"}, this.rightHeader);
        Calendar cal = this.convertStringToCalendar(date);

        Date issueDate = cal.getTime();
        java.sql.Date result;
        if (issueDate.getTime() > 0) {
            result = new java.sql.Date(issueDate.getTime());
        } else {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find issue date in OCR. Using default value");
            result = java.sql.Date.valueOf(LocalDate.now());
        }
        return result;
    }

    /**
     * Converts a string containing date information to a Calendar object
     * @param date  the date that should be converted
     * @return  the Calendar object with the given date
     */
    private Calendar convertStringToCalendar(String date) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "Trying to convert date: " + date);
        Calendar cal = new Calendar.Builder().build();
        if (date.contains(".")) {
            String[] dateValues = date.split("\\.");
            if (dateValues.length == 3) {
                // we expect german calendar writing style, so days are in the first row, then months, then years
                // TODO: Do internationalization as a setting in the application
                if (dateValues[2].length() > 2) {
                    cal.set(Calendar.YEAR, Integer.parseInt(dateValues[dateValues.length - 1]));
                }
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateValues[0]));
                cal.set(Calendar.MONTH, Integer.parseInt(dateValues[1]));
            }
        }
        return cal;
    }

    /**
     * Searche for the invoice number in both header strings
     * @return  the invoice number or an empty string if nothing has been found
     */
    private String findInvoiceNumber() {
            String header = this.leftHeader + "\n" + this.rightHeader;
            String[] lines = header.split("\n");
            for (String line : lines) {
                String[] parts = line.split(" ");
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i].toLowerCase();
                    if (part.contains("rechnung") || part.contains("rechnungs-Nr") || part.contains("rechnungsnummer")) {
                        if (i < parts.length - 1) {
                            if (parts[i+1].toLowerCase().contains("nr")) {
                                if (i < parts.length - 2) {
                                    return parts[i+2];
                                }
                            }
                            return parts[i + 1];
                        } else {
                            return "";
                        }
                    } else {
                        // try again with levenshtein distance
                        int amountOfChanges = StringUtils.getLevenshteinDistance(part, "rechnungs-nr");
                        double ratio = ((double) amountOfChanges) / (Math.max(part.length(), "rechnungs-nr".length()));
                        // take the line if ratio > 80%
                        if (ratio < confidence) {
                            if (parts[i+1].toLowerCase().contains("nr")) {
                                if (i < parts.length - 2) {
                                    return parts[i+2];
                                }
                            }
                            return parts[i + 1];
                        }
                    }
                }
            }
        // if we are here we have not found an invoice number
        return "";
    }

    /**
     * This method goes through the searchPosition and tries to find any of the given search conditions
     * @param searchConditions  the conditions that should be searched for
     * @param searchPosition  the string where the conditions should be in
     * @return  the next word after the found search condition, or an empty string if nothing has been found
     */
    private String findValueInString(String[] searchConditions, String searchPosition) {
        String[] lines = searchPosition.split("\n");
        for (String line : lines) {
            String[] parts = line.split(" ");
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (this.partContainsSearchConditions(part, searchConditions)) {
                    if (i < parts.length - 1) {
                        return parts[i+1];
                    } else {
                        return "";
                    }
                } else {
                    // try again with levenshtein distance
                    double avgRatio = this.getAverageDistanceOfSearchConditions(part, searchConditions);
                    // take the line if ratio > 80%
                    if (avgRatio < confidence) {
                        if (i < parts.length - 1) {
                            return parts[i+1];
                        }
                    }
                }
            }
        }
        // if we are here we have not found an invoice number
        return "";
    }

    /**
     * Calculates the average distance of all given searchConditions
     * This enables an overall check for multiple words
     * @param part  the string where the conditions should be in
     * @param searchConditions  the conditions that should be searched for
     * @return  the average distance as a double value
     */
    private double getAverageDistanceOfSearchConditions(String part, String[] searchConditions) {
        double min;
        double oldMin = 100;
        double result = 100;
        // we want to find signal words in the string
        // therefore the whole line has to be separated into each word
        String[] lines = part.split(" ");
        for (String con : searchConditions) {
            for (String line : lines) {
                min = StringUtils.getLevenshteinDistance(line, con);
                if (min < oldMin) {
                    result = min / line.length();
                    oldMin = min;
                }
                //result += ((double) amountOfChanges) / (Math.max(part.length(), con.length()));
            }
        }
        return result / 2; // additional divisor to adjust number influence level // / (double) searchConditions.length;
    }

    /**
     *
     * @param part  the part to be checked
     * @param searchConditions  the search conditions to be searched for
     * @return  true if at least one of the search conditions are in the given part, false if otherwise
     */
    private boolean partContainsSearchConditions(String part, String[] searchConditions) {
        for (String con : searchConditions) {
            if (part.contains(con)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param nextLine  the line that should be searched in
     * @return  true if the next line contains a numeric value
     */
    private boolean nextLineContainsValue(String nextLine) {
        if (nextLine.contains(",")) {
            String[] parts = nextLine.split(",");
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                part = part.substring(part.length() - 1, part.length());
                String nextPart = parts[i+1];
                if (StringUtils.isNumeric(part) && StringUtils.isNumeric(nextPart.substring(0,1))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return  the confidence that should be reached at least
     */
    private double getConfidence() {
        return confidence;
    }

    public DocumentCaseSet getCaseSet() {
        return caseSet;
    }

    private int findCorporateFormIndex(String line) {
        line = line.toLowerCase();
        int result = 0;
        if (line.indexOf("gmbh") > 0) {
            result = line.indexOf("gmbh") + 4;
        }
        else if (line.indexOf("ag") > 0) {
            result = line.indexOf("ag") + 2;
        }
        else if (line.indexOf("kg") > 0) {
            result = line.indexOf("kg") + 2;
        }
        else if (line.indexOf("kgaa") > 0) {
            result = line.indexOf("kgaa") + 4;
        }
        else if (line.indexOf("ohg") > 0) {
            result = line.indexOf("ohg") + 3;
        }
        else if (line.indexOf("gbr") > 0) {
            result = line.indexOf("gbr") + 3;
        }
        return result;
    }

    private HocrDocument getHocrDoument() {
        return this.document;
    }

    @Override
    public void run() {
        String info = this.extractInvoice? "invoice information" : "account record information";
        Logger.getLogger(this.getClass()).log(Level.INFO, "Thread started. Searching for " + info);
        if (this.extractInvoice) {
            this.threadInvoice = this.extractInvoiceInformationFromHocr();
        } else {
            this.threadRecord = this.extractAccountingRecordInformationFromHocr();
            if (this.threadRecord == null || this.threadRecord.size() == 0) {
                Logger.getLogger(this.getClass()).log(Level.INFO, "No record information in hOCR, using default strategy");
                this.threadRecord = extractAccountingRecordInformation();
            }
        }
    }

    public Invoice getThreadInvoice() {
        if (this.threadInvoice == null) {
            return null;
        } else {
            this.caseDao = null;
            this.legalPersonDao = null;
            return this.threadInvoice;
        }
    }

    public List<Record> getThreadRecord() {return this.threadRecord;}
}
