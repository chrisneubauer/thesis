package de.cneubauer.domain.helper;

/**
 * Created by Christoph Neubauer on 20.10.2016.
 * Helper can be filled with values and enables calculations
 */
public class InvoiceInformationHelper {
    private double lineTotal;
    private double chargeTotal;
    private double allowanceTotal;
    private double taxBasisTotal;
    private double taxTotal;
    private double grandTotal;
    //private boolean hasSkonto;
    //private double skonto;

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
}
