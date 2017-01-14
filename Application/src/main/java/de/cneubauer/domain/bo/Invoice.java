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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LegalPerson getDebitor() {
        return debitor;
    }

    public void setDebitor(LegalPerson debitor) {
        this.debitor = debitor;
    }

    public LegalPerson getCreditor() {
        return creditor;
    }

    public void setCreditor(LegalPerson creditor) {
        this.creditor = creditor;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public boolean isHasSkonto() {
        return hasSkonto;
    }

    public void setHasSkonto(boolean hasSkonto) {
        this.hasSkonto = hasSkonto;
    }

    public double getSkonto() {
        return skonto;
    }

    public void setSkonto(double skonto) {
        this.skonto = skonto;
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

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }

    public double getChargeTotal() {
        return chargeTotal;
    }

    public void setChargeTotal(double chargeTotal) {
        this.chargeTotal = chargeTotal;
    }

    public double getAllowanceTotal() {
        return allowanceTotal;
    }

    public void setAllowanceTotal(double allowanceTotal) {
        this.allowanceTotal = allowanceTotal;
    }

    public double getTaxBasisTotal() {
        return taxBasisTotal;
    }

    public void setTaxBasisTotal(double taxBasisTotal) {
        this.taxBasisTotal = taxBasisTotal;
    }

    public double getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(double taxTotal) {
        this.taxTotal = taxTotal;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public boolean isRevised() {
        return revised;
    }

    public void setRevised(boolean revised) {
        this.revised = revised;
    }
/*
    public List<LegalPerson> getParties() {
        return parties;
    }

    public void setParties(List<LegalPerson> parties) {
        this.parties = parties;
    }*/
}
