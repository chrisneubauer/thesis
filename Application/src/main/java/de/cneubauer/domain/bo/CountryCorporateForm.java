package de.cneubauer.domain.bo;

import java.sql.Timestamp;

/**
 * Created by Christoph Neubauer on 26.10.2016.
 */
public class CountryCorporateForm {
    //private int id;
    private Country country;
    private CorporateForm corporateForm;

    /*public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public CorporateForm getCorporateForm() {
        return corporateForm;
    }

    public void setCorporateForm(CorporateForm corporateForm) {
        this.corporateForm = corporateForm;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    private Timestamp createdDate;
    private Timestamp modifiedDate;
}
