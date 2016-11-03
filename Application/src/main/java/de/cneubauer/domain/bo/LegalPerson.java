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
    private CorporateForm corporateForm;
    private Address address;
    private String name;
    private String firstName;
    private Timestamp createdDate;
    private Timestamp modifiedDate;

    public LegalPerson() {

    }

    public LegalPerson(String name) {
        if (name.contains(" ")) {
            if (this.checkCorporateForm(name.split(" ")[1])) {
                this.setIsCompany(true);
                this.setCompanyName(name.split(" ")[0]);
                //this.setCorporateForm(name.split(" ")[1]);
            } else {
                this.setFirstName(name.split(" ")[0]);
                this.setName(name.split(" ")[1]);
            }
        } else {
            this.setName(name);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        String result;
        if (isCompany) {
            result = this.getCompanyName();
            if (this.getCorporateForm() != null) {
                result += " " + this.getCorporateForm();
            }
        } else {
            if (this.getFirstName() != null) {
                result = this.getFirstName() + " " + this.getName();
            } else {
                result = this.getName();
            }
        }
        return result;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public CorporateForm getCorporateForm() {
        return corporateForm;
    }

    public void setCorporateForm(CorporateForm corporateForm) {
        this.corporateForm = corporateForm;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
