package de.cneubauer.domain.bo;

import java.sql.Date;

/**
 * Created by Christoph Neubauer on 25.10.2016.
 * Business Object for table Address
 */
public class Address {
    private int id;
    private String street;
    private int zipCode;
    private String city;
    private Country country;
    private Date createdDate;
    private Date modifiedDate;

    /**
     * Returns the id of the address stored in the database table
     * @return  the id of the object in the database table
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
     * The name of the street where this address belongs to
     * @return  a String containing the street of the address
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the name of the stret where this address should belong to
     * @param street  a String containing the street name
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Returns the zip code of the city of this address
     * @return  the int value containing the zip code of the given city
     */
    public int getZipCode() {
        return zipCode;
    }

    /**
     * Sets the zip code for this address
     * @param zipCode  the zip code of a city as an int value
     */
    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Returns the city name where this address belongs to
     * @return  the String containing the city of this address
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city name where this address should belong to
     * @param city  the string of the city name for this address
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the country object for this address
     * @return  the country object of this address
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Sets the country for this address
     * @param country  the country object for this address
     */
    public void setCountry(Country country) {
        this.country = country;
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
     * @return the date of the last modification made to this object
     */
    public Date getModifiedDate() {
        return modifiedDate;
    }

    /**
     * @param modifiedDate  the date of the last modification made to this object
     */
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

}
