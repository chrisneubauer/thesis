package de.cneubauer.ocr;

import org.im4java.core.ConvertCmd;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import static com.lowagie.text.pdf.PdfName.op;

/**
 * Created by Christoph Neubauer on 14.11.2016.
 * Creates Histogram by calculating the amount of white per row
 * Used to find out the rows with text
 * As proposed in Text Pre-processing and Text Segmentation for OCR
 * Archana A. Shinde#1, D.G.Chougule*
 * 2012 in IJCSET
 */
public class HistogramMaker {
    private BufferedImage inputFile;
    private boolean[] importantRows;

    public void setMinThreshold(double minThreshold) {
        this.minThreshold = minThreshold;
    }

    public void setMaxThreshold(double maxThreshold) {
        this.maxThreshold = maxThreshold;
    }

    private double minThreshold = 0.2;
    private double maxThreshold = 0.8;

    public HistogramMaker(BufferedImage imageToProcess) {
        this.inputFile = imageToProcess;
    }


    public BufferedImage makeHistogram(BufferedImage input) {
        int height = input.getHeight();
        int width = input.getWidth();
        long[] values = new long[height];
        this.importantRows = new boolean[height];
        long maxValue = 0;

        for (int row = 0; row < height; row++) {
            long value = 0;
            for (int col = 0; col < width; col++) {
                value += new Color(input.getRGB(col,row)).getRed();
            }
            values[row] = value;
            if (maxValue < value) {
                maxValue = value;
            }
        }

        // adjust values to 400 pixel
        // zB 300.000 is max -> 300.000 = 400
        // -> 150.000 = 200
        // aber wenn 200.000 max -> 200.000 = 400
        // -> 100.000 = 200
        // mit WERT / max * 400 ->
        // 280.000 / 300.000 * 400 =
        if (maxValue > 400) {
            for (int i = 0; i < values.length; i++) {
                double diff = (double) values[i] / (double) maxValue;
                double result = diff * 400;
                values[i] = (int) result;
            }
            maxValue = 400;
        }

        BufferedImage output = new BufferedImage(height, 400, BufferedImage.TYPE_INT_RGB);
        for (int col = 0; col < height; col++) {
           // System.out.println("Calculating column for value " + values[col]);
            for (int row = 0; row < 400; row++) {
                // fill from the bottom until value reached
                if (values[col] < row) {
                    output.setRGB(col, row, 0);
                } else {
                    output.setRGB(col, row, new Color(255,255,255).getRGB());
                }
            }
        }

        for (int row = 0; row < values.length; row++) {
            long value = values[row];
            this.importantRows[row] = value > maxValue * minThreshold && value < maxValue * maxThreshold;
        }
        return output;
    }

    public boolean[] getImportantRows() {
        return importantRows;
    }
}
