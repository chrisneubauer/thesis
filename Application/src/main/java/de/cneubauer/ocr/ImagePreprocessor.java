package de.cneubauer.ocr;

import de.cneubauer.util.DeSkewer;
import magick.MagickException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;
import org.im4java.process.ProcessStarter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Christoph Neubauer on 10.11.2016.
 * Preprocessor who deals with coloured images, removes gaussian noise and converts the image to a greyscale image
 * Steps being made:
 * 1. De-Skew and align horizontally
 * 2. Despeckle the image: Remove singular points inside the image
 * 3. Greyscaling the image
 * 4. Removing lines that not contain words (lines, empty pages etc.)
 * 5. Analyzation of the invoice layout
 * 6. Find baseline for words and separate the words
 * 7. Aspect Ratio and Scale is being normalised
 * TODO: Dictionary that contains invoice words
 */
public class ImagePreprocessor {
    private String tempPath = ".\\temp\\tempImage.png";
    private String tempPathConverted = ".\\temp\\tempImageConverted.png";

    private BufferedImage inputFile;
    private BufferedImage outputFile;
    private double gaussianRatio = 10.0;

    public ImagePreprocessor(BufferedImage imageToProcess) {
        ProcessStarter.setGlobalSearchPath(".\\portable\\imagemagick\\ImageMagick-7.0.3-6-portable-Q16-x86;");
        this.inputFile = imageToProcess;
        File outputfile = new File(this.tempPath);
        File outputConvertedFile = new File(this.tempPathConverted);
        try {
            ImageIO.write(this.inputFile, "png", outputfile);
            ImageIO.write(this.inputFile, "png", outputConvertedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage preprocess() {
        try {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Preprocessing started...");
            BufferedImage image = this.inputFile;
            Logger.getLogger(this.getClass()).log(Level.INFO, "deskewing...");
            this.outputFile = this.deSkewImage(image);

            image = this.outputFile;
            Logger.getLogger(this.getClass()).log(Level.INFO, "greyscaling...");
            this.outputFile = this.greyScaleImage(image);

            image = this.outputFile;
            Logger.getLogger(this.getClass()).log(Level.INFO, "despeckling...");
            this.outputFile = this.deSpeckleImage(image);

            /*this.removeLinesWithoutWords();
            this.analyzeInvoiceLayout();
            this.findBaselineForWords();
            this.separateWords();
            this.normaliseAspectRatioAndScale();*/
            Logger.getLogger(this.getClass()).log(Level.INFO, "Preprocessing done. Returning image...");
            return this.outputFile;
        } catch (MagickException | InterruptedException | IOException | IM4JavaException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedImage greyScaleImage(BufferedImage img) throws MagickException, InterruptedException, IOException, IM4JavaException {
        BufferedImage image = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = image.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return image;
    }

    private BufferedImage deSpeckleImage(BufferedImage img) throws MagickException, InterruptedException, IOException, IM4JavaException {
        IMOperation op = new IMOperation();
        op.addImage();

        op.despeckle();
        op.addImage("png:-");

        // set up command
        ConvertCmd convert = new ConvertCmd();

        Stream2BufferedImage s2b = new Stream2BufferedImage();
        convert.setOutputConsumer(s2b);

        // run command and extract BufferedImage from OutputConsumer
        convert.run(op,img);
        return s2b.getImage();
    }

    private BufferedImage deSkewImage(BufferedImage img) throws MagickException, InterruptedException, IOException, IM4JavaException {
        IMOperation op = new IMOperation();
        op.addImage();

        double value = DeSkewer.calculateRadiant(img);
        op.deskew(value);
        op.addImage("png:-");

        // set up command
        ConvertCmd convert = new ConvertCmd();

        Stream2BufferedImage s2b = new Stream2BufferedImage();
        convert.setOutputConsumer(s2b);

        // run command and extract BufferedImage from OutputConsumer
        convert.run(op,img);
        return s2b.getImage();
    }

    /*public BufferedImage reduceNoise() {
        Raster source = inputFile.getRaster();
        BufferedImage output = new BufferedImage(inputFile.getWidth(), inputFile.getHeight(), inputFile.getType());
        WritableRaster out = output.getRaster();

        int currVal;
        double newVal;
        double gaussian;
        int bands  = out.getNumBands();
        int width  = inputFile.getWidth();
        int height = inputFile.getHeight();
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
    }*/

    public BufferedImage getImage() {
        return this.inputFile;
    }
}
