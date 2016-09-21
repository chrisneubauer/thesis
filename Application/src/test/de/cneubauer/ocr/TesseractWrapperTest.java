package de.cneubauer.ocr;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.lang3.StringUtils;

import static org.junit.Assert.*;

public class TesseractWrapperTest {
    private TesseractWrapper wrapper;

    @Before
    public void setUp() throws Exception {
        wrapper = new TesseractWrapper();
    }

    @After
    public void tearDown() throws Exception {
        wrapper = null;
    }

    @Test
    public void testInitOcr() throws Exception {
        String path = ".\\src\\test\\resources\\text\\eurotext.tif";
        String result = wrapper.initOcr(path);
        if (result != null) {
            System.out.println(result);
            Assert.assertTrue(result.length() > 0);
        }
    }

    @Test
    public void testInitOcrWithPng() throws Exception {
        String path = ".\\src\\test\\resources\\text\\wa2.png";
        String result = wrapper.initOcr(path);
        if (result != null) {
            System.out.println(result);
            Assert.assertTrue(result.length() > 0);
        }
    }

    @Test
    public void testInitOcrWithPdf() throws Exception {
        String path = ".\\src\\test\\resources\\invoice\\2015-11-26_Reifen Ebay.pdf";
        String result = wrapper.initOcr(path);
        if (result != null) {
            System.out.println(result);
            Assert.assertTrue(result.length() > 0);
            Assert.assertTrue(result.contains("mein-reifen-outlet"));
            Assert.assertTrue(result.contains("26. Nov. 2015"));
        }
    }

    @Test
    public void testInitOcrWithPdf2() throws Exception {
        String path = ".\\src\\test\\resources\\invoice\\2015-07-10 Rechnung Yamaha YDP 142.pdf";
        String result = wrapper.initOcr(path);
        if (result != null) {
            System.out.println(result);
            Assert.assertTrue(result.length() > 0);
            Assert.assertTrue(result.contains("Musik Klier"));
            Assert.assertTrue(result.contains("Christoph Neubauer"));
            Assert.assertTrue(result.contains("10.07.15"));
        }
    }

    @Test
    public void testInitOcrWithPdf3() throws Exception {
        String path = ".\\src\\test\\resources\\invoice\\2015-05-15 Rechnung Ersatzteil Dachfenster.pdf";
        String result = wrapper.initOcr(path);
        if (result != null) {
            System.out.println(result);
            Assert.assertTrue(result.length() > 0);
        }
    }

    @Test
    public void testInitOcrWithPdf3Processed() throws Exception {
        String path = ".\\src\\test\\resources\\invoice\\2015-05-15 Rechnung Ersatzteil Dachfenster_Processed.pdf";
        String result = wrapper.initOcr(path);
        if (result != null) {
            System.out.println(result);
            Assert.assertTrue(result.length() > 0);
        }
    }

    @Test
    public void testOCRKeywordBruttoSum() throws Exception {
        String path1 = ".\\src\\test\\resources\\invoice\\KeywordSuccess\\Bruttosumme\\Scan_20160822_161042_012.jpg";
        String path2 = ".\\src\\test\\resources\\invoice\\KeywordSuccess\\Bruttosumme\\Scan_20160822_161042_019.jpg";
        String path3 = ".\\src\\test\\resources\\invoice\\KeywordSuccess\\Bruttosumme\\Scan_20160822_163306_001.jpg";
        String path4 = ".\\src\\test\\resources\\invoice\\KeywordSuccess\\Bruttosumme\\Scan_20160822_163306_005.jpg";
        String path5 = ".\\src\\test\\resources\\invoice\\KeywordSuccess\\Bruttosumme\\Scan_20160822_163306_007.jpg";
        //String result1 = wrapper.initOcr(path1);
        //String result2 = wrapper.initOcr(path2);
        String result3 = wrapper.initOcr(path3);
        String result4 = wrapper.initOcr(path4);
        String result5 = wrapper.initOcr(path5);

        /*if (result1 != null) {
            Assert.assertTrue(result1.contains("Bruttosumme"));
        }
        if (result2 != null) {
            Assert.assertTrue(result2.contains("Bruttosumme"));
        }*/
        /*if (result3 != null) {
            System.out.println(result3);
            boolean found = assertOCR(result3, "Bruttosumme");
            Assert.assertTrue(found);
        }
        if (result4 != null) {
            boolean found = assertOCR(result4, "Bruttosumme");
            Assert.assertTrue(found);
        }*/
        if (result5 != null) {
            System.out.println(result5);
            boolean found = assertOCR(result5, "Bruttosumme");
            Assert.assertTrue(found);
        }
    }

    private boolean assertOCR(String ocrResult, String stringToCheck) {
        boolean found = false;
        String result = ocrResult.replace("\n", "");
        for (String s : result.split(" ")) {
            if (this.compareStrings(s, stringToCheck) > 0.8) {
                found = true;
                break;

            }
        }
        return found;
    }

    private double compareStrings(String stringA, String stringB) {
        return StringUtils.getJaroWinklerDistance(stringA, stringB);
    }
}