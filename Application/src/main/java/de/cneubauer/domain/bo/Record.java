package de.cneubauer.domain.bo;

import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.util.RecordTrainingEntry;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Business Object for Record table
 */
public class Record {
    public Record() {
        this.recordAccounts = new HashSet<>(0);
    }

    private int id;
    private Date entryDate;
    private String documentNo;
    private byte[] document;
    private String entryText;
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

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
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

    public void addRecordTrainingEntry(RecordTrainingEntry entry) {
        for (Map.Entry<String, Double> mapEntry : entry.getDebitAccounts().entrySet()) {
            try {
                AccountRecord record = new AccountRecord();
                AccountDaoImpl accountDao = new AccountDaoImpl();
                record.setIsDebit(true);
                record.setAccount(accountDao.getByName(mapEntry.getKey()));
                record.setBruttoValue(mapEntry.getValue());
                this.getRecordAccounts().add(record);
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to parse account from string, skipping..");
            }
        }

        for (Map.Entry<String, Double> mapEntry : entry.getCreditAccounts().entrySet()) {
            try {
                AccountRecord record = new AccountRecord();
                AccountDaoImpl accountDao = new AccountDaoImpl();
                record.setIsDebit(false);
                record.setAccount(accountDao.getByName(mapEntry.getKey()));
                record.setBruttoValue(mapEntry.getValue());
                this.getRecordAccounts().add(record);
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to parse account from string, skipping..");
            }
        }
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
