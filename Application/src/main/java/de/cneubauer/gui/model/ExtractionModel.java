package de.cneubauer.gui.model;

import de.cneubauer.domain.bo.Record;
import de.cneubauer.domain.bo.Invoice;

import java.util.List;

/**
 * Created by Christoph Neubauer on 29.11.2016.
 * Model that contains information about electronic invoice and accounting records
 */
public class ExtractionModel {
    private Invoice invoiceInformation;
    private List<Record> records;

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
}
