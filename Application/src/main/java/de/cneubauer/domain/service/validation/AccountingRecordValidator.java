package de.cneubauer.domain.service.validation;

import de.cneubauer.domain.bo.Invoice;

/**
 * Created by Christoph Neubauer on 02.12.2016.
 * Validates Invoice with minimum information
 */
public class InvoiceValidator {
    private Invoice invoice;
    public InvoiceValidator(Invoice i) {
        this.invoice = i;
    }

    public boolean isValid() {
        return this.validateInvoice();
    }

    private boolean validateInvoice() {
        boolean valid = true;
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
        valid = valid && this.invoice.getChargeTotal() > 0;
        if (this.invoice.isHasSkonto()) {
            valid = valid && this.invoice.getSkonto() > 0;
        }
        return valid;
    }
}
