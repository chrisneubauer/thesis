package de.cneubauer.domain.bo;

import java.sql.Date;

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
    private Date createdDate;
    private Date modifiedDate;

    /**
     * Default constructor for the LegalPerson class
     */
    public LegalPerson() {}

    /**
     * Constructor for the LegalPerson class
     * @param name  the name of the legal person. Can contain corporate form information
     */
    public LegalPerson(String name) {
        /*if (name.contains(" ")) {
            if (this.checkCorporateForm(name)) {
                this.setIsCompany(true);
                this.setCompanyName(name.split(" ")[0]);
                //this.setCorporateForm(name.split(" ")[1]);
            } else {
                this.setFirstName(name.split(" ")[0]);
                this.setName(name.split(" ")[1]);
            }
        } else {
            this.setName(name);
        }*/
        this.setName(name);
        if (this.checkCorporateForm(name)) {
            this.setIsCompany(true);
        }
    }

    /**
     * Checks if it is a company
     * If not, first name (if existent) + last name is returned
     * If it is, the company name + the corporate form (if existent) is returned
     * @return  the name of the legal person
     */
    @Override
    public String toString() {
        String result;
        if (this.getIsCompany() && this.getCompanyName() != null) {
            result = this.getCompanyName();
            if (this.getCorporateForm() != null) {
                result += " " + this.getCorporateForm().getShortName();
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

    /**
     * Checks the given String for indications for corporate information
     * @param name  the name which should be checked
     * @return  true if the name contains company abbreviations (such as "AG"), false if otherwise
     */
    private boolean checkCorporateForm(String name) {
        return name.contains("AG") || name.contains("OHG") || name.contains("GmbH") || name.contains("GbR") || name.contains("S.A.") || name.contains("KG");
    }

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
     * @return  true if it is a company, false if it is a natural person
     */
    public boolean getIsCompany() {
        return isCompany;
    }

    /**
     * @param isCompany  true if it is a company, false if it is a natural person
     */
    public void setIsCompany(boolean isCompany) {
        this.isCompany = isCompany;
    }

    /**
     * @return  the name of the company, without corporate form information
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     *
     * @param companyName  the name of the company, without corporate form information
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * @return  the (last) name of the legal person
     */
    public String getName() {
        return name;
    }

    /**
     * @param name  the (last) name of the legal person
     */
    public void setName(String name) {
        this.name = name;
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

    /**
     * @return  the first name of the natural person, null if it is a company
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName  the first name of the natural person, set to null if it is a company
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return  the corporate form of the given company / legal person (null for natural persons)
     */
    public CorporateForm getCorporateForm() {
        return corporateForm;
    }

    /**
     * @param corporateForm  the corporate form of the given company (set to null for a natural persons)
     */
    public void setCorporateForm(CorporateForm corporateForm) {
        this.corporateForm = corporateForm;
    }

    /**
     * @return  the address of the legal person
     */
    public Address getAddress() {
        return address;
    }

    /**
     * @param address  the address of the legal person
     */
    public void setAddress(Address address) {
        this.address = address;
    }
}
