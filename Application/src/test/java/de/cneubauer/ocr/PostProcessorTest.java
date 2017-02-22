package de.cneubauer.ocr;

import de.cneubauer.ocr.hocr.HocrDocument;
import de.cneubauer.ocr.hocr.HocrWord;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by Christoph Neubauer on 22.02.2017.
 * Test for the PostProcessor class
 */
public class PostProcessorTest {
    private HocrDocument doc;

    @Before
    public void setUp() throws Exception {
        File hocr = new File("..\\hocrOutput.xml");
        FileInputStream in = new FileInputStream(hocr);
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader r = new BufferedReader(isr);
        String line = r.readLine();
        String hocrDoc = "";
        while (line != null) {
            hocrDoc += line;
            line = r.readLine();
        }
        this.doc = new HocrDocument(hocrDoc);
    }

    @Test
    public void postProcess() throws Exception {
        HocrWord word = (HocrWord) this.doc.getPage(0).getSubElements().get(0).getSubElements().get(0).getSubElements().get(0).getSubElements().get(0);
        word.setValue("Abrecnungszeitpkt");

        PostProcessor postProcessor = new PostProcessor(this.doc);
        HocrDocument newDoc = postProcessor.postProcess();
        HocrWord improvedWord = (HocrWord) newDoc.getPage(0).getSubElements().get(0).getSubElements().get(0).getSubElements().get(0).getSubElements().get(0);

        Assert.isTrue(improvedWord.getValue().equals("Abrechnungszeitpunkt"));
    }
}