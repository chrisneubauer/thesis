package de.cneubauer.util;

import de.cneubauer.domain.bo.DocumentCase;
import de.cneubauer.util.enumeration.CaseKey;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 15.02.2017.
 */
//TODO: MOVE TO Model
public class DocumentCaseSet {
    private DocumentCase invoiceNoCase;
    private DocumentCase invoiceDateCase;
    private DocumentCase buyerCase;
    private DocumentCase documentTypeCase;
    private DocumentCase sellerCase;
    private List<DocumentCase> positionCases;

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

    public void setDocumentTypeCase(DocumentCase documentTypeCase) {
        this.documentTypeCase = documentTypeCase;
    }

    public void setSellerCase(DocumentCase sellerCase) {
        this.sellerCase = sellerCase;
    }

    public DocumentCase getDocumentTypeCase() {
        return documentTypeCase;
    }

    public DocumentCase getSellerCase() {
        return sellerCase;
    }

    public List<DocumentCase> getPositionCases() {
        if (positionCases == null) {
            positionCases = new LinkedList<>();
        }
        return positionCases;
    }

    public void addPositionCase(DocumentCase documentCase) {
        if (documentCase.getKeyword().getId() == CaseKey.POSITION) {
            this.getPositionCases().add(documentCase);
        }
    }

    public void setPositionCases(List<DocumentCase> positionCases) {
        this.positionCases = positionCases;
    }
}
