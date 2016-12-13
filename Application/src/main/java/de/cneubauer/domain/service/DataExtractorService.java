package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.LegalPersonDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.domain.dao.impl.LegalPersonDaoImpl;
import de.cneubauer.domain.helper.AccountFileHelper;
import de.cneubauer.domain.helper.InvoiceInformationHelper;
import de.cneubauer.util.config.Cfg;
import de.cneubauer.util.config.ConfigHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

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
    private double confidence = 1 - (Double.valueOf(ConfigHelper.getValue("confidenceRate")));

    public DataExtractorService(String[] parts) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "Using confidence level: " + confidence*100 + "%");
        this.leftHeader = parts[0];
        this.rightHeader = parts[1];
        this.body = parts[2];
        this.footer = parts[3];
    }

    /*
     * Method to search for invoice metadata in the scanned page
     * @return the invoice metadata that has been found
     */
    public Invoice extractInvoiceInformation() {
        Invoice result = new Invoice();
        result.setInvoiceNumber(this.findInvoiceNumber());
        result.setIssueDate(this.findIssueDate());
        result.setCreditor(this.findCreditor());
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

    /*
     * Uses scanned page and looks for several information regarding accounting records
     * @return  returns a list of all AccountingRecords that has been found on the page
     */
    public List<AccountingRecord> extractAccountingRecordInformation() {
        List<AccountingRecord> records = new LinkedList<>();
        //TODO: Additional filtering through the branch of the company

        //TODO: Filtering if invoice or voucher
        AccountDao dao = new AccountDaoImpl();

        List<Account> accountsLeft = dao.getAll();

        int index = 0;
        String[] lines = this.body.split("\n");
        for (String line : lines) {
            if (this.getAverageDistanceOfSearchConditions(line, new String[] { "Art.-Nr.", "Artikel", "Beschreibung" }) < this.getConfidence()) {
                if (lines.length > index) {
                    String nextLine = lines[index + 1];
                    while(!this.nextLineContainsValue(nextLine) && lines.length > index + 1) {
                        // go on until we find a line with value or end of file reached
                        index++;
                    }
                    // now we have a line with position information
                    AccountingRecord r = new AccountingRecord();
                    r.setEntryText(nextLine);
                    records.add(r);
                } else {
                    // break on eof
                    break;
                }
            } else {
                index++;
            }
        }

        Map<String, String> values = AccountFileHelper.getConfig();

        for (AccountingRecord r : records) {
            for (String key : values.keySet()) {
                if (StringUtils.getLevenshteinDistance(key, r.getEntryText()) < this.getConfidence()) {
                    for (Account a : accountsLeft) {
                        if (a.getAccountNo().equals(values.get(key))) {
                            r.setDebit(a);
                        }
                    }
                }
            }
        }
        return records;
    }

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

    private Timestamp findDeliveryDate() {
        String date = this.findValueInString(new String[] { "Lieferdatum"}, this.rightHeader);
        Calendar cal = this.convertStringToCalendar(date);

        Date deliveryDate = cal.getTime();
        Timestamp result;
        if (deliveryDate.getTime() > 0) {
            result = new Timestamp(deliveryDate.getTime());
        } else {
            result = Timestamp.valueOf(LocalDateTime.now());
            Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find delivery date in OCR. Using default value");
        }
        return result;
    }

    private double findSkontoValue() {
        String skonto = "";
        boolean found = false;
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
                        skonto = line.substring(startIndex+1, startIndex+3);
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
    }

    private boolean findSkontoInformation() {
        String text = this.findValueInString(new String[] { "Skonto" }, this.footer);
        return text.length() > 0;
    }

    private LegalPerson getFromDatabase(String line) {
        LegalPersonDao dao = new LegalPersonDaoImpl();
        List<LegalPerson> list = dao.getAll();
        for (LegalPerson p : list) {
            if (line.contains(p.getName())) {
                return p;
            } else {
                // refine search if we have some ocr probs
                double confidenceRate = Double.valueOf(ConfigHelper.getValue(Cfg.CONFIDENCERATE.getValue()));
                if (StringUtils.getLevenshteinDistance(line, p.getName()) > confidenceRate) {
                    return p;
                }
            }
        }
        return null;
    }

    private LegalPerson findDebitor() {
        // Debitor is usually in the left part of the invoice header
        String line = this.findLineWithContainingInformation(new String[] { "Str.", "Straße" }, this.leftHeader);
        return this.getFromDatabase(line);
    }

    private Timestamp findIssueDate() {

        String date = this.findValueInString(new String[] { "Rechnungsdatum"}, this.rightHeader);
        Calendar cal = this.convertStringToCalendar(date);

        Date issueDate = cal.getTime();
        Timestamp result;
        if (issueDate.getTime() > 0) {
            result = new Timestamp(issueDate.getTime());
        } else {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Could not find issue date in OCR. Using default value");
            result = Timestamp.valueOf(LocalDateTime.now());
        }
        return result;
    }

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

    /*  Finds creditor in image
     *  The following steps are performed:
     *  Get stored LegalPersons in db
     *  Compare with found creditor
     *  if existent, use existing one -> return LP
     *  if not existent, user has to create new one manually -> return null
     */
    private LegalPerson findCreditor() {
        // Creditor is usually in the right part of the invoice header.
        String line = this.findLineWithContainingInformation(new String[] { "Str.", "Straße" }, this.rightHeader);
        return this.getFromDatabase(line);
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

    private String findInvoiceNumber() {
        String header = this.leftHeader + "\n" + this.rightHeader;
        String[] lines = header.split("\n");
        for (String line : lines) {
            String[] parts = line.split(" ");
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (part.contains("Rechnungs-Nr") || part.contains("Rechnungsnummer")) {
                    if (i < parts.length - 1) {
                        return parts[i+1];
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
                            return parts[i+1];
                        }
                    }
                }
            }
        }
        // if we are here we have not found an invoice number
        return "";
    }

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

    private double getAverageDistanceOfSearchConditions(String part, String[] searchConditions) {
        double result = 0;
        for (String con : searchConditions) {
            int amountOfChanges = StringUtils.getLevenshteinDistance(part, con);
            result += ((double) amountOfChanges) / (Math.max(part.length(), con.length()));
        }
        return result / (double) searchConditions.length;
    }

    private boolean partContainsSearchConditions(String part, String[] searchConditions) {
        for (String con : searchConditions) {
            if (part.contains(con)) {
                return true;
            }
        }
        return false;
    }

    private String findLineWithContainingInformation(String[] searchConditions, String searchPosition) {
        String[] lines = searchPosition.split("\n");
        for (String line : lines) {
            if (this.getAverageDistanceOfSearchConditions(line, searchConditions) < this.getConfidence()) {
                return line;
            }
        }
        // if we are here we have not found an invoice number
        return "";
    }

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

    private double getConfidence() {
        return confidence;
    }
}
