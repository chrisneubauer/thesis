package de.cneubauer.ocr;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;

/**
 * Created by Christoph Neubauer on 12.11.2016.
 * RankFilter to reduce lines in the image
 */
@Deprecated
public class RankFilter {
    private int filterWidth = 5, filterHeight = 5;

    public BufferedImage filterImageHorizontally(BufferedImage input) {
        double[][] filter = new double[][]
        {
            {0, 0, -1, 0, 0},
            {0, 0, -1, 0, 0},
            {0, 0, 2, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
        } ;

        double factor = 1.0;
        double bias = 0.0;

        return this.apply(filter, input, factor, bias);
    }

    public BufferedImage filterImageVertically(BufferedImage input) {
        double[][] filter = new double[][]
        {
                {0,  0, -1,  0,  0},
                {0,  0, -1,  0,  0},
                {0,  0,  4,  0,  0},
                {0,  0, -1,  0,  0},
                {0,  0, -1,  0,  0},
        };

        double factor = 1.0;
        double bias = 0.0;
        return this.apply(filter, input, factor, bias);
    }

    private BufferedImage apply(double[][] filter, BufferedImage image, double factor, double bias) {
        //apply the filter
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage outputImageTest = image;
        int[] orig=new int[width*height];

        PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height, orig, 0, width);
        try {
            grabber.grabPixels();
        }
        catch(InterruptedException e2) {
            System.out.println("error: " + e2);
        }
        int[] imageArray = orig;
        int[] result = imageArray;
        for (int x = 2; x < width-2; x++)
            for (int y = 2; y < height-2; y++) {
                double red = 0.0; //, green = 0.0, blue = 0.0;

                red += new Color(image.getRGB(x-2, y-2)).getRed() * filter[0][0];
                red += new Color(image.getRGB(x-1, y-2)).getRed()* filter[0][1];
                red += new Color(image.getRGB(x, y-2)).getRed()* filter[0][2];
                red += new Color(image.getRGB(x+1, y-2)).getRed()* filter[0][3];
                red += new Color(image.getRGB(x+2, y-2)).getRed()* filter[0][4];

                red += new Color(image.getRGB(x-2, y-1)).getRed()* filter[1][0];
                red += new Color(image.getRGB(x-1, y-1)).getRed()* filter[1][1];
                red += new Color(image.getRGB(x, y-1)).getRed()* filter[1][2];
                red += new Color(image.getRGB(x+1, y-1)).getRed()* filter[1][3];
                red += new Color(image.getRGB(x+2, y-1)).getRed()* filter[1][4];

                red += new Color(image.getRGB(x-2, y)).getRed()* filter[2][0];
                red += new Color(image.getRGB(x-1, y)).getRed()* filter[2][1];
                red += new Color(image.getRGB(x, y)).getRed()* filter[2][2];
                red += new Color(image.getRGB(x+1, y)).getRed()* filter[2][3];
                red += new Color(image.getRGB(x+2, y)).getRed()* filter[2][4];

                red += new Color(image.getRGB(x-2, y+1)).getRed()* filter[3][0];
                red += new Color(image.getRGB(x-1, y+1)).getRed()* filter[3][1];
                red += new Color(image.getRGB(x, y+1)).getRed()* filter[3][2];
                red += new Color(image.getRGB(x+1, y+1)).getRed()* filter[3][3];
                red += new Color(image.getRGB(x+2, y+1)).getRed()* filter[3][4];

                red += new Color(image.getRGB(x-2, y+2)).getRed()* filter[4][0];
                red += new Color(image.getRGB(x-1, y+2)).getRed()* filter[4][1];
                red += new Color(image.getRGB(x, y+2)).getRed()* filter[4][2];
                red += new Color(image.getRGB(x+1, y+2)).getRed()* filter[4][3];
                red += new Color(image.getRGB(x+2, y+2)).getRed()* filter[4][4];

                int value = Math.min(Math.abs((int) (factor * red + bias)), 255);
                outputImageTest.setRGB(x,y,value);

                //multiply every value of the filter with corresponding image pixel
                /*for (int filterY = 0; filterY < filterHeight; filterY++)
                    for (int filterX = 0; filterX < filterWidth; filterX++) {
                        int imageX = (x - filterWidth / 2 + filterX + width) % width;
                        int imageY = (y - filterHeight / 2 + filterY + height) % height;
                        red += imageArray[imageY * width + imageX] * filter[filterY][filterX];
                        //green += image[imageY * width + imageX].g * filter[filterY][filterX];
                        //blue += image[imageY * width + imageX].b * filter[filterY][filterX];
                    }
*/
                //take absolute value and truncate to 255
                //result[y * width + x] = Math.min(Math.abs((int) (factor * red + bias)), 255);
                //result[y * width + x].g = Math.min(Math.abs(int(factor * green + bias)), 255);
                //result[y * width + x].b = Math.min(Math.abs(int(factor * blue + bias)), 255);
            }
        //BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        //WritableRaster raster = resultImage.getRaster();
        //raster.setPixels(0, 0, width, height, result);
        //return resultImage;
        return outputImageTest;
    }
}
