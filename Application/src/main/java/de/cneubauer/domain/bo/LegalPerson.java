package de.cneubauer.domain.bo;

import java.sql.Timestamp;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 * Business Object for Table LegalPerson
 */
public class LegalPerson {
    private int id;
    private boolean isCompany;
    private String companyName;
    private String corporateForm;
    private String name;
    private String surName;
    private String street;
    private int zipCode;
    private String city;
    private Timestamp createdDate;
    private Timestamp modifiedDate;

    public LegalPerson() {

    }

    public LegalPerson(String name) {
        if (name.contains(" ")) {
            if (this.checkCorporateForm(name.split(" ")[1])) {
                this.setIsCompany(true);
                this.setCompanyName(name.split(" ")[0]);
                this.setCorporateForm(name.split(" ")[1]);
            } else {
                this.setName(name.split(" ")[0]);
                this.setSurName(name.split(" ")[1]);
            }
        } else {
            this.setSurName(name);
        }
    }

    private boolean checkCorporateForm(String s) {
        return s.contains("AG") || s.contains("OHG") || s.contains("GmbH") || s.contains("GbR") || s.contains("S.A.") || s.contains("KG");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsCompany() {
        return isCompany;
    }

    public void setIsCompany(boolean isCompany) {
        this.isCompany = isCompany;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCorporateForm() {
        return corporateForm;
    }

    public void setCorporateForm(String corporateForm) {
        this.corporateForm = corporateForm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    @Override
    public String toString() {
        String result = "";
        if (isCompany) {
            result = this.getCompanyName();
            if (this.getCorporateForm() != null) {
                result += " " + this.getCorporateForm();
            }
        } else {
            if (this.getName() != null) {
                result = this.getName() + " " + this.getSurName();
            } else {
                result = this.getSurName();
            }
        }
        return result;
    }
}
