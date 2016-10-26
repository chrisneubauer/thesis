package de.cneubauer.scanner;

import org.junit.Assert;

public class PDFScannerTest {
    private PDFScanner scanner;

    @org.junit.Before
    public void setUp() throws Exception {
        scanner = new PDFScanner();
    }

    @org.junit.After
    public void tearDown() throws Exception {
        scanner = null;
    }

    @org.junit.Test
    public void testReadPdf() throws Exception {
        System.out.println("Testing simple pdf reading process...");
        String path = ".\\src\\test\\resources\\invoice\\2015-05-15 Rechnung Ersatzteil Dachfenster.pdf";
        String result = scanner.readPdf(path);
        System.out.println(result.length());
        System.out.println(result);

        Assert.assertNotNull(result);
    }

    @org.junit.Test
    public void testReadPdf2() throws Exception {
        System.out.println("Testing simple pdf reading process...");
        String path = ".\\src\\test\\resources\\invoice\\2015-07-10 Rechnung Yamaha YDP 142.pdf";
        String result = scanner.readPdf(path);
        System.out.println(result.length());
        System.out.println(result);

        Assert.assertNotNull(result);
    }

    @org.junit.Test
    public void testReadPdf3() throws Exception {
        System.out.println("Testing simple pdf reading process...");
        String path = ".\\src\\test\\resources\\invoice\\2015-11-26_Reifen Ebay.pdf";
        String result = scanner.readPdf(path);
        System.out.println(result.length());
        System.out.println(result);

        Assert.assertNotNull(result);
    }
}