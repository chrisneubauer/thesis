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

    /**
     * @return  the id of the stored account
     */
    public int getId() {
        return id;
    }

    /**
     * Do not use this method. It is used by hibernate internally
     * @param id  the id of the stored account
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * A 4 digit number which exactly identifies the account
     * @return  a String containing 4 numbers
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * Sets the account number for the current account object
     * @param accountNo  the account number to identify the acount
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    /**
     * Returns the (long) name of the account
     * @return  the name of the current account object
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the (long) name of the account
     * @param name  the new name for the current account object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the type of the account
     * There are five different Types of accounts:
     * <p><ul>
     *     <li>"Aktivkonto"</li>
     *     <li>"Passivkonto"</li>
     *     <li>"Aufwandskonto"</li>
     *     <li>"Ertragskonto"</li>
     *     <li>"Statistikkonto"</li>
     * </ul></p>
     * @return  the type of the current account
     */
    public AccountType getType() {
        return type;
    }

    /**
     * Sets the type of the account
     * There are five different Types of accounts:
     * <p><ul>
     *     <li>"Aktivkonto"</li>
     *     <li>"Passivkonto"</li>
     *     <li>"Aufwandskonto"</li>
     *     <li>"Ertragskonto"</li>
     *     <li>"Statistikkonto"</li>
     * </ul></p>
     * @param type  the new type for the current account object
     */
    public void setType(AccountType type) {
        this.type = type;
    }

    /**
     * Combines account number and account name in the given form: number - name
     * @return  the combination of account number and name as a string
     */
    @Override
    public String toString() {
        return this.getAccountNo() + " - " + this.getName();
    }
}
