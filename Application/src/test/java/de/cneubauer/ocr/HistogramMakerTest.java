package de.cneubauer.ocr;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 14.11.2016.
 */
public class HistogramMakerTest {
    private BufferedImage image;
    private HistogramMaker maker;

    @Before
    public void setUp() throws Exception {
        String path = ".\\temp\\tryold.png";
        File imageFile = new File(path);

        this.image = ImageIO.read(imageFile);
        this.maker = new HistogramMaker(this.image);
    }

    @Test
    public void makeHistogram() throws Exception {
        BufferedImage output = this.maker.makeHistogram(this.image);

        File test = new File(".\\temp\\histogram.png");
        ImageIO.write(output, "png", test);
    }

}