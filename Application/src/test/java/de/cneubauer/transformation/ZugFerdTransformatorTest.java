package de.cneubauer.transformation;

import com.google.common.io.Files;
import de.cneubauer.AbstractTest;
import de.cneubauer.domain.bo.LegalPerson;
import io.konik.zugferd.Invoice;
import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Christoph Neubauer on 21.09.2016.
 *
 */
public class ZugFerdTransformatorTest extends AbstractTest {
    private ZugFerdTransformator transformator;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        transformator = new ZugFerdTransformator();
    }

    @After
    public void tearDown() throws Exception {
        transformator = null;
    }

    @Test
    public void fullInvoiceExtractionTest() {
        de.cneubauer.domain.bo.Invoice i = this.createInvoiceForTesting();
        Invoice generatedInvoice = transformator.createFullConformalBasicInvoice(i);
        byte[] output;
        try {
            String filepath = "../../../../../target/test-classes/data/generation/template1_geerated0.pdf";
            output = transformator.appendInvoiceToPDF(Files.toByteArray(new File(filepath)), i);
        } catch (Exception e) {
            throw new AssertionFailedError("Unable to add invoice to pdf");
        }
        InputStream outFileStream = new ByteArrayInputStream(output);
        Invoice result = transformator.getInvoiceFromPdf(outFileStream);

        transformator.pdfIsZugFerdConform(output);

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

    @Test
    public void testValidPDF() {
        boolean result = false;
        try {
            byte[] pdf = Files.toByteArray(new File("C:\\Users\\Christoph\\Desktop\\appendedInvoice.pdf"));
            Invoice mock = transformator.createFullConformalBasicInvoice(this.createInvoiceForTesting());
            byte[] output = transformator.appendInvoiceToPdf(pdf, mock);
            result = transformator.pdfIsZugFerdConform(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(result);
    }

    private de.cneubauer.domain.bo.Invoice createInvoiceForTesting() {
        de.cneubauer.domain.bo.Invoice i = new de.cneubauer.domain.bo.Invoice();
        i.setInvoiceNumber(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE) + "_" + ((int) (Math.random()*10)));
        i.setIssueDate(Date.valueOf(LocalDate.now()));
        i.setDeliveryDate(Date.valueOf(LocalDate.now()));

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
