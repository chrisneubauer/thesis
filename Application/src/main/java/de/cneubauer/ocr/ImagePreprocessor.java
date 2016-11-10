package de.cneubauer.ocr;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Created by Christoph Neubauer on 10.11.2016.
 * Preprocessor who deals with coloured images, removes gaussian noise and converts the image to a greyscale image
 */
public class ImagePreprocessor {

    private BufferedImage colouredImage;
    private double gaussianRatio = 10.0;

    public ImagePreprocessor(BufferedImage imageToProcess) {
        this.colouredImage = imageToProcess;
    }

    public BufferedImage reduceNoise() {
        Raster source = colouredImage.getRaster();
        BufferedImage output = new BufferedImage(colouredImage.getWidth(), colouredImage.getHeight(), colouredImage.getType());
        WritableRaster out = output.getRaster();

        int currVal;
        double newVal;
        double gaussian;
        int bands  = out.getNumBands();
        int width  = colouredImage.getWidth();
        int height = colouredImage.getHeight();
        java.util.Random randGen = new java.util.Random();

        for (int j=0; j<height; j++) {
            for (int i=0; i<width; i++) {
                gaussian = randGen.nextGaussian();

                for (int b=0; b<bands; b++) {
                    newVal = gaussianRatio * gaussian;
                    currVal = source.getSample(i, j, b);
                    newVal = newVal + currVal;
                    if (newVal < 0)   newVal = 0.0;
                    if (newVal > 255) newVal = 255.0;

                    out.setSample(i, j, b, (int)(newVal));
                }
            }
        }

        return output;
    }

    public BufferedImage toGreyscaleImage() {
        BufferedImage image = new BufferedImage(colouredImage.getWidth(), colouredImage.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image.getGraphics();
        g.drawImage(colouredImage, 0, 0, null);
        g.dispose();
        return image;
    }

    public BufferedImage getImage() {
        return this.colouredImage;
    }
}
