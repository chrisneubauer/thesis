package de.cneubauer.ocr;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import magick.MagickException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
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
 */
public class ImagePreprocessor {
    private String tempPath = ".\\temp\\tempImage.jpg";
    private String tempPathConverted = ".\\temp\\tempImageConverted.jpg";
    private BufferedImage inputFile;

    public ImagePreprocessor(BufferedImage imageToProcess) {
        ProcessStarter.setGlobalSearchPath(".\\portable\\imagemagick\\ImageMagick-7.0.3-6-portable-Q16-x86;");
        this.inputFile = imageToProcess;
        File outputfile = new File(this.tempPath);
        File outputConvertedFile = new File(this.tempPathConverted);
        try {
            ImageIO.write(this.inputFile, "jpg", outputfile);
            ImageIO.write(this.inputFile, "jpg", outputConvertedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImagePreprocessor(String path) {
        try {
        File imageFile = new File(path);
        if (path.endsWith(".pdf")) {
            PDDocument pdf = PDDocument.load(imageFile);
            PDFRenderer renderer = new PDFRenderer(pdf);
            this.inputFile = renderer.renderImageWithDPI(0, 300);
            pdf.close();
        } else {
            this.inputFile = ImageIO.read(imageFile);
        }

        ProcessStarter.setGlobalSearchPath(".\\portable\\imagemagick\\ImageMagick-7.0.3-6-portable-Q16-x86;");

        File outputfile = new File(this.tempPath);
        File outputConvertedFile = new File(this.tempPathConverted);
            ImageIO.write(this.inputFile, "jpg", outputfile);
            ImageIO.write(this.inputFile, "jpg", outputConvertedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage preprocess() {
        try {
            Logger.getLogger(this.getClass()).log(Level.INFO, "Preprocessing started...");
            BufferedImage image = this.inputFile;

            Logger.getLogger(this.getClass()).log(Level.INFO, "resizing...");
            BufferedImage outputFile = this.resizeImage(image);

            image = outputFile;

            Logger.getLogger(this.getClass()).log(Level.INFO, "adjusting to 300dpi...");
            outputFile = this.adjustDPI(image);

            image = outputFile;

            Logger.getLogger(this.getClass()).log(Level.INFO, "deskewing...");
            outputFile = this.deSkewImage(image);

            image = outputFile;

            Logger.getLogger(this.getClass()).log(Level.INFO, "greyscaling...");
            outputFile = this.greyScaleImage(image);

            image = outputFile;

            Logger.getLogger(this.getClass()).log(Level.INFO, "despeckling...");
            outputFile = this.deSpeckleImage(image);

            Logger.getLogger(this.getClass()).log(Level.INFO, "Preprocessing done. Returning image...");
            return outputFile;
        } catch (MagickException | InterruptedException | IOException | IM4JavaException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedImage resizeImage(BufferedImage image) throws InterruptedException, IOException, IM4JavaException {
        IMOperation op = new IMOperation();
        op.addImage();

        op.adaptiveResize(3508, 2480);
        op.addImage("jpg:-");

        ConvertCmd convert = new ConvertCmd();
        Stream2BufferedImage s2b = new Stream2BufferedImage();
        convert.setOutputConsumer(s2b);

        convert.run(op,image);
        return s2b.getImage();
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
        op.addImage("jpg:-");

        ConvertCmd convert = new ConvertCmd();
        Stream2BufferedImage s2b = new Stream2BufferedImage();
        convert.setOutputConsumer(s2b);

        convert.run(op,img);
        return s2b.getImage();
    }

    private BufferedImage deSkewImage(BufferedImage img) throws MagickException, InterruptedException, IOException, IM4JavaException {
        IMOperation op = new IMOperation();
        op.addImage();

        double value = 0.4;
        op.deskew(value);
        op.addImage("jpg:-");

        ConvertCmd convert = new ConvertCmd();
        Stream2BufferedImage s2b = new Stream2BufferedImage();
        convert.setOutputConsumer(s2b);

        convert.run(op,img);
        return s2b.getImage();
    }

    private BufferedImage adjustDPI(BufferedImage image) throws FileNotFoundException {
        File imageFile = new File(tempPath + "300dpi" + ".jpeg");
        FileOutputStream fos = new FileOutputStream(imageFile);
        JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(fos);
        JPEGEncodeParam jpegEncodeParam = jpegEncoder.getDefaultJPEGEncodeParam(image);
        jpegEncodeParam.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);

        jpegEncodeParam.setQuality(1, false);
        jpegEncodeParam.setXDensity(300);
        jpegEncodeParam.setYDensity(300);
        jpegEncoder.setJPEGEncodeParam(jpegEncodeParam);
        try {
            jpegEncoder.encode(image, jpegEncodeParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image.flush();
        try {
            fos.close();
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BufferedImage getImage() {
        return this.inputFile;
    }
}
