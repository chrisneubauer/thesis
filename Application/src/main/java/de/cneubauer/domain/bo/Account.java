package de.cneubauer.domain.bo;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Business Object for table Account
 */
public class Account {
    private int id;
    private String accountNo;
    private String name;
    private AccountType type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.getAccountNo() + " - " + this.getName();
    }
}
