package de.cneubauer.domain.bo;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 * Keyword BO
 */
public class Keyword {
    private int id;
    private String name;

    /**
     * Returns the id of the Keyword object stored in the database table
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
     * Returns the name of the Keyword
     * @return human-readable string to recognize the Keyword
     */
    public String getName() {
        return name;
    }

    /**
     * Defines a name for the Keyword
     * @param name the name the keyword should have
     */
    public void setName(String name) {
        this.name = name;
    }
}
