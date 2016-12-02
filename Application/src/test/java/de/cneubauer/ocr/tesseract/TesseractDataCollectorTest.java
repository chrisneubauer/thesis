package de.cneubauer.ocr.tesseract;

import de.cneubauer.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by Christoph Neubauer on 23.11.2016.
 * This class executes ocr and saves output to text files in order to check them later
 */
public class TesseractDataCollectorTest extends AbstractTest {
    private TesseractWrapper wrapper;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        databaseChanged = false;
        wrapper = new TesseractWrapper();
    }

    @After
    public void tearDown() throws Exception {
        wrapper = null;
    }

    @Test
    public void execute() throws Exception {
        String path =  "..\\Data\\20160830_Scans\\";
        String outputPath = "..\\Data\\Output\\";
        File folder = new File(path);
        File[] files = folder.listFiles();
        int count = 1;
        if (files != null) {
            for (final File fileEntry : files) {
                String result = wrapper.initOcr(fileEntry);
                String lines[] = result.split("\\r?\\n");
                File outputFile = new File(outputPath + count + ".txt");
                Files.write(outputFile.toPath(), Arrays.asList(lines));
                count++;
            }
        }
    }

    @Test
    public void executeDatenwerk() throws Exception {
        String path =  "..\\Data\\Datenwerk";
        String outputPath = "..\\Data\\OutputDatenwerk\\";

        for (int i = 1; i < 15; i++) {
            File f = new File(path + i + ".pdf");
            String result = wrapper.initOcr(f);
            String lines[] = result.split("\\r?\\n");
            File outputFile = new File(outputPath + i + ".txt");
            Files.write(outputFile.toPath(), Arrays.asList(lines));
        }
    }
}
