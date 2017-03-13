package de.cneubauer.ocr;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Christoph Neubauer on 14.11.2016.
 * Test for HistogramMaker
 */
public class HistogramMakerTest {
    private BufferedImage image;
    private HistogramMaker maker;

    @Before
    public void setUp() throws Exception {
        String path = ".\\temp\\invoice.pdf";
        File imageFile = new File(path);

        if (path.endsWith("pdf")) {
            PDDocument pdf = PDDocument.load(imageFile);
            PDFRenderer renderer = new PDFRenderer(pdf);
            this.image = renderer.renderImageWithDPI(0, 300);
        } else {
            this.image = ImageIO.read(imageFile);
        }
        this.maker = new HistogramMaker();
    }

    @Test
    public void makeHistogram() throws Exception {
        BufferedImage output = this.maker.makeHistogram(this.image);

        File test = new File(".\\temp\\histogram.png");
        ImageIO.write(output, "png", test);
    }

    @Test
    public void makeVerticalHistogram() throws Exception {
        BufferedImage line = ImageIO.read(new File(".\\temp\\croppedLine.png"));
        this.maker = new HistogramMaker();
        BufferedImage output = this.maker.makeVerticalHistogram(line, false);

        File test = new File(".\\temp\\histogramWords.png");
        ImageIO.write(output, "png", test);
    }

}