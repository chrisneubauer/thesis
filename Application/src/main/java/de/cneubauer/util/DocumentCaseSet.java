package de.cneubauer.util;

import de.cneubauer.domain.bo.DocumentCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 */
public class DocumentCaseSet {
    private DocumentCase invoiceNoCase;
    private DocumentCase invoiceDateCase;
    private DocumentCase buyerCase;

    public DocumentCase getInvoiceNoCase() {
        return invoiceNoCase;
    }

    public void setInvoiceNoCase(DocumentCase invoiceNoCase) {
        this.invoiceNoCase = invoiceNoCase;
    }

    public DocumentCase getInvoiceDateCase() {
        return invoiceDateCase;
    }

    public void setInvoiceDateCase(DocumentCase invoiceDateCase) {
        this.invoiceDateCase = invoiceDateCase;
    }

    public DocumentCase getBuyerCase() {
        return buyerCase;
    }

    public void setBuyerCase(DocumentCase buyerCase) {
        this.buyerCase = buyerCase;
    }

    public List<DocumentCase> getCases() {
        List<DocumentCase> cases = new ArrayList<>(3);
        cases.add(invoiceNoCase);
        cases.add(invoiceDateCase);
        cases.add(buyerCase);
        return cases;
    }
}