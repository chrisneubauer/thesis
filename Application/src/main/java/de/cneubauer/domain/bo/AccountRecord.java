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

    public boolean getIsDebit() {
        return isDebit;
    }

    public void setIsDebit(boolean isDebit) {
        this.isDebit = isDebit;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBruttoValue() {
        return bruttoValue;
    }

    public void setBruttoValue(double bruttoValue) {
        this.bruttoValue = bruttoValue;
    }
}
