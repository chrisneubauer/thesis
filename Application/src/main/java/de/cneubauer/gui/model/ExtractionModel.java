package de.cneubauer.gui.model;

import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.bo.Invoice;

import java.util.List;

/**
 * Created by Christoph Neubauer on 29.11.2016.
 * Model that contains information about electronic invoice and accounting records
 */
public class ExtractionModel {
    private Invoice invoiceInformation;
    private List<AccountingRecord> accountingRecords;

    public Invoice getInvoiceInformation() {
        return invoiceInformation;
    }

    public void setInvoiceInformation(Invoice invoiceInformation) {
        this.invoiceInformation = invoiceInformation;
    }

    public List<AccountingRecord> getAccountingRecords() {
        return accountingRecords;
    }

    public void setAccountingRecords(List<AccountingRecord> accountingRecords) {
        this.accountingRecords = accountingRecords;
    }
}
