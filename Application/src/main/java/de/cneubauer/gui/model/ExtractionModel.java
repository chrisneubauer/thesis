package de.cneubauer.gui.model;

import de.cneubauer.domain.bo.Record;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.ocr.hocr.HocrDocument;
import de.cneubauer.util.DocumentCaseSet;

import java.util.List;

/**
 * Created by Christoph Neubauer on 29.11.2016.
 * Model that contains information about electronic invoice and accounting records
 */
public class ExtractionModel {
    private Invoice invoiceInformation;
    private Invoice updatedInvoiceInformation;

    private List<Record> records;
    private List<Record> updatedRecords;

    private HocrDocument hocrDocument;
    private DocumentCaseSet caseSet;

    private byte[] file;

    public Invoice getInvoiceInformation() {
        return invoiceInformation;
    }

    public void setInvoiceInformation(Invoice invoiceInformation) {
        this.invoiceInformation = invoiceInformation;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public DocumentCaseSet getCaseSet() {
        return caseSet;
    }

    public void setCaseSet(DocumentCaseSet caseSet) {
        this.caseSet = caseSet;
    }

    public void setHocrDocument(HocrDocument hocrDocument) {
        this.hocrDocument = hocrDocument;
    }

    public void setUpdatedInvoiceInformation(Invoice updatedInvoiceInformation) {
        this.updatedInvoiceInformation = updatedInvoiceInformation;
    }

    public void setUpdatedRecords(List<Record> updatedRecords) {
        this.updatedRecords = updatedRecords;
    }

    public Invoice getUpdatedInvoiceInformation() {
        return updatedInvoiceInformation;
    }

    public List<Record> getUpdatedRecords() {
        return updatedRecords;
    }

    public HocrDocument getHocrDocument() {
        return hocrDocument;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
