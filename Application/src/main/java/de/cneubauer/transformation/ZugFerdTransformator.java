package de.cneubauer.transformation;

import io.konik.PdfHandler;
import io.konik.validation.InvoiceValidator;
import io.konik.zugferd.Invoice;
import io.konik.zugferd.entity.Context;
import io.konik.zugferd.entity.Header;
import io.konik.zugferd.entity.TradeParty;
import io.konik.zugferd.entity.trade.*;
import io.konik.zugferd.entity.trade.item.Item;
import io.konik.zugferd.profile.Profile;
import io.konik.zugferd.profile.ProfileVersion;
import io.konik.zugferd.unqualified.Amount;
import io.konik.zugferd.unqualified.ZfDateDay;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.validation.ConstraintViolation;
import java.io.*;
import java.math.BigDecimal;
import java.util.Set;

import static com.neovisionaries.i18n.CurrencyCode.EUR;
import static io.konik.zugferd.profile.ConformanceLevel.BASIC;
import static io.konik.zugferd.unece.codes.DocumentCode._380;

/**
 * Created by Christoph Neubauer on 21.09.2016.
 * Basic transformation class that provides methods to convert invoice information to zugferd
 */
public class ZugFerdTransformator {
    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Writes a new pdf file with additional invoice information
     * @param pdf  the original scanned file
     * @param i  the invoice information that should be added to the file
     * @return  the new file with additional invoice information
     */
    byte[] appendInvoiceToPdf(byte[] pdf, Invoice i) {
        ByteArrayOutputStream outPdf = new ByteArrayOutputStream();
        InputStream reader = new ByteArrayInputStream(pdf);

        PdfHandler pdfHandler = new PdfHandler();
        pdfHandler.appendInvoice(i, reader, outPdf);
        return outPdf.toByteArray();
    }

    /**
     * @param i  the invoice to be checked
     * @return  true if the invoice is valid, false if otherwise
     */
    private boolean isInvoiceValid(Invoice i) {
        InvoiceValidator invoiceValidator = new InvoiceValidator();

        //execute
        Set<ConstraintViolation<Invoice>> violations = invoiceValidator.validate(i);

        for (ConstraintViolation<Invoice> violation : violations) {
            logger.log(Level.INFO, violation.getMessage() + " at: " + violation.getPropertyPath() );
        }
        //verify
        System.out.println("Violations: " + violations.size());
        return violations.size() < 1;
    }

    /**
     * Creates a complete ZUGFeRD invoice of basic level
     * @param inv  the invoice that should be converted
     * @return  the valid ZUGFeRD invoice or null if violations of the format occured
     */
    public Invoice createFullConformalBasicInvoice(de.cneubauer.domain.bo.Invoice inv) {
        Invoice i = new Invoice(BASIC);

        Context con = new Context(BASIC);
        Profile guideline = new Profile(BASIC);
        guideline.setVersion(ProfileVersion.V1P0);
        con.setGuideline(guideline);

        Header h = new Header();
        h.setName("RECHNUNG");
        h.setInvoiceNumber(inv.getInvoiceNumber());
        h.setCode(_380);
        h.setIssued(new ZfDateDay(inv.getIssueDate().getTime()));

        Agreement a = new Agreement();
        a.setBuyer(new TradeParty().setName(inv.getDebitor().toString()));
        a.setSeller(new TradeParty().setName(inv.getCreditor().toString()));

        MonetarySummation sum = new MonetarySummation();
        sum.setLineTotal(new Amount(BigDecimal.valueOf(inv.getLineTotal()), EUR));
        sum.setChargeTotal(new Amount(BigDecimal.valueOf(inv.getChargeTotal()), EUR));
        sum.setAllowanceTotal(new Amount(BigDecimal.valueOf(inv.getAllowanceTotal()), EUR));
        sum.setTaxBasisTotal(new Amount(BigDecimal.valueOf(inv.getTaxBasisTotal()), EUR));
        sum.setTaxTotal(new Amount(BigDecimal.valueOf(inv.getTaxTotal()), EUR));
        sum.setGrandTotal(new Amount(BigDecimal.valueOf(inv.getGrandTotal()), EUR));

        Settlement s = new Settlement();
        s.setCurrency(EUR);
        s.setMonetarySummation(sum);

        Delivery d;
        if (inv.getDeliveryDate() == null) {
            d = new Delivery(new ZfDateDay(inv.getIssueDate().getTime()));
        } else {
            d = new Delivery(new ZfDateDay(inv.getDeliveryDate().getTime()));
        }

        Trade tr = new Trade();
        Item item = new Item();
        tr.addItem(item);
        tr.setAgreement(a);
        tr.setDelivery(d);
        tr.setSettlement(s);

        i.setContext(con);
        i.setHeader(h);
        i.setTrade(tr);

        if (this.isInvoiceValid(i)) {
            return i;
        } else {
            return null;
        }
    }

    boolean pdfIsZugFerdConform(byte[] pdf) {
        PdfHandler handler = new PdfHandler();
        Invoice i = handler.extractInvoice(new ByteArrayInputStream(pdf));
        return this.isInvoiceValid(i);
    }

    public byte[] appendInvoiceToPDF(byte[] originalPdf, de.cneubauer.domain.bo.Invoice i) throws IOException {
        io.konik.zugferd.Invoice konikInvoice = this.createFullConformalBasicInvoice(i);
        return this.appendInvoiceToPdf(originalPdf, konikInvoice);
    }

    Invoice getInvoiceFromPdf(InputStream pdf) {
        PdfHandler handler = new PdfHandler();
        return handler.extractInvoice(pdf);
    }
}
