package de.cneubauer.ocr;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Created by Christoph Neubauer on 14.11.2016.
 * Test for ImageCropper
 */
public class ImageCropperTest {
    private BufferedImage image;
    private HistogramMaker maker;
    private ImageCropper cropper;

    @Before
    public void setUp() throws Exception {
        InputStream file = this.getClass().getResourceAsStream("/data/generation/template1_generated0.pdf");
        PDDocument pdf = PDDocument.load(file);
        PDFRenderer renderer = new PDFRenderer(pdf);
        //String path = ".\\temp\\tryold.png";
        //File imageFile = new File(path);

        this.image = renderer.renderImageWithDPI(0, 300);
        //this.image = ImageIO.read(imageFile);
        this.maker = new HistogramMaker();

        this.maker.setMinThreshold(0.2);
        this.maker.setMaxThreshold(0.7);

        this.maker.makeHistogram(this.image);
        boolean[] importantRows = this.maker.getImportantRows();

        this.cropper = new ImageCropper(50);
        this.cropper.setImageToCrop(this.image, importantRows);
    }

    @Test
    public void cropImages() throws Exception {
        this.cropper.cropImages();
    }

}