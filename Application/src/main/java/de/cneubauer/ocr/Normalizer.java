package de.cneubauer.ocr;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Christoph Neubauer on 07.02.2017.
 * Normalizes image before OCR
 * All pictures should:
 * - be greyscaled
 * - contain 300dpi
 */
@Deprecated
public class Normalizer {
    private String tempPath = ".\\temp\\";

    public BufferedImage process(BufferedImage image) {
        BufferedImage output = this.greyscaleImage(image);

        try {
            return this.adjustDPI(output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage adjustDPI(BufferedImage image) throws FileNotFoundException {
        File imageFile = new File(tempPath + "300dpi" + ".jpeg");
        FileOutputStream fos = new FileOutputStream(imageFile);
        JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(fos);
        JPEGEncodeParam jpegEncodeParam = jpegEncoder.getDefaultJPEGEncodeParam(image);
        jpegEncodeParam.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
        jpegEncoder.setJPEGEncodeParam(jpegEncodeParam);
        jpegEncodeParam.setQuality(0.75f, false);
        jpegEncodeParam.setXDensity(300); //DPI rate 100, 200 or 300
        jpegEncodeParam.setYDensity(300); //DPI rate 100, 200 or 300
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

    private BufferedImage greyscaleImage(BufferedImage image) {
        BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = output.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return output;
    }
}
