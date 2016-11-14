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
public class ImageCropperTest {
    private BufferedImage image;
    private HistogramMaker maker;
    private ImageCropper cropper;

    @Before
    public void setUp() throws Exception {
        String path = ".\\temp\\tryold.png";
        File imageFile = new File(path);

        this.image = ImageIO.read(imageFile);
        this.maker = new HistogramMaker(this.image);
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
        Assert.isTrue(Files.exists(new File(".\\temp\\areas\\cropped_1.png").toPath()));
    }

}