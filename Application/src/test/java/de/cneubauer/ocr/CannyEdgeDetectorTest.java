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
    private OwnCannyEdgeDetector detector;
    private CannyEdgeDetector cannyEdgeDetector;
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
        this.detector = new OwnCannyEdgeDetector(this.origImage);
        this.cannyEdgeDetector = new CannyEdgeDetector();
    }

    @After
    public void tearDown() throws Exception {
        this.detector = null;
    }

    @Test
    public void testCannyEdgeDetector() throws Exception {
        this.cannyEdgeDetector.setLowThreshold(0.5f);
        this.cannyEdgeDetector.setHighThreshold(1f);
        this.cannyEdgeDetector.setSourceImage(this.origImage);
        this.cannyEdgeDetector.process();
        BufferedImage output = this.cannyEdgeDetector.getEdgesImage();
        File test = new File(".\\temp\\testTGDetector.png");
        ImageIO.write(output, "png", test);
        Assert.isTrue(Files.exists(new File(".\\temp\\testTGDetector.png").toPath()));
        Assert.isTrue(!this.origImage.equals(output));
    }

    @Test
    public void detect() throws Exception {
        BufferedImage output =  this.detector.detect();
        File test = new File(".\\temp\\testEdgeDetector.png");
        ImageIO.write(output, "png", test);
        Assert.isTrue(Files.exists(new File(".\\temp\\tempImage.png").toPath()));
        Assert.isTrue(!this.origImage.equals(output));
    }

    @Test
    public void testRemoveLines() throws Exception {
        String path = ".\\temp\\testTGDetector.png";
        File imageFile = new File(path);
        BufferedImage input = ImageIO.read(imageFile);
        BufferedImage output = this.detector.removeLines(input);
        File test = new File(".\\temp\\withoutEdgesTest.png");
        ImageIO.write(output, "png", test);
        Assert.isTrue(Files.exists(new File(".\\temp\\withoutEdgesTest.png").toPath()));
    }
}