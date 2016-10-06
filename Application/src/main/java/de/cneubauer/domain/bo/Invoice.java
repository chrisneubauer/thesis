package de.cneubauer.domain.bo;

import java.sql.Timestamp;

/**
 * Created by Christoph Neubauer on 05.10.2016.
 * Busines Object for table Invoice
 */
public class Invoice {
    private int id;
    private LegalPerson debitor;
    private LegalPerson creditor;
    private Timestamp date;
    private double moneyVale;
    private boolean hasSkonto;
    private double skonto;
    private Timestamp createdDate;
    private Timestamp modifiedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public double getMoneyVale() {
        return moneyVale;
    }

    public void setMoneyVale(double moneyVale) {
        this.moneyVale = moneyVale;
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
}
