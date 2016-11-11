package de.cneubauer.ocr;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 10.11.2016.
 */
public class ImagePreprocessorTest {
    private ImagePreprocessor preprocessor;
    private BufferedImage origImage;

    @Before
    public void setUp() throws Exception {
        String path = "..\\data\\Datenwerk4.pdf";
        boolean isPdf = false;
        path = "..\\data\\20160830_Scans\\Scan_20160822_161042_003.jpg";
        path = ".\\temp\\tempImage.png";
        File imageFile = new File(path);

        if (isPdf) {
            PDDocument pdf = PDDocument.load(imageFile);
            //PDDocument pdf = PDDocument.load(this.pdfFile);
            PDFRenderer renderer = new PDFRenderer(pdf);
            this.origImage = renderer.renderImageWithDPI(0, 600);
        } else {
            this.origImage = ImageIO.read(imageFile);
        }
        this.preprocessor = new ImagePreprocessor(this.origImage);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void toGreyscaleImage() throws Exception {

        File orig = new File(".\\temp\\originalPdf.png");
        System.out.println("Image Created -> "+ orig.getName());
        ImageIO.write(this.preprocessor.getImage(), "png", orig);
        
        BufferedImage gaussed = this.preprocessor.reduceNoise();

        File gaussian = new File(".\\temp\\gaussianRemoved.png");
        System.out.println("Image Created -> "+ gaussian.getName());
        ImageIO.write(gaussed, "png", gaussian);

        BufferedImage greyscaledImg = this.preprocessor.toGreyscaleImage();

        File greyscaled = new File(".\\temp\\greyscaled.png");
        System.out.println("Image Created -> "+ greyscaled.getName());
        ImageIO.write(greyscaledImg, "png", greyscaled);
    }

    @Test
    public void preprocess() throws Exception {
        BufferedImage result = this.preprocessor.preprocess();
        Assert.isTrue(Files.exists(new File(".\\temp\\tempImage.png").toPath()));
        Assert.isTrue(!this.origImage.equals(result));
    }

    @Test
    public void deSkew() throws Exception {
        this.preprocessor.deSkewImage();
    }

    @Test
    public void anotherDeskec() throws Exception {
        BufferedImage result = this.preprocessor.anotherDeskewApproach();
        File test = new File(".\\temp\\testI4.png");
        ImageIO.write(result, "png", test);
    }
}