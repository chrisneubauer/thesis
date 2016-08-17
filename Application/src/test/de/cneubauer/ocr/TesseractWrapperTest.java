package de.cneubauer.ocr;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        }
    }
}