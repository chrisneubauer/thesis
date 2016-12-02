package de.cneubauer.ocr;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;

import java.awt.image.BufferedImage;
/**
 * Created by Christoph Neubauer on 14.11.2016.
 * Creates a skeleton of the image
 */
class Skeletonizer {
    private BufferedImage img;

    Skeletonizer(BufferedImage image) {
        this.img = image;
    }

    BufferedImage createSkeleton() {
        IMOperation op = new IMOperation();
        op.addImage();

        //op.morphology("thinning:-1", "LineEnds:-1;Peaks:1.5");
        //op.morphology("Convolve", "Sobel");
        // /op.morphology("thinning:-1 Diagonals");

        op.morphology("Edge", "Diamond");
        op.addImage("png:-");

        ConvertCmd convert = new ConvertCmd();
        Stream2BufferedImage s2b = new Stream2BufferedImage();
        convert.setOutputConsumer(s2b);

        try {
            System.out.println("Now runnning sobel convolution");
            convert.run(op,this.img);
            System.out.println("Convolution done");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s2b.getImage();
    }
}
