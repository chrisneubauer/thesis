package de.cneubauer.ocr;

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

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 12.11.2016.
 */
public class RankFilterTest {
    private BufferedImage origImage;
    private RankFilter filter;
    @Before
    public void setUp() throws Exception {
        String path = ".\\temp\\testEdgeDetector.png";
        File imageFile = new File(path);
        this.origImage = ImageIO.read(imageFile);
        filter = new RankFilter();
    }

    @After
    public void tearDown() throws Exception {
        this.filter = null;
    }

    @Test
    public void filterImageHorizontally() throws Exception {
        BufferedImage output = this.filter.filterImageHorizontally(this.origImage);
        File test = new File(".\\temp\\filteredHorizontally.png");
        ImageIO.write(output, "png", test);
        Assert.isTrue(Files.exists(new File(".\\temp\\filteredHorizontally.png").toPath()));
        Assert.isTrue(!this.origImage.equals(output));
    }

    @Test
    public void filterImageVertically() throws Exception {
        BufferedImage output = this.filter.filterImageVertically(this.origImage);
        File test = new File(".\\temp\\filteredVertically.png");
        ImageIO.write(output, "png", test);
        Assert.isTrue(Files.exists(new File(".\\temp\\filteredVertically.png").toPath()));
        Assert.isTrue(!this.origImage.equals(output));
    }

    @Test
    public void filterBothSides() throws Exception {
        BufferedImage output = this.filter.filterImageHorizontally(this.origImage);
        output = this.filter.filterImageVertically(output);
        File test = new File(".\\temp\\filteredBothSides.png");
        ImageIO.write(output, "png", test);
        Assert.isTrue(Files.exists(new File(".\\temp\\filteredBothSides.png").toPath()));
        Assert.isTrue(!this.origImage.equals(output));
    }

}