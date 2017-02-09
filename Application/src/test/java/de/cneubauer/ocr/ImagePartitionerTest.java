package de.cneubauer.ocr;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 07.02.2017.
 */
public class ImagePartitionerTest {
    private ImagePartitioner partitioner;
    private BufferedImage origImage;

    @Before
    public void setUp() throws Exception {
        String path = "..\\data\\whiteBorders.pdf";
        File imageFile = new File(path);

        if (path.endsWith(".pdf")) {
            PDDocument pdf = PDDocument.load(imageFile);
            PDFRenderer renderer = new PDFRenderer(pdf);
            this.origImage = renderer.renderImageWithDPI(0, 600);
        } else {
            this.origImage = ImageIO.read(imageFile);
        }
        this.partitioner = new ImagePartitioner(this.origImage);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testFindTableInInvoice() {
        ImagePreprocessor preprocessor = new ImagePreprocessor(this.origImage);
        BufferedImage in = preprocessor.preprocess();
        BufferedImage out = this.partitioner.findTableInInvoice(in, true);

        String outputDir = ".\\temp\\";
        File test = new File( outputDir + "table.png");
        try {
            ImageIO.write(out, "png", test);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}