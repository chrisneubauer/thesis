package de.cneubauer.ocr;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Stream2BufferedImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;

/**
 * Created by Christoph Neubauer on 12.11.2016.
 * Apply Gaussian filter to smooth the image in order to remove the noise
 * Find the intensity gradients of the image
 * Apply non-maximum suppression to get rid of spurious response to edge detection
 * Apply double threshold to determine potential edges
 * Track edge by hysteresis: Finalize the detection of edges by suppressing all the other edges that are weak and not connected to strong edges.
 */
public class CannyEdgeDetector {
    private BufferedImage input;
    private BufferedImage output;
    private double threshold = 3.0;
    private double[][] weightedG;

    public CannyEdgeDetector(BufferedImage inputImage) {
        this.input = inputImage;
        this.weightedG = new double[inputImage.getWidth()][inputImage.getHeight()];
    }

    // applies edge detection on the input image
    // return the edged image
    public BufferedImage detect() {
        System.out.println("Applying gauss filter");
        this.output = this.applyGaussianFilter(this.input);
        System.out.println("Finding intensity gradients");
        this.findIntensityGradients(this.output);
        System.out.println("applying non maximum suppression");
        this.output = this.applyNonMaximumSuppression(this.output);
        System.out.println("applying the double threshold");
        this.applyDoubleThreshold(this.output);
        //this.trackEdgeByHysteresis(this.input);
        return this.output;
    }

    // from CQ+08 - A double-threshold image binarization method based on edge detector - Pattern Recognition 41
    private BufferedImage applyDoubleThreshold(BufferedImage input) {
        return input;
    }

    // from http://users.ecs.soton.ac.uk/msn/book/new_demo/nonmax/nonmax.java
    private BufferedImage applyNonMaximumSuppression(BufferedImage inputImage) {
        int convolveX[] = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        int convolveY[] = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        int width = input.getWidth();
        int height = input.getHeight();
        int templateSize = 3;

        int[] orig=new int[width*height];
        int[] input;
        int[] output;

        //TODO: Make image to byte array
        PixelGrabber grabber = new PixelGrabber(inputImage, 0, 0, width, height, orig, 0, width);
        try {
            grabber.grabPixels();
        }
        catch(InterruptedException e2) {
            System.out.println("error: " + e2);
        }
        input = orig;
        output = orig;

        // first convolute image with x and y templates
        int diffx[] = new int[width*height];
        int diffy[] = new int[width*height];
        int mag[] = new int[width*height];

        int valx, valy;

        for(int x = templateSize / 2; x < width - (templateSize / 2); x++) {
            for(int y= templateSize / 2; y < height- (templateSize / 2); y++) {
                valx = 0;
                valy = 0;
                for(int x1 = 0; x1 < templateSize; x1++) {
                    for(int y1 = 0; y1 < templateSize; y1++) {
                        int pos = (y1 * templateSize + x1);
                        int imPos = (x + (x1 - (templateSize / 2))) + ((y + (y1 - (templateSize / 2))) * width);

                        valx +=((input[imPos]&0xff) * convolveX[pos]);
                        valy +=((input[imPos]&0xff) * convolveY[pos]);
                    }
                }
                diffx[x + (y * width)] = valx;
                diffy[x + (y * width)] = valy;
                mag[x + (y * width)] = (int)(Math.sqrt((valx * valx) + (valy * valy)));
            }
        }

        for(int x = 1; x < width - 1; x++) {
            for(int y = 1 ; y < height - 1; y++) {
                int dx, dy;

                if(diffx[x + (y * width)] > 0) dx = 1;
                else dx = -1;

                if(diffy[x + (y * width)] > 0) dy = 1;
                else dy = -1;

                int a1, a2, b1, b2, A, B, point, val;
                if(Math.abs(diffx[x + (y * width)]) > Math.abs(diffy[x + (y * width)]))
                {
                    a1 = mag[(x+dx) + ((y) * width)];
                    a2 = mag[(x+dx) + ((y-dy) * width)];
                    b1 = mag[(x-dx) + ((y) * width)];
                    b2 = mag[(x-dx) + ((y+dy) * width)];
                    A = (Math.abs(diffx[x + (y * width)]) - Math.abs(diffy[x + (y * width)]))*a1 + Math.abs(diffy[x + (y * width)])*a2;
                    B = (Math.abs(diffx[x + (y * width)]) - Math.abs(diffy[x + (y * width)]))*b1 + Math.abs(diffy[x + (y * width)])*b2;
                    point = mag[x + (y * width)] * Math.abs(diffx[x + (y * width)]);
                    if(point >= A && point > B) {
                        val = Math.abs(diffx[x + (y * width)]);
                        output[x + (y * width)] = 0xff000000 | (val << 16 | val << 8 | val);
                    }
                    else {
                        val = 0;
                        output[x + (y * width)] = 0xff000000 | (val << 16 | val << 8 | val);
                    }
                }
                else
                {
                    a1 = mag[(x) + ((y-dy) * width)];
                    a2 = mag[(x+dx) + ((y-dy) * width)];
                    b1 = mag[(x) + ((y+dy) * width)];
                    b2 = mag[(x-dx) + ((y+dy) * width)];
                    A = (Math.abs(diffy[x + (y * width)]) - Math.abs(diffx[x + (y * width)]))*a1 + Math.abs(diffx[x + (y * width)])*a2;
                    B = (Math.abs(diffy[x + (y * width)]) - Math.abs(diffx[x + (y * width)]))*b1 + Math.abs(diffx[x + (y * width)])*b2;
                    point = mag[x + (y * width)] * Math.abs(diffy[x + (y * width)]);
                    if(point >= A && point > B) {
                        val = Math.abs(diffy[x + (y * width)]);
                        output[x + (y * width)] = 0xff000000 | (val << 16 | val << 8 | val);
                    }
                    else {
                        val = 0;
                        output[x + (y * width)] = 0xff000000 | (val << 16 | val << 8 | val);
                    }
                }
            }
        }

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = outputImage.getRaster();
        raster.setPixels(0, 0, width, height, output);
        return outputImage;

    }

    // algorithm from
    // CS+13 - An Efficient Universal Noise Removal Algorithm Combining Spatial Gradient and Impulse statistic - MPiE
    private void findIntensityGradients(BufferedImage img) {
            int width = img.getWidth();
            int height = img.getHeight();

            for (int row = 0; row < width; row++) {
                for (int col = 0; col < height; col++) {
                    //double[] valuesAroundPixel = new double[9];
                    // currently just using pixel val

                    int imgPix = new Color(img.getRGB(row, col)).getRed();
                    //float val = -(float) (Math.pow(imgPix, 2) / (2 * Math.pow(SIGMA_G[5], 2)));
                    float val = -(float) (Math.pow(imgPix, 2) / (2 * Math.pow(imgPix, 2)));
                    weightedG[row][col] = (float) Math.exp(val);
                }
            }
    }

    private BufferedImage applyGaussianFilter(BufferedImage img) {
            IMOperation op = new IMOperation();
            op.addImage();

            op.gaussianBlur(this.threshold);
            op.addImage("png:-");

            ConvertCmd convert = new ConvertCmd();
            Stream2BufferedImage s2b = new Stream2BufferedImage();
            convert.setOutputConsumer(s2b);

        try {
            convert.run(op,img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s2b.getImage();
    }
}
