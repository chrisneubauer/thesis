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
}
