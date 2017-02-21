package de.cneubauer.domain.bo;

import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.util.RecordTrainingEntry;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Business Object for Record table
 */
public class Record {
    private float probability;

    public Record() {
        this.recordAccounts = new HashSet<>(0);
    }

    private int id;
    private Date entryDate;
    private String documentNo;
    private byte[] document;
    private String entryText;
    private Set<AccountRecord> recordAccounts;

    /**
     * @param entry  the account-record relation entry to be used for training instances
     */
    public void addRecordTrainingEntry(RecordTrainingEntry entry) {
        AccountDaoImpl accountDao = new AccountDaoImpl();
        List<Account> accountList = accountDao.getAll();
        for (Map.Entry<String, Double> mapEntry : entry.getDebitAccounts().entrySet()) {
            try {
                AccountRecord record = new AccountRecord();
                record.setIsDebit(true);
                for (Account a : accountList) {
                    if (mapEntry.getKey().equals(a.getName())) {
                        record.setAccount(a);
                        break;
                    }
                }
                record.setBruttoValue(mapEntry.getValue());
                this.getRecordAccounts().add(record);
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to parse account from string, skipping..");
            }
        }

        for (Map.Entry<String, Double> mapEntry : entry.getCreditAccounts().entrySet()) {
            try {
                AccountRecord record = new AccountRecord();
                record.setIsDebit(false);
                for (Account a : accountList) {
                    if (mapEntry.getKey().equals(a.getName())) {
                        record.setAccount(a);
                        break;
                    }
                }
                record.setAccount(accountDao.getByName(mapEntry.getKey()));
                record.setBruttoValue(mapEntry.getValue());
                this.getRecordAccounts().add(record);
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to parse account from string, skipping..");
            }
        }
    }

    /**
     * @return  returns a set of account-record relations
     */
    public Set<AccountRecord> getRecordAccounts() {
        return recordAccounts;
    }

    /**
     * @param recordAccounts  the set of account-record relations to be stored
     */
    public void setRecordAccounts(Set<AccountRecord> recordAccounts) {
        this.recordAccounts = recordAccounts;
    }

    /**
     * @return  the id of this object stored in the database table
     */
    public int getId() {
        return id;
    }

    /**
     * Do not use this method. It is used by Hibernate internally
     * @param id  the id for this object to be stored in the database table
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return //TODO: whats entrydate?
     */
    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    /**
     * @return  the identification number of the document
     */
    public String getDocumentNo() {
        return documentNo;
    }

    /**
     * @param documentNo  the identification number of the document
     */
    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    /**
     * @return  the scanned document stored as a byte[]
     */
    public byte[] getDocument() {
        return document;
    }

    /**
     * @param document  the scanned document stored as a byte[]
     */
    public void setDocument(byte[] document) {
        this.document = document;
    }

    /**
     * @return  the text of this position
     */
    public String getEntryText() {
        return entryText;
    }

    /**
     * @param entryText  the position string
     */
    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public float getProbability() {
        return probability;
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
