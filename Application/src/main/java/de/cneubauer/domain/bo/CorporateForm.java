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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Set<Country> getUsedInCountries() {
        return usedInCountries;
    }

    public void setUsedInCountries(Set<Country> usedInCountries) {
        this.usedInCountries = usedInCountries;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
