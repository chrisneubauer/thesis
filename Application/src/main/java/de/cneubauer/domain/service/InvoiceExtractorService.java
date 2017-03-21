package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Creditor;
import de.cneubauer.domain.bo.DocumentCase;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.helper.DateHelper;
import de.cneubauer.domain.helper.InvoiceInformationHelper;
import de.cneubauer.ocr.hocr.HocrArea;
import de.cneubauer.ocr.hocr.HocrDocument;
import de.cneubauer.ocr.hocr.HocrElement;
import de.cneubauer.ocr.hocr.HocrLine;
import de.cneubauer.util.DocumentCaseSet;
import de.cneubauer.util.config.ConfigHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Christoph on 17.03.2017.
 * DataExtractionService for invoice
 */
public class InvoiceExtractorService extends DataExtractorService {
    public InvoiceExtractorService(HocrDocument doc, String[] parts) {
        super(doc, parts);
        super.extractInvoice = true;
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
    private Date findDeliveryDate(Date issueDate) {
        String date = this.findValueInString(new String[] { "Lieferdatum"}, this.rightHeader);
        DateHelper helper = new DateHelper();
        LocalDate deliveryDate = helper.stringToDate(date);
        Date result;
        if (deliveryDate.toEpochDay() > 1) {
            result = Date.valueOf(deliveryDate);
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

    private LegalPerson findDebitor() {
        List<String> lines = Arrays.asList(this.leftHeader.split("\\n"));
        LegalPerson debitor = this.getLegalPersonFromDatabase(lines, false);
        // repeat searching if nothing found in database
        if (debitor == null) {
            String[] words = this.leftHeader.split("\\n");
            String person = null;
            for (String word : words) {
                if (word.contains("Herr") || word.contains("Frau")) {
                    // using substring removes pretitle
                    if (word.split(" ").length > 2) {
                        person = word.split(" ")[1] + " " + word.split(" ")[2];
                    } else {
                        person = word.split(" ")[1];
                    }
                    break;
                }
            }
            if (person != null) {
                return new LegalPerson(person);
            }
        }
        return debitor;
    }

    /**
     * Searches for the issue date in the right header of the document
     * @return  the issue date if it has been found, the current date if not
     */
    private Date findIssueDate() {
        String date = this.findValueInString(new String[] { "Datum", "Rechnungsdatum"}, this.rightHeader);
        DateHelper helper = new DateHelper();
        LocalDate issueDate = helper.stringToDate(date);
        Date result;
        if (issueDate.toEpochDay() > 1) {
            result = Date.valueOf(issueDate);
        } else {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find issue date in OCR. Using default value");
            result = Date.valueOf(LocalDate.now());
        }
        return result;
    }

    /**
     * Searche for the invoice number in both header strings
     * @return  the invoice number or an empty string if nothing has been found
     */
    private String findInvoiceNumber() {
        String header = this.leftHeader + "\n" + this.rightHeader;
        String[] searchConditions = new String[] { "rechnung", "rechnungs-nr", "rechnungsnummer" };

        String number = this.findValueInString(searchConditions, header, true);
        if (number.toLowerCase().contains("nr.")) {
            number = number.replace("nr.", "").replace("Nr.", "");
        } else if (number.toLowerCase().contains("nr")) {
            number = number.replace("nr", "").replace("Nr", "");
        } else if (number.toLowerCase().contains("no.")) {
            number = number.replace("no.", "").replace("No.", "");
        } else if (number.toLowerCase().contains("no")) {
            number = number.replace("no", "").replace("No", "");
        }
        return number;
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

    @Override
    public void run() {
        String info = this.extractInvoice? "invoice information" : "account record information";
        Logger.getLogger(this.getClass()).log(Level.INFO, "Thread started. Searching for " + info);
        this.threadInvoice = this.extractInvoiceInformationFromHocr();
    }
}
