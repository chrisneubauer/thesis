package de.cneubauer.ocr;

import de.cneubauer.AbstractTest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

/**
 * Created by Christoph Neubauer on 12.11.2016.
 * Test for CannyEdgeDetector
 */
public class CannyEdgeDetectorTest extends AbstractTest {
    private CannyEdgeDetector detector;
    private BufferedImage origImage;

    @Before
    public void setUp() throws Exception {
        String path = "..\\data\\Datenwerk4.pdf";
        File imageFile = new File(path);

        if (path.endsWith(".pdf")) {
            PDDocument pdf = PDDocument.load(imageFile);
            PDFRenderer renderer = new PDFRenderer(pdf);
            this.origImage = renderer.renderImageWithDPI(0, 600, ImageType.GRAY);
        } else {
            this.origImage = ImageIO.read(imageFile);
        }
        this.detector = new CannyEdgeDetector(this.origImage);
    }

    @After
    public void tearDown() throws Exception {
        this.detector = null;
    }

    @Test
    public void detect() throws Exception {
        BufferedImage output =  this.detector.detect();
        File test = new File(".\\temp\\testEdgeDetector.png");
        ImageIO.write(output, "png", test);
        Assert.isTrue(Files.exists(new File(".\\temp\\tempImage.png").toPath()));
        Assert.isTrue(!this.origImage.equals(output));
    }

}