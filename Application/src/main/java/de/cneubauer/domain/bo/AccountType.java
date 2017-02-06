package de.cneubauer.domain.bo;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Business Object for table AccountType
 */
public class AccountType {
    private int id;
    private int type;
    private String name;

    /**
     * Returns the id of the account type stored in the database
     * @return  the id for the database table
     */
    public int getId() {
        return id;
    }

    /**
     * Do not use this method. It is used by hibernate internally
     * @param id  the id that should be stored for the database table
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the type for the account as an int value
     * <p><ul>
     *     <li>1: Aktivkonto</li>
     *     <li>2: Passivkonto</li>
     *     <li>3: Aufwandskonto</li>
     *     <li>4: Ertragskonto</li>
     *     <li>5: Statistikkonto</li>
     * </ul></p>
     * @return  the type as an int value between 1 and 5
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type for the account as an int value
     * <p><ul>
     *     <li>1: Aktivkonto</li>
     *     <li>2: Passivkonto</li>
     *     <li>3: Aufwandskonto</li>
     *     <li>4: Ertragskonto</li>
     *     <li>5: Statistikkonto</li>
     * </ul></p>
     * @param type  the type to be set as a value between 1 and 5
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Returns the name of the type
     * @return  the name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the type
     * @param name  the name as a String
     */
    public void setName(String name) {
        this.name = name;
    }
}
