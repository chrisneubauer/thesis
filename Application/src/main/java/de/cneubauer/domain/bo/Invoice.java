package de.cneubauer.domain.bo;

import java.sql.Date;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 * Busines Object for table Invoice
 */
public class Invoice {
    private int id;
    private String invoiceNumber;
    private LegalPerson debitor;
    private LegalPerson creditor;
    private Date issueDate;
    private Date deliveryDate;
    private double lineTotal;
    private double chargeTotal;
    private double allowanceTotal;
    private double taxBasisTotal;
    private double taxTotal;
    private double grandTotal;
    private boolean hasSkonto;
    private double skonto;
    private Date createdDate;
    private Date modifiedDate;
    private boolean revised;

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
     * The invoice usually contains an invoice number used to identify the document
     * @return  the invoice number of the document
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoice number which is used by companies to identify their invoices
     * @param invoiceNumber  the invoice number for this document
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * The legal person which is requested to pay the invoice
     * @return  the legal person known as Debitor
     */
    public LegalPerson getDebitor() {
        return debitor;
    }

    /**
     * Sets the legal person which is requested to pay the invoice
     * It can be a company as well
     * @param debitor  the legal person which is known as Debitor
     */
    public void setDebitor(LegalPerson debitor) {
        this.debitor = debitor;
    }

    /**
     * The legal person which is issuing the invoice
     * @return  the legal person known as Creditor
     */
    public LegalPerson getCreditor() {
        return creditor;
    }

    /**
     * Sets the legal person which is issuing the current invoice document
     * @param creditor  the legal person known as Creditor
     */
    public void setCreditor(LegalPerson creditor) {
        this.creditor = creditor;
    }

    /**
     * The date when this invoice has been issued
     * @return  the date of the invoice
     */
    public Date getIssueDate() {
        return issueDate;
    }

    /**
     * Sets the date when this invoice has been issued
     * @param issueDate  the date of the invoice
     */
    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * Indicates if the invoice has allowed a skonto if payment is made by a certain date
     * @return  true if it has skonto, false if it does not
     */
    public boolean isHasSkonto() {
        return hasSkonto;
    }

    /**
     * Sets the indication if skonto has been allowed
     * @param hasSkonto  true if it has skonto, false if otherwise
     */
    public void setHasSkonto(boolean hasSkonto) {
        this.hasSkonto = hasSkonto;
    }

    /**
     * @return  the percentual skonto
     */
    public double getSkonto() {
        return skonto;
    }

    /**
     * @param skonto  the percentual value of the skonto allowed
     */
    public void setSkonto(double skonto) {
        this.skonto = skonto;
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
     * @return  the date when the goods or services are delivered
     */
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * @param deliveryDate  the date when the goods or services are delivered
     */
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * Also see the technical documentation of the ZUGFeRD format
     * @return  the total value of all positions in this invoice
     */
    public double getLineTotal() {
        return lineTotal;
    }

    /**
     * Also see the technical documentation of the ZUGFeRD format
     * @param lineTotal  the total value of all positions in this invoice
     */
    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }

    /**
     * Also see the technical documentation of the ZUGFeRD format
     * @return  the total amount of all additional costs of this invoice
     */
    public double getChargeTotal() {
        return chargeTotal;
    }

    /**
     * Also see the technical documentation of the ZUGFeRD format
     * @param chargeTotal  the total amount of all additional costs of this invoice
     */
    public void setChargeTotal(double chargeTotal) {
        this.chargeTotal = chargeTotal;
    }

    /**
     * Also see the technical documentation of the ZUGFeRD format
     * @return  the total amount of all discounts for this invoice
     */
    public double getAllowanceTotal() {
        return allowanceTotal;
    }

    /**
     * Also see the technical documentation of the ZUGFeRD format
     * @param allowanceTotal  the total amount of all discounts for this invoice
     */
    public void setAllowanceTotal(double allowanceTotal) {
        this.allowanceTotal = allowanceTotal;
    }

    /**
     * Sum of the net value + additional costs - discounts
     * Also see the technical documentation of the ZUGFeRD format
     * @return  the base value for the tax to be applied on
     */
    public double getTaxBasisTotal() {
        return taxBasisTotal;
    }

    /**
     * Sum of the net value + additional costs - discounts
     * Also see the technical documentation of the ZUGFeRD format
     * @param taxBasisTotal  the base value for the tax to be applied on
     */
    public void setTaxBasisTotal(double taxBasisTotal) {
        this.taxBasisTotal = taxBasisTotal;
    }

    /**
     * Also see the technical documentation of the ZUGFeRD format
     * @return  the total amount of taxes applied on the net value of this invoice
     */
    public double getTaxTotal() {
        return taxTotal;
    }

    /**
     * Also see the technical documentation of the ZUGFeRD format
     * @param taxTotal  the total amount of taxes applied on the net value of this invoice
     */
    public void setTaxTotal(double taxTotal) {
        this.taxTotal = taxTotal;
    }

    /**
     * Also see the technical documentation of the ZUGFeRD format
     * @return  the brutto sum of the values in this invoice
     */
    public double getGrandTotal() {
        return grandTotal;
    }

    /**
     * Also see the technical documentation of the ZUGFeRD format
     * @param grandTotal  the brutto sum of the values for this invoice
     */
    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    /**
     * Indicates if this invoice has been manually revised by the user
     * @return  true if the user has reviewed the invoice, false if otherwise
     */
    public boolean isRevised() {
        return revised;
    }

    /**
     * Indicates if this invoice has been manually revised by the user
     * @param revised  true if the user has reviewed the invoice, false if otherwise
     */
    public void setRevised(boolean revised) {
        this.revised = revised;
    }
}
