package de.cneubauer.transformation;

import io.konik.zugferd.Invoice;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

/**
 * Created by Christoph Neubauer on 21.09.2016.
 *
 */
public class ZugFerdTransformatorTest {
    private ZugFerdTransformator transformator;

    @org.junit.Before
    public void setUp() throws Exception {
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
}
