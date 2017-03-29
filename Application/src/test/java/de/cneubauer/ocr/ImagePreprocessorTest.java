package de.cneubauer.ocr;

import de.cneubauer.AbstractTest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Created by Christoph Neubauer on 10.11.2016.
 * Test for ImagePreprocessor
 */
public class ImagePreprocessorTest extends AbstractTest {
    private ImagePreprocessor preprocessor;
    private BufferedImage origImage;

    @Before
    public void setUp() throws Exception {
        databaseChanged = false;
        InputStream file = this.getClass().getResourceAsStream("/data/generation/template1_generated0.pdf");
        PDDocument pdf = PDDocument.load(file);
        PDFRenderer renderer = new PDFRenderer(pdf);
        this.origImage = renderer.renderImageWithDPI(0, 300);
        this.preprocessor = new ImagePreprocessor(this.origImage);
    }

    @After
    public void tearDown() throws Exception {
        this.preprocessor = null;
        this.origImage = null;
    }

    @Test
    public void preprocess() throws Exception {
        BufferedImage result = this.preprocessor.preprocess();
        File test = new File(".\\temp\\preprocessingResult.jpg");
        ImageIO.write(result, "jpg", test);
        Assert.isTrue(Files.exists(new File(".\\temp\\tempImage.jpg").toPath()));
        Assert.isTrue(!this.origImage.equals(result));
    }
}