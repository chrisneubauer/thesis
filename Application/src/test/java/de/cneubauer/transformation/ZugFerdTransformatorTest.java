package de.cneubauer.transformation;

import de.cneubauer.AbstractTest;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.dao.InvoiceDao;
import de.cneubauer.domain.dao.impl.InvoiceDaoImpl;
import io.konik.zugferd.Invoice;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Christoph Neubauer on 21.09.2016.
 *
 */
public class ZugFerdTransformatorTest extends AbstractTest {
    private ZugFerdTransformator transformator;

    @org.junit.Before
    public void setUp() throws Exception {
        super.setUp();
        transformator = new ZugFerdTransformator();
    }

    @org.junit.After
    public void tearDown() throws Exception {
        transformator = null;
    }

    @org.junit.Test
    public void testCreatingInvoice() throws Exception {
        System.out.println("Testing simple invoice creation process...");

        Invoice inv = transformator.createMockInvoice();

        Assert.assertNotNull(inv);
        Assert.assertEquals("20131122-42", inv.getHeader().getInvoiceNumber());
        Assert.assertEquals(inv.getTrade().getAgreement().getBuyer().getName(), "Buyer Inc.");
        Assert.assertEquals(inv.getTrade().getAgreement().getSeller().getName(), "Seller Inc.");
    }

    @Test
    public void fullInvoiceExtractionTest() {
        Invoice generatedInvoice = transformator.createMockInvoice();
        try {
            String filepath = "../../../../../target/test-classes/invoice/2015-11-26_Reifen Ebay.pdf";
            transformator.addMockInvoiceToPDF(filepath, "mockTest");
        } catch (Exception e) {
            throw new AssertionFailedError("Unable to add invoice to pdf");
        }
        Invoice result = transformator.extractInvoiceFromMockPdf("mockTest");
        transformator.validateMockInvoiceFromPdf("mockTest");

        Assert.assertEquals(result.getHeader().getInvoiceNumber(), generatedInvoice.getHeader().getInvoiceNumber());
        Assert.assertEquals(result.getHeader().getCode(), generatedInvoice.getHeader().getCode());
        Assert.assertEquals(result.getHeader().getName(), generatedInvoice.getHeader().getName());
        Assert.assertEquals(result.getTrade().getAgreement().getSeller().getName(), generatedInvoice.getTrade().getAgreement().getSeller().getName());
    }

    @Test
    public void createFullConformalBasicInvoiceTest() {
        de.cneubauer.domain.bo.Invoice i = this.createInvoiceForTesting();

        Invoice valid = transformator.createFullConformalBasicInvoice(i);
        Assert.assertNotNull(valid);
        Assert.assertEquals(i.getDebitor().getName(), valid.getTrade().getAgreement().getBuyer().getName());
        Assert.assertEquals(i.getCreditor().getName(), valid.getTrade().getAgreement().getSeller().getName());
    }

    private de.cneubauer.domain.bo.Invoice createInvoiceForTesting() {
        de.cneubauer.domain.bo.Invoice i = new de.cneubauer.domain.bo.Invoice();
        i.setInvoiceNumber(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE) + "_" + ((int) (Math.random()*10)));
        i.setIssueDate(Timestamp.valueOf(LocalDateTime.now()));
        i.setDeliveryDate(Timestamp.valueOf(LocalDateTime.now()));

        LegalPerson creditor = new LegalPerson();
        creditor.setName("Kreditor");
        LegalPerson debitor = new LegalPerson();
        debitor.setName("Debitor");

        i.setCreditor(creditor);
        i.setDebitor(debitor);
        i.setLineTotal(100);
        i.setChargeTotal(0);
        i.setAllowanceTotal(0);
        i.setTaxBasisTotal(100);
        i.setTaxTotal(19);
        i.setGrandTotal(119);
        i.setHasSkonto(false);

        return i;
    }
}
