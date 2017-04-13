package de.cneubauer.domain.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 13.04.2017.
 */
public class InvoiceExtractorServiceTest {
    private InvoiceExtractorService service;
    @Before
    public void setUp() throws Exception {
        String[] parts = new String[4];
        service = new InvoiceExtractorService(null, parts);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDateExtraction() {
        this.service.leftHeader = "This is a test from today.";
        this.service.rightHeader = " We have the 23.10.2017. I would like to see that date! And another one: 17.12.2010";
        LocalDate date = this.service.findDateInformation();
        Assert.isTrue(date != null);
    }
}