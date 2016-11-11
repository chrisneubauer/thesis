package de.cneubauer.ocr;

import com.google.common.io.Files;

import magick.MagickException;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;
import org.im4java.process.Pipe;
import org.im4java.process.ProcessStarter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
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
    //private MagickImage magickImage;
    private IMOperation imOperation;
    private String tempPath = ".\\temp\\tempImage.png";
    private String tempPathConverted = ".\\temp\\tempImageConverted.png";

    private BufferedImage colouredImage;
    private double gaussianRatio = 10.0;

    public ImagePreprocessor(BufferedImage imageToProcess) {
        ProcessStarter.setGlobalSearchPath(".\\portable\\imagemagick\\ImageMagick-7.0.3-6-portable-Q16-x86;");
        this.colouredImage = imageToProcess;
        File outputfile = new File(this.tempPath);
        File outputConvertedFile = new File(this.tempPathConverted);
        try {
            ImageIO.write(this.colouredImage, "png", outputfile);
            ImageIO.write(this.colouredImage, "png", outputConvertedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            //ImageInfo outputImage = new ImageInfo(tempPath);
            //this.magickImage = new MagickImage(outputImage);
        }
        catch (MagickException e) {
            e.printStackTrace();
        }*/
    }

    public BufferedImage preprocess() {
        try {
            this.deSkewImage();
            this.replaceAfterConversion();
            this.deSpeckleImage();
            this.replaceAfterConversion();
            this.greyScaleImage();
            /*this.removeLinesWithoutWords();
            this.analyzeInvoiceLayout();
            this.findBaselineForWords();
            this.separateWords();
            this.normaliseAspectRatioAndScale();*/
        } catch (MagickException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IM4JavaException e) {
            e.printStackTrace();
        }
        return this.colouredImage;
    }

    private void greyScaleImage() throws MagickException, InterruptedException, IOException, IM4JavaException {
        this.colouredImage = this.createImageFromBytes(Files.toByteArray(new File(this.tempPathConverted)));
        //this.colouredImage = this.createImageFromBytes(this.magickImage.imageToBlob(new ImageInfo(this.tempPath)));
        this.colouredImage = this.toGreyscaleImage();
    }

    private void deSpeckleImage() throws MagickException, InterruptedException, IOException, IM4JavaException {
        this.imOperation = new IMOperation();
        this.imOperation.addImage(this.tempPath);
        this.imOperation.addImage(this.tempPathConverted);
        this.imOperation.despeckle();
        ConvertCmd cmd = new ConvertCmd();

        cmd.run(this.imOperation);
        //this.magickImage = this.magickImage.despeckleImage();
    }

    public void deSkewImage() throws MagickException, InterruptedException, IOException, IM4JavaException {

        //double radians = DeSkewer.doIt(this.colouredImage);

        /*InputStream is = new FileInputStream(this.tempPath);
        Pipe pipeIn = new Pipe (is, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Pipe pipeOut = new Pipe(null, os);*/
        ConvertCmd convert = new ConvertCmd();

      //  convert.setInputProvider(pipeIn);
      //  convert.setOutputConsumer(pipeOut);

        this.imOperation = new IMOperation();
        this.imOperation.addImage();
        this.imOperation.deskew();
        this.imOperation.addImage();
        //this.imOperation.addImage(this.tempPath);
        //this.imOperation.addImage(this.tempPathConverted);
        //ConvertCmd cmd = new ConvertCmd();

        convert.run(this.imOperation, this.tempPath, this.tempPathConverted);
        //convert.run(this.imOperation, this.tempPath, this.tempPathConverted);
        //cmd.run(this.imOperation);

        //this.magickImage.rotateImage(radians);
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

    public BufferedImage anotherDeskewApproach() throws IOException, IM4JavaException, InterruptedException {
        IMOperation op = new IMOperation();
        op.addImage();
        //op.resize(350);
        //op.deskew();
        op.addImage("png:-");
        BufferedImage images = ImageIO.read(new File(this.tempPathConverted));

        // set up command
        ConvertCmd convert = new ConvertCmd();
        Stream2BufferedImage s2b = new Stream2BufferedImage();
        convert.setOutputConsumer(s2b);

        // run command and extract BufferedImage from OutputConsumer
        convert.run(op,images);
        return s2b.getImage();
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

    private BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void replaceAfterConversion() {
        byte[] newImage = new byte[0];
        try {
            newImage = Files.toByteArray(new File(this.tempPathConverted));

            BufferedImage newBufferedImage = this.createImageFromBytes(newImage);

            File orig = new File(this.tempPath);
            ImageIO.write(newBufferedImage, "png", orig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
