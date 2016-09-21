package de.cneubauer.transformation;

import io.konik.zugferd.Invoice;
import org.junit.Assert;

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
}
