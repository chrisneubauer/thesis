package de.cneubauer.domain.bo;

import java.sql.Date;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 * Business Object for table scan
 */
public class Scan {
    private int id;
    private byte[] file;
    private Invoice invoiceInformation;
    private Date createdDate;
    private Date modifiedDate;
    private Set<Position> positions;

    public Set<Position> getPositions() {
        return positions;
    }

    public void setPositions(Set<Position> positions) {
        this.positions = positions;
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
     * @return  the document file as a byte[]
     */
    public byte[] getFile() {
        return file;
    }

    /**
     * @param file  the document file as a byte[]
     */
    public void setFile(byte[] file) {
        this.file = file;
    }

    /**
     * @return  the related invoice for this document
     */
    public Invoice getInvoiceInformation() {
        return invoiceInformation;
    }

    /**
     * @param invoiceInformation  the related invoice for this document
     */
    public void setInvoiceInformation(Invoice invoiceInformation) {
        this.invoiceInformation = invoiceInformation;
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
