package de.cneubauer.domain.bo;

/**
 * Created by Christoph Neubauer on 08.01.2017.
 * Many-To-Many mapping of accounts to records
 */
public class AccountRecord {
    private int id;
    private double bruttoValue;
    private Account account;
    private Record record;
    private boolean isDebit;

    /**
     * Gives information if an account-record relation is debit or credit related
     * @return  true if it is debit, false if it is credit
     */
    public boolean getIsDebit() {
        return isDebit;
    }

    /**
     * Set information if this account-record relation is debit or credit related
     * @param isDebit  true if it is debit, false if it is credit
     */
    public void setIsDebit(boolean isDebit) {
        this.isDebit = isDebit;
    }

    /**
     * Gets the record which is related to this account-record relation
     * @return  the record of this relation
     */
    public Record getRecord() {
        return record;
    }

    /**
     * Sets the record which is related to this account-record relation
     * @param record  the record for this relation
     */
    public void setRecord(Record record) {
        this.record = record;
    }

    /**
     * Returns the account which is related to this account-record relation
     * @return  the account of this relation
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account which is related to this account-record relation
     * @param account  the account for this relation
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Returns the id of this account-record relation stored in the database table
     * @return  the id of the relation in the database
     */
    public int getId() {
        return id;
    }

    /**
     * Do not use this method. It is used by hibernate internally
     * @param id  the id for the relation in the database
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the value which is stored for the given account-record relation
     * @return  the value as double
     */
    public double getBruttoValue() {
        return bruttoValue;
    }

    /**
     * Sets the value for the given account-record relation
     * @param bruttoValue  the value which should be stored as double
     */
    public void setBruttoValue(double bruttoValue) {
        this.bruttoValue = bruttoValue;
    }
}
