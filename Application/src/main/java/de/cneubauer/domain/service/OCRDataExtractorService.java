package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.helper.InvoiceInformationHelper;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by Christoph Neubauer on 20.10.2016.
 * This service contains methods for extracting multiple information from a processed form
 * Output is a possible invoice filled with as much information as possible
 */
public class OCRDataExtractorService {
    private String file;

    public OCRDataExtractorService(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Invoice extractInformation() {
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

    private InvoiceInformationHelper findInvoiceValues() {
        //TODO: Make actual calculation
        InvoiceInformationHelper result = new InvoiceInformationHelper();
        result.setLineTotal(7);
        result.setChargeTotal(0);
        result.setAllowanceTotal(66);
        result.setTaxBasisTotal(0);
        result.setTaxTotal(0);
        result.setGrandTotal(0);
        return result;
    }

    private Timestamp findDeliveryDate() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    private double findSkontoValue() {
        return 0;
    }

    private boolean findSkontoInformation() {
        return false;
    }

    private LegalPerson findDebitor() {
        return null;
    }

    private Timestamp findIssueDate() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    private LegalPerson findCreditor() {
        return null;
    }

    private String findInvoiceNumber() {
        return "";
    }
}
