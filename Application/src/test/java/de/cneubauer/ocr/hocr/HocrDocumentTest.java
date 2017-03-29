package de.cneubauer.ocr.hocr;

import de.cneubauer.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 * Test for HocrDocument
 */
public class HocrDocumentTest extends AbstractTest {
    private HocrDocument document;
    @Before
    public void setUp() throws Exception {
        databaseChanged = false;
        super.setUp();
        InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("/hocr/hocrOutput.xml"));
        String content = new Scanner(in).useDelimiter("\\Z").next();
        this.document = new HocrDocument(content);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getPage() throws Exception {
        //File hocr = new File("..\\hocrOutput.xml");
        Assert.notNull(this.document);
        Assert.isTrue(this.document.getPage(0).getSubElements().size() > 0);
        Assert.isTrue(this.document.getPage(0).getSubElements().get(0).getSubElements().size() > 0);
    }

    @Test
    public void getStringValues() throws Exception {
        List<String> wordlist = new LinkedList<>();
        for (HocrElement area : this.document.getPage(0).getSubElements()) {
            HocrArea currentArea = (HocrArea) area;
            wordlist.addAll(currentArea.getAllWordsInArea());
        }
        Assert.isTrue(wordlist.size() > 0);
    }

}