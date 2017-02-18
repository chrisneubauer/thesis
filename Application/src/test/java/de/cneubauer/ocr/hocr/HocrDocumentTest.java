package de.cneubauer.ocr.hocr;

import de.cneubauer.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 14.02.2017.
 */
public class HocrDocumentTest extends AbstractTest {
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getPage() throws Exception {
        File hocr = new File("..\\hocrOutput.xml");
        String content = new Scanner(hocr).useDelimiter("\\Z").next();
        HocrDocument document = new HocrDocument(content);
        Assert.notNull(document);
        Assert.isTrue(document.getPage(0).getAreas().size() > 0);
        Assert.isTrue(document.getPage(0).getAreas().get(0).getParagraphs().size() > 0);
    }

    @Test
    public void getStringValues() throws Exception {
        File hocr = new File("..\\hocrOutput.xml");
        String content = new Scanner(hocr).useDelimiter("\\Z").next();
        HocrDocument document = new HocrDocument(content);
        List<String> wordlist = new LinkedList<>();
        for (HocrArea area : document.getPage(0).getAreas()) {
            wordlist.addAll(area.getAllWordsInArea());
        }
        Assert.isTrue(wordlist.size() > 0);
    }

}