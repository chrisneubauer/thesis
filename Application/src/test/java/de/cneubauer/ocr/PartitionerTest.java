package de.cneubauer.ocr;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Files;

/**
 * Created by Christoph Neubauer on 10.11.2016.
 * Test for Partitioner-Class in OCR package
 */
public class PartitionerTest {
    private Partitioner partitioner;
    @Before
    public void setUp() throws Exception {
        String path = "..\\data\\Datenwerk4.pdf";
        File imageFile = new File(path);
        partitioner = new Partitioner(IOUtils.toByteArray(imageFile.toURI()));
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void splitPage() throws Exception {
        this.partitioner.splitPage();
        Assert.isTrue(Files.exists(new File(".\\temp\\header.png").toPath()));
        Assert.isTrue(Files.exists(new File(".\\temp\\body.png").toPath()));
        Assert.isTrue(Files.exists(new File(".\\temp\\footer.png").toPath()));
    }

    @Test
    public void separateImageByLayout() throws Exception {
        PDDocument pdf = PDDocument.load(partitioner.getCompleteFile());
        PDFRenderer renderer = new PDFRenderer(pdf);
        this.partitioner.separateImageByLayout(renderer.renderImageWithDPI(0, 600));
    }

    @Test
    public void separateImageByLayoutAndChunks() throws Exception {
        PDDocument pdf = PDDocument.load(partitioner.getCompleteFile());
        PDFRenderer renderer = new PDFRenderer(pdf);
        this.partitioner.separateImageByLayoutAndChunk(renderer.renderImageWithDPI(0, 300));
    }

}