package de.cneubauer.domain.bo;

import java.sql.Date;

/**
 * Created by Christoph Neubauer on 25.10.2016.
 * Country BO
 */
public class Country {
    private int id;
    private String name;
    private String abbreviation;
    private Date createdDate;
    private Date modifiedDate;

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
     * @return  the name of the country
     */
    public String getName() {
        return name;
    }

    /**
     * @param name  the name for the country
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  the country code of the country (e.g. DE for Germany)
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * @param abbreviation  the country code for the country (e.g. DE for Germany)
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * @return  the date when this object has been created
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate  the date when this object will be created
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return  the last modification date of this object
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }

    /**
     * @param modifiedDate  the date when this object has been modified the last time
     */
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
