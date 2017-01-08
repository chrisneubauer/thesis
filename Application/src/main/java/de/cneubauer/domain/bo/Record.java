package de.cneubauer.domain.bo;

import java.sql.Timestamp;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Business Object for Record table
 */
public class Record {
    private int id;
    private Timestamp entryDate;
    private String documentNo;
    private byte[] document;
    private String entryText;
    //private Account debit;
    //private Account credit;
    //private double bruttoValue;
    //private double vat_rate;
    //private String salesTaxId;
    private Set<AccountRecord> recordAccounts;

    public Set<AccountRecord> getRecordAccounts() {
        return recordAccounts;
    }

    public void setRecordAccounts(Set<AccountRecord> recordAccounts) {
        this.recordAccounts = recordAccounts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Timestamp entryDate) {
        this.entryDate = entryDate;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public byte[] getDocument() {
        return document;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }

    public String getEntryText() {
        return entryText;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    /* public Account getDebit() {
        return debit;
    }

    public void setDebit(Account debit) {
        this.debit = debit;
    }

    public Account getCredit() {
        return credit;
    }

    public void setCredit(Account credit) {
        this.credit = credit;
    }

    public double getBruttoValue() {
        return bruttoValue;
    }

    public void setBruttoValue(double bruttoValue) {
        this.bruttoValue = bruttoValue;
    }

    public double getVat_rate() {
        return vat_rate;
    }

    public void setVat_rate(double vat_rate) {
        this.vat_rate = vat_rate;
    }

    public String getSalesTaxId() {
        return salesTaxId;
    }

    public void setSalesTaxId(String salesTaxId) {
        this.salesTaxId = salesTaxId;
    }*/
}
