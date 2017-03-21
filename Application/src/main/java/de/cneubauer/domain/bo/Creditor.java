package de.cneubauer.domain.bo;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 * Creditor BO
 */
public class Creditor {
    private int id;
    private String name;
    private LegalPerson legalPerson;

    /**
     * Returns the id of the creditor object stored in the database table
     * @return  the id of the object in the database
     */
    public int getId() {
        return id;
    }

    /**
     * Do not use this method. It is used by Hibernate internally
     * @param id  the id for the object in the database table
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the stored name information of the creditor
     * @return the name of the creditor
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the creditor
     * @param name the name the creditor should get
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The linked LegalPerson object of this creditor
     * @return the creditor as a LegalPerson
     */
    public LegalPerson getLegalPerson() {
        return legalPerson;
    }

    /**
     * Relates the creditor object to a LegalPerson object
     * @param legalPerson the LegalPerson that is the creditor
     */
    public void setLegalPerson(LegalPerson legalPerson) {
        this.legalPerson = legalPerson;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
