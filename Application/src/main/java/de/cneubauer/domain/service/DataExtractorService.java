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
import de.cneubauer.domain.helper.AccountFileHelper;
import de.cneubauer.domain.helper.InvoiceInformationHelper;
import de.cneubauer.ml.LearningService;
import de.cneubauer.ml.Model;
import de.cneubauer.ocr.hocr.*;
import de.cneubauer.util.DocumentCaseSet;
import de.cneubauer.util.RecordTrainingEntry;
import de.cneubauer.util.config.ConfigHelper;
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
public class DataExtractorService {
    private String leftHeader;
    private String rightHeader;
    private String body;
    private String footer;
    private double confidence = 1 - ConfigHelper.getConfidenceRate();
    private HocrDocument document;
    private DocumentCaseDao caseDao;
    private LegalPersonDao legalPersonDao;
    private List<Creditor> creditorList;
    private List<Keyword> keywordList;
    private List<LegalPerson> list;
    private DocumentCaseSet caseSet;

    /**
     * Constructor of the DataExtractorService class
     * @param parts  the resulting strings from the ocr in the following order:
     * <p><ul>
     *    <li>[0]: left header string</li>
     *    <li>[1]: right header string</li>
     *    <li>[2]: body string</li>
     *    <li>[3]: footer string</li>
     * </ul></p>
     */
    public DataExtractorService(String[] parts) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "Using confidence level: " + confidence*100 + "%");
        this.leftHeader = parts[0];
        this.rightHeader = parts[1];
        this.body = parts[2];
        this.footer = parts[3];
        this.legalPersonDao = new LegalPersonDaoImpl();
        this.list = this.legalPersonDao.getAll();
    }

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
    }

    public Invoice extractInvoiceInformationFromHocr() {
        Invoice result = new Invoice();
        result.setCreditor(this.findCreditor());
        if (result.getCreditor() != null) {
            result = this.getCaseInformation(result);
        } else {
            String invNo = this.findInvoiceNumber();
            result.setInvoiceNumber(invNo);
            result.setIssueDate(this.findIssueDate());
            result.setDebitor(this.findDebitor());
        }
        if (this.findSkontoInformation()) {
            result.setHasSkonto(true);
            result.setSkonto(this.findSkontoValue());
        }
        result.setDeliveryDate(this.findDeliveryDate());

        InvoiceInformationHelper helper = this.findInvoiceValues();

        result.setLineTotal(helper.getLineTotal());
        result.setChargeTotal(helper.getChargeTotal());
        result.setAllowanceTotal(helper.getAllowanceTotal());
        result.setTaxBasisTotal(helper.getTaxBasisTotal());
        result.setTaxTotal(helper.getTaxTotal());
        result.setGrandTotal(helper.getGrandTotal());

        return result;
    }

    private Invoice getCaseInformation(Invoice result) {
        List<DocumentCase> cases = this.caseDao.getAllByCreditorName(result.getCreditor().getName());
        if (cases.size() == 0) {
            String invNo = this.findInvoiceNumber();
            result.setInvoiceNumber(invNo);
            result.setIssueDate(this.findIssueDate());
            result.setDebitor(this.findDebitor());
            // return if no cases exist yet
            return result;
        }
        List<DocumentCase> invoiceNo = new LinkedList<>();
        List<DocumentCase> docType = new LinkedList<>();
        List<DocumentCase> invoiceDate = new LinkedList<>();
        List<DocumentCase> buyer = new LinkedList<>();
        int highestCase = 0;
        this.caseSet = new DocumentCaseSet();

        for (DocumentCase documentCase : cases) {
            if (documentCase.getKeyword().getId() == 2) {
                invoiceNo.add(documentCase);
            } else if (documentCase.getKeyword().getId() == 1) {
                docType.add(documentCase);
            } else if (documentCase.getKeyword().getId() == 3) {
                invoiceDate.add(documentCase);
            } else if (documentCase.getKeyword().getId() == 5) {
                buyer.add(documentCase);
            }
            if (documentCase.getCaseId() > highestCase) {
                highestCase = documentCase.getCaseId();
            }
        }

        HocrElement possibleInvoiceNo = this.findInCase(invoiceNo);
        if (possibleInvoiceNo != null) {
            result.setInvoiceNumber(possibleInvoiceNo.getValue());
        }
        //this.findDocTypeInCases(docType, result);
        HocrElement possibleInvoiceDate = this.findInCase(invoiceDate);
        String date = null;
        if (possibleInvoiceDate != null) {
            date = possibleInvoiceDate.getValue();
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

        HocrElement possibleDebitor = this.findInCase(buyer);
        String debitor = null;
        if (possibleDebitor != null && possibleDebitor.getValue() != null) {
            debitor = possibleDebitor.getValue().toLowerCase().trim();
        }
        if (debitor != null) {
            for (LegalPerson p : this.list) {
                if(p.getName().toLowerCase().trim().equals(debitor)) {
                    result.setDebitor(p);
                    break;
                }
            }
        }

        Creditor creditor = null;
        for (Creditor c : this.creditorList) {
            if (c.getLegalPerson().getId() == result.getCreditor().getId()) {
                creditor = c;
                break;
            }
        }

        if (possibleDebitor != null) {
            DocumentCase buyerCase = new DocumentCase();
            buyerCase.setCreditor(creditor);
            buyerCase.setCaseId(highestCase+1);
            buyerCase.setKeyword(keywordList.get(4));
            buyerCase.setPosition(possibleDebitor.getPosition());
            buyerCase.setCreatedDate(java.sql.Date.valueOf(LocalDate.now()));
            this.caseSet.setBuyerCase(buyerCase);
        }

        if (possibleInvoiceNo != null) {
            DocumentCase invoiceNoCase = new DocumentCase();
            invoiceNoCase.setCreditor(creditor);
            invoiceNoCase.setCaseId(highestCase + 1);
            invoiceNoCase.setKeyword(keywordList.get(1));
            invoiceNoCase.setPosition(possibleInvoiceNo.getPosition());
            invoiceNoCase.setCreatedDate(java.sql.Date.valueOf(LocalDate.now()));
            this.caseSet.setInvoiceNoCase(invoiceNoCase);
        }

        if (possibleInvoiceDate != null) {
            DocumentCase invoiceDateCase = new DocumentCase();
            invoiceDateCase.setCreditor(creditor);
            invoiceDateCase.setCaseId(highestCase + 1);
            invoiceDateCase.setKeyword(keywordList.get(2));
            invoiceDateCase.setPosition(possibleInvoiceDate.getPosition());
            invoiceDateCase.setCreatedDate(java.sql.Date.valueOf(LocalDate.now()));
            this.caseSet.setInvoiceDateCase(invoiceDateCase);
        }
        return result;
    }

    private HocrElement findInCase(List<DocumentCase> cases) {
        // TODO: make parameter to repell or attract position
        for (DocumentCase docCase : cases) {
            if (docCase.getIsCorrect()) {
                String[] position = docCase.getPosition().split("\\+");
                // 0: startX, 1: startY, 2: endX, 3: endY
                int[] pos = new int[] {Integer.valueOf(position[0]), Integer.valueOf(position[1]), Integer.valueOf(position[2]), Integer.valueOf(position[3])};

                HocrElement possibleArea = this.document.getPage(0).getByPosition(pos);
                if (possibleArea != null) {
                    HocrParagraph possibleParagraph = (HocrParagraph) possibleArea.getByPosition(pos);
                    if (possibleParagraph != null) {
                        HocrLine possibleLine = (HocrLine) possibleParagraph.getByPosition(pos);
                        if (possibleLine != null) {
                            HocrWord possibleWord = (HocrWord) possibleLine.getByPosition(pos);
                            if (possibleWord != null) {
                                return possibleWord;
                            } else {
                                // refine to multiple words
                                possibleWord = possibleLine.getWordsByPosition(pos);
                                return possibleWord;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Method to search for invoice metadata in the scanned page
     * @return the invoice metadata that has been found
     */
    public Invoice extractInvoiceInformation() {
        Invoice result = new Invoice();
        result.setInvoiceNumber(this.findInvoiceNumber());
        result.setCreditor(this.findCreditor());
        result.setIssueDate(this.findIssueDate());
        result.setDebitor(this.findDebitor());
        if(this.findSkontoInformation()) {
            result.setHasSkonto(true);
            result.setSkonto(this.findSkontoValue());
        }
        result.setDeliveryDate(this.findDeliveryDate());

        InvoiceInformationHelper helper = this.findInvoiceValues();

        result.setLineTotal(helper.getLineTotal());
        result.setChargeTotal(helper.getChargeTotal());
        result.setAllowanceTotal(helper.getAllowanceTotal());
        result.setTaxBasisTotal(helper.getTaxBasisTotal());
        result.setTaxTotal(helper.getTaxTotal());
        result.setGrandTotal(helper.getGrandTotal());

        return result;
    }

    /**
     * Uses scanned page and looks for several information regarding accounting records
     * @return  returns a list of all AccountingRecords that has been found on the page
     */
    public List<Record> extractAccountingRecordInformation() {
        List<Record> records = new LinkedList<>();
        //TODO: Additional filtering through the branch of the company

        //TODO: Filtering if invoice or voucher
        //AccountDao dao = new AccountDaoImpl();

        //List<Account> accountsLeft = dao.getAll();

        int index = 0;
        boolean found = false;
        this.body = this.body.replace("\n\n", "\n");
        String[] lines = this.body.split("\n");
        while (!found && lines.length > index) {
            String line = lines[index];
            if (this.getAverageDistanceOfSearchConditions(line, new String[] { "Art.-Nr.", "Artikel", "Beschreibung", "Pos" }) < 1 - this.getConfidence()) {
                found = true;
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
                        //double avg = this.getAverageDistanceOfSearchConditions(nextLine, new String[] { "Betrag", "Summe", "Zwischensumme", "Gesamtbetrag", "Bruttobetrag", "Nettobetrag"});
                        //if (this.getAverageDistanceOfSearchConditions(nextLine, new String[] { "Betrag", "Summe", "Zwischensumme", "Gesamtbetrag", "Bruttobetrag", "Nettobetrag"}) < 1- this.getConfidence()) {
                            Record r = new Record();
                            String recordLine = this.removeFinancialInformationFromRecordLine(nextLine);
                            RecordTrainingEntry entry = this.recordInLearningFile(recordLine);
                            if (entry == null) {
                                r.setEntryText(nextLine);
                            } else {
                                r.setEntryText(entry.getPosition());
                            }
                            records.add(r);
                            index++;
                        //} else {
                        //    break;
                        //}
                    }
                }
            } else {
                index++;
            }
        }

        // Index out of bounds, Map String String doesn't exist anymore
        List<RecordTrainingEntry> values = AccountFileHelper.getAllRecords();
        //Map<String, String> values = AccountFileHelper.getConfig();

        for (Record r : records) {
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
                        }*/
                    //}
                }
            }
        }
        return records;
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
        /*
        line = line.replaceAll("^\\d{1,9}(,\\d{2})", "");
        line = line.replaceAll("(.*)(^\\d{1,9},\\d{2}(.*)", "");
        String newString = "";
        for (String part : line.split(" ")) {
            if (!part.matches("^\\d{1,9}(,\\d{2})\"")) {
                newString += part + " ";
            }
        }*/
        return line;
    }

    /**
     * checks if record already exists in learning file
     * if this is the case, the existing string is being returned
     * if not, the given string is returned again
     * @param position  the position to be searched for
     * @return  the RecordTrainingEntry with the position found in the learning file
     */
    private RecordTrainingEntry recordInLearningFile(String position) {

        LearningService service = new LearningService();
        Model m = service.getMostLikelyModel(position);


        // AccountFileHelper.getConfig().containsValue(position);
       return AccountFileHelper.findAccountingRecord(position);
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
        // TODO: do we need to find these fields?
        result.setChargeTotal(0);
        result.setAllowanceTotal(0);
        return result;
    }

    /**
     * Searchs for the delivery date in the string
     * @return  the date that has been found, or the current date if nothing has been found
     */
    private java.sql.Date findDeliveryDate() {
        String date = this.findValueInString(new String[] { "Lieferdatum"}, this.rightHeader);
        Calendar cal = this.convertStringToCalendar(date);

        Date deliveryDate = cal.getTime();
        java.sql.Date result;
        if (deliveryDate.getTime() > 0) {
            result = new java.sql.Date(deliveryDate.getTime());
        } else {
            result = java.sql.Date.valueOf(LocalDate.now());
            Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find delivery date in OCR. Using default value");
        }
        return result;
    }

    /**
     * Searches for Skonto in the invoice
     * @return  the found skonto value of there is Skonto, 0.0 otherwise
     */
    private double findSkontoValue() {
        String skonto = "";
        boolean found = false;
        if (!ConfigHelper.isDebugMode()) {
            try {
                String[] lines = this.footer.split("\n");
                for (String line : lines) {
                    if (!found) {
                        // try again with levenshtein distance
                        int amountOfChanges = StringUtils.getLevenshteinDistance(line, "Bei Zahlung innerhalb von Tagen gewähren wir %");
                        double ratio = ((double) amountOfChanges) / (Math.max(line.length(), "Bei Zahlung innerhalb von Tagen gewähren wir %".length() + 2));
                        // take the line if ratio > 80%
                        if (ratio < confidence) {
                            int startIndex = line.indexOf("gewähren wir");
                            skonto = line.substring(startIndex + 1, startIndex + 3);
                            found = true;
                        }
                    }
                }

                // normally skonto information starts with "Bei Zahlung innerhalb.. gewähren wir X% Skonto
                if (skonto.contains("%")) {
                    skonto = skonto.split("%")[0];
                }
                return Double.valueOf(skonto);
            } catch (Exception ex) {
                Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find skonto value in OCR. Using default value");
                return 0;
            }
        } else {
            try {
            for (HocrElement area : this.document.getPage(0).getSubElements()) { //.getAreas()) {
                for (HocrElement p : area.getSubElements()) { //.getParagraphs()) {
                    for (HocrElement line : p.getSubElements()) { //p.getLines()) {
                        HocrLine currentLine = (HocrLine) line;
                        String lineAsString = currentLine.getWordsAsString();
                        if (!found) {
                            // try again with levenshtein distance
                            int amountOfChanges = StringUtils.getLevenshteinDistance(lineAsString, "Bei Zahlung innerhalb von Tagen gewähren wir %");
                            double ratio = ((double) amountOfChanges) / (Math.max(lineAsString.length(), "Bei Zahlung innerhalb von Tagen gewähren wir %".length() + 2));
                            // take the line if ratio > 80%
                            if (ratio < confidence) {
                                int startIndex = lineAsString.indexOf("gewähren wir");
                                skonto = lineAsString.substring(startIndex + 1, startIndex + 3);
                                found = true;
                            }
                        }
                    }
                }// normally skonto information starts with "Bei Zahlung innerhalb.. gewähren wir X% Skonto
                if (skonto.contains("%")) {
                    skonto = skonto.split("%")[0];
                }
                return Double.valueOf(skonto);
            }
        } catch(Exception ex){
                Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find skonto value in OCR. Using default value");
                return 0;
            }
        }
        return 0;
    }

    /**
     * @return  true if "Skonto" is in the footer text, false if otherwise
     */
    private boolean findSkontoInformation() {
        String text = "";
        if (!ConfigHelper.isDebugMode()) {
            text = this.findValueInString(new String[]{"Skonto"}, this.footer);
        } else {
            for (HocrElement area : this.document.getPage(0).getSubElements()) { //.getAreas()) {
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
        for (HocrElement area : document.getPage(0).getSubElements()) { //.getAreas()) {
            for (HocrElement paragraph : area.getSubElements()) { //.getParagraphs()) {
                for (HocrElement line : paragraph.getSubElements()) { //.getLines()) {
                    HocrLine currentLine = (HocrLine) line;
                    lines.add(currentLine.getWordsAsString());
                }
            }
        }

        if (searchForCreditor) {
            for (String line : lines) {
                String listString = creditorList.toString();
                String lpString = list.toString();
                if (creditorList.toString().contains(line)) {
                    for (LegalPerson p : list) {
                        if (p.getName() != null && p.getName().equals(line)) {
                            return p;
                        }
                        if (this.refineSearch(line, p.getName())) {
                            return p;
                        }
                    }
                }
            }
        }
        else {
            for (String line : lines) {
                for (LegalPerson p : list) {
                    if (p.getName() != null && line.contains(p.getName())) {
                    /*if (searchForCreditor) {
                        for (Creditor c : creditorList) {
                            if (c.getLegalPerson().getId() == p.getId()) {
                                return p;
                            } else {
                                return null;
                            }
                        }
                    } else {*/
                        return p;
                        //}
                    } else {
                        if (this.refineSearch(line, p.getName())) {
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
        double distance = StringUtils.getLevenshteinDistance(checkString, compareWith);
        double comparison = distance / checkString.length();
        return comparison < confidenceRate;
    }

    /**
     * Searches for legal persons and compares them with the lines
     * @param lines  the ocr result
     * @return  the legal person that occurs in the lines
     */
    private LegalPerson getLegalPersonFromDatabase(String lines, boolean searchForCreditor) {
        LegalPersonDao dao = new LegalPersonDaoImpl();
        List<LegalPerson> list = dao.getAll();

        for (String line : lines.split("\n")) {
            for (LegalPerson p : list) {
                if (line.contains(p.toString())) {
                    if (searchForCreditor) {
                        for (Creditor c : creditorList) {
                            if (c.getLegalPerson().equals(p)) {
                                return p;
                            }
                        }
                    } else {
                        return p;
                    }
                } else {
                    // refine search if we have some ocr probs
                    /* calculation explanation:
                     * Comparing "18:1 Telecom GmbH" with "Telekom"
                     * Levenshtein-Distance would be: 11
                     * String length: 17
                     * 11 of 17 are incorrect (65%) which means 35% correct
                     * When confidence 80% we need less errors, maximum: 0/17 -> 0% < 20%
                    */
                    double confidenceRate = ConfigHelper.getConfidenceRate();
                    double distance = StringUtils.getLevenshteinDistance(line, p.toString());
                    double comparison = distance / line.length();
                    if (comparison < confidenceRate) {
                        return p;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Searches for debitor in the left header part of the document
     * Uses the getLegalPersonFromDatabase() method to retrieve the result
     * @return  the debitor that has been found or null if none has been found
     */
    private LegalPerson findDebitor() {
        // Debitor is usually in the left part of the invoice header
        //String line = this.findLineWithContainingInformation(new String[] { "Str.", "Straße" }, this.leftHeader);
        //return this.getLegalPersonFromDatabase(this.leftHeader);
        return this.getLegalPersonFromDatabase(document, false);
    }

    /**
     * Searches for the issue date in the right header of the document
     * @return  the issue date if it has been found, the current date if not
     */
    private java.sql.Date findIssueDate() {
        String date = this.findValueInString(new String[] { "Rechnungsdatum"}, this.rightHeader);
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
     * Searches for creditor in the left header part of the document
     * Uses the getLegalPersonFromDatabase() method to retrieve the result
     * @return  the creditor that has been found or null if none has been found
     */
    private LegalPerson findCreditor() {
        return this.getLegalPersonFromDatabase(this.document, true);
        // Creditor is usually in the right part of the invoice header.
        //String line = this.findLineWithContainingInformation(new String[] { "Str.", "Straße" }, this.rightHeader);
        //return this.getLegalPersonFromDatabase(this.rightHeader);
        //int index = this.findCorporateFormIndex(line);
        /*if (index > 0) {
            LegalPerson result = new LegalPerson();
            result.setIsCompany(true);
            // TODO: Store cf in beforehand in db and receive it here by calling the db
            CorporateForm cf = new CorporateForm();
            cf.setShortName(line.substring(index).split(" ")[0]);
            result.setCorporateForm(cf);
            result.setCompanyName(line.substring(0, index - 1));
            return result;*/
        /*} else {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find creditor information in OCR. Using default value");
            return null;
        }*/
    }

    private LegalPerson getLegalPersonFromDatabase(HocrDocument document) {
        for (HocrElement area : document.getPage(0).getSubElements()) { //getAreas()) {
            HocrArea currentArea = (HocrArea) area;
            List<String> allWordsInArea = currentArea.getAllWordsInArea();
            for (int i = 0; i < allWordsInArea.size(); i++) {
                String word = allWordsInArea.get(i);
                for (LegalPerson p : list) {
                    if (p.getName() != null) {
                        if (p.getName().contains(" ")) {
                            int parts = p.getName().split(" ").length;
                            if (i+parts < allWordsInArea.size()) {
                                for (int j = 1; j <= parts - 1; j++) {
                                    word += " " + allWordsInArea.get(i+j);
                                }
                            }
                        }
                        if (word.equals(p.getName())) {
                            return p;
                        } else {
                            // refine search if we have some ocr probs
                    /* calculation explanation:
                     * Comparing "18:1 Telecom GmbH" with "Telekom"
                     * Levenshtein-Distance would be: 11
                     * String length: 17
                     * 11 of 17 are incorrect (65%) which means 35% correct
                     * When confidence 80% we need less errors, maximum: 0/17 -> 0% < 20%
                    */
                            double confidenceRate = ConfigHelper.getConfidenceRate();
                            double distance = StringUtils.getLevenshteinDistance(word, p.getName());
                            double comparison = distance / word.length();
                            if (comparison < confidenceRate) {
                                return p;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Deprecated
    private int findCorporateFormIndex(String line) {
        int result = 0;
        if (line.indexOf("GmbH") > 0) {
            result = line.indexOf("GmbH");
        }
        else if (line.indexOf("AG") > 0) {
            result = line.indexOf("AG");
        }
        else if (line.indexOf("KG") > 0) {
            result = line.indexOf("KG");
        }
        else if (line.indexOf("KGaA") > 0) {
            result = line.indexOf("KgaA");
        }
        else if (line.indexOf("OHG") > 0) {
            result = line.indexOf("OHG");
        }
        else if (line.indexOf("GbR") > 0) {
            result = line.indexOf("GbR");
        }
        return result;
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
                    String part = parts[i];
                    if (part.contains("Rechnungs-Nr") || part.contains("Rechnungsnummer")) {
                        if (i < parts.length - 1) {
                            return parts[i + 1];
                        } else {
                            return "";
                        }
                    } else {
                        // try again with levenshtein distance
                        int amountOfChanges = StringUtils.getLevenshteinDistance(part, "Rechnungs-Nr");
                        double ratio = ((double) amountOfChanges) / (Math.max(part.length(), "Rechnungs-Nr".length()));
                        // take the line if ratio > 80%
                        if (ratio < confidence) {
                            if (i < parts.length - 1) {
                                return parts[i + 1];
                            }
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
     * Searches for the search condition in the search position using the average distance method
     * @param searchConditions  the conditions to be searched for
     * @param searchPosition  the position where to search in
     * @return  the found line if the average is higher than the confidence, an empty string if not
     */
    private String findLineWithContainingInformation(String[] searchConditions, String searchPosition) {
        String[] lines = searchPosition.split("\n");
        for (String line : lines) {
            if (this.getAverageDistanceOfSearchConditions(line, searchConditions) < this.getConfidence()) {
                return line;
            }
        }
        // if we are here we have not found the given conditions
        return "";
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
}
