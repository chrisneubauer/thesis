package de.cneubauer.ocr;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 10.11.2016.
 */
public class ImagePreprocessorTest {
    private ImagePreprocessor preprocessor;

    @Before
    public void setUp() throws Exception {
        String path = "..\\data\\Datenwerk4.pdf";
        File imageFile = new File(path);
        PDDocument pdf = PDDocument.load(imageFile);
        //PDDocument pdf = PDDocument.load(this.pdfFile);
        PDFRenderer renderer = new PDFRenderer(pdf);
        BufferedImage image = renderer.renderImageWithDPI(0, 600);
        this.preprocessor = new ImagePreprocessor(image);
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

}