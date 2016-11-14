package de.cneubauer.ocr;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;
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
public class SkeletonizerTest {    private OwnCannyEdgeDetector detector;
    private Skeletonizer skeletonizer;
    private BufferedImage origImage;

    @Before
    public void setUp() throws Exception {
        String path = ".\\temp\\testTGDetector.png";
        File imageFile = new File(path);

        this.origImage = ImageIO.read(imageFile);
        this.skeletonizer = new Skeletonizer(this.origImage);
    }
    @Test
    public void createSkeleton() throws Exception {
        BufferedImage output = this.skeletonizer.createSkeleton();
        File test = new File(".\\temp\\skeleton.png");
        ImageIO.write(output, "png", test);
        Assert.isTrue(Files.exists(new File(".\\temp\\skeleton.png").toPath()));
    }

    @Test
    public void doAfterSkeleton() throws Exception {
        BufferedImage input = ImageIO.read(new File(".\\temp\\tempImage.png"));
        IMOperation op = new IMOperation();
        op.addImage();

        op.morphology("Close", "Square:1");
        op.morphology("Open", "Diamond");
        op.addImage("png:-");

        ConvertCmd convert = new ConvertCmd();
        Stream2BufferedImage s2b = new Stream2BufferedImage();
        convert.setOutputConsumer(s2b);

        try {
            convert.run(op,input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedImage output = s2b.getImage();
        File test = new File(".\\temp\\try.png");
        ImageIO.write(output, "png", test);
    }
}