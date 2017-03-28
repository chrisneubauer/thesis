package de.cneubauer.domain.service.validation;

import de.cneubauer.domain.bo.Invoice;

/**
 * Created by Christoph Neubauer on 02.12.2016.
 * Validates Invoice with minimum information
 */
public class InvoiceValidator {
    private Invoice invoice;

    /**
     * Constructor of the InvoiceValidator class
     * @param i  the invoice that should be validated
     */
    public InvoiceValidator(Invoice i) {
        this.invoice = i;
    }

    /**
     * Validates the invoice
     * @return  true if the invoice is valid, false if otherwise
     */
    public boolean isValid() {
        return this.validateInvoice();
    }

    /**
     * Checks invoice for creditor and debitor information as well as invoice no, issue date and charge
     * @return  true if the invoice is valid, false if otherwise
     */
    private boolean validateInvoice() {
        boolean valid;
        if (this.invoice.getCreditor() != null) {
            valid = this.invoice.getCreditor().getName() != null;
        } else {
            return false;
        }
        if (this.invoice.getDebitor() != null) {
            valid = valid && this.invoice.getDebitor().getName() != null;
        } else {
            return false;
        }
        valid = valid && this.invoice.getInvoiceNumber() != null;
        valid = valid && this.invoice.getIssueDate() != null;
        valid = valid && this.invoice.getGrandTotal() > 0;
        if (this.invoice.isHasSkonto()) {
            valid = valid && this.invoice.getSkonto() > 0;
        }
        return valid;
    }
}
