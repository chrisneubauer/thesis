package de.cneubauer.domain.bo;

import java.sql.Date;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 25.10.2016.
 * CorporateForm BO
 */
public class CorporateForm {

    private int id;
    private String name;
    private String shortName;
    private Set<Country> usedInCountries;
    private Date createdDate;
    private Date modifiedDate;

    /**
     * @return  the id of the object stored in the database
     */
    public int getId() {
        return id;
    }

    /**
     * Do not use this method. It is used by Hibernate internally
     * @param id  the id of the object to be stored in the database
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The name of this corporate form as a whole
     * @return  the name of the corporate form
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the (full) name for the corporate form
     * @param name  the name of the corporate form
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return  the abbreviation of the current corporate form
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName  the abbreviation used for this corporate form
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return a set of countries where this corporate form is known to be used in
     */
    public Set<Country> getUsedInCountries() {
        return usedInCountries;
    }

    /**
     * Can be used to extend (or change) the countries where this corporate form is known to be used in
     * @param usedInCountries  the new set of countries where this corporate form is known to be used in
     */
    public void setUsedInCountries(Set<Country> usedInCountries) {
        this.usedInCountries = usedInCountries;
    }

    /**
     * @return  the date when this object has been created
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate  the date when this object has been created
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return  the date when this object has been modified the last time
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }

    /**
     * @param modifiedDate  the last modification date for this object
     */
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
