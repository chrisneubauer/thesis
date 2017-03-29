package de.cneubauer.ocr.tesseract;

import de.cneubauer.AbstractTest;
import de.cneubauer.ocr.ImagePartitioner;
import de.cneubauer.ocr.ImagePreprocessor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
    @Ignore
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
    @Ignore
    public void executeDatenwerk() throws Exception {
        String path =  "..\\temp\\Datenwerk";
        String outputPath = "..\\";

        for (int i = 11; i <= 20; i++) {
            File imageFile = new File(path + i + ".pdf");
            BufferedImage image;

            PDDocument pdf = PDDocument.load(imageFile);
            PDFRenderer renderer = new PDFRenderer(pdf);
            image = renderer.renderImageWithDPI(0, 300);

            ImagePreprocessor preprocessor = new ImagePreprocessor(image);
            image = preprocessor.preprocess();
            ImagePartitioner partitioner = new ImagePartitioner(image);
            image = partitioner.findTableInInvoice(image, false);
            String result = wrapper.initOcr(image);
            String lines[] = result.split("\\r?\\n");
            File outputFile = new File(outputPath + i + ".txt");
            Files.write(outputFile.toPath(), Arrays.asList(lines));
        }
    }

    @Test
    @Ignore
    public void executeDatenwerkHeader() throws Exception {
        String path =  "..\\temp\\Datenwerk";
        String outputPath = "..\\";

        for (int i = 1; i <= 20; i++) {
            File imageFile = new File(path + i + ".pdf");
            BufferedImage image;

            PDDocument pdf = PDDocument.load(imageFile);
            PDFRenderer renderer = new PDFRenderer(pdf);
            image = renderer.renderImageWithDPI(0, 300);

            ImagePreprocessor preprocessor = new ImagePreprocessor(image);
            image = preprocessor.preprocess();
            ImagePartitioner partitioner = new ImagePartitioner(image);
            BufferedImage[] parts = partitioner.process();
            String result = wrapper.initOcr(parts[0]);
            String lines[] = result.split("\\r?\\n");
            File outputFile = new File(outputPath + i + "left.txt");
            Files.write(outputFile.toPath(), Arrays.asList(lines));

            result = wrapper.initOcr(parts[1]);
            lines = result.split("\\r?\\n");
            outputFile = new File(outputPath + i + "right.txt");
            Files.write(outputFile.toPath(), Arrays.asList(lines));
        }
    }
}
