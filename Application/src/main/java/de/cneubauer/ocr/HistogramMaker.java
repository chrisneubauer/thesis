package de.cneubauer.ocr;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Christoph Neubauer on 14.11.2016.
 * Creates Histogram by calculating the amount of white per row
 * Used to find out the rows with text
 * As proposed in Text Pre-processing and Text Segmentation for OCR
 * Archana A. Shinde#1, D.G.Chougule*
 * 2012 in IJCSET
 */
public class HistogramMaker {
    private boolean[] importantRows;
    private double minThreshold = 0.2;
    private double maxThreshold = 0.8;

    public void setMinThreshold(double minThreshold) {
        this.minThreshold = minThreshold;
    }

    public void setMaxThreshold(double maxThreshold) {
        this.maxThreshold = maxThreshold;
    }

    public BufferedImage makeVerticalHistogram(BufferedImage input) {
        int width = input.getWidth();
        long[] values = new long[width];
        this.importantRows = new boolean[width];
        long maxValue = 0;

        for (int col = 0; col < width; col++) {
            long value = this.sumValuesInRow(input, false, col);
            values[col] = value;
            if (maxValue < value) {
                maxValue = value;
            }
        }

        if (maxValue > 400) {
            this.adjustValues(values, maxValue);
            maxValue = 400;
        }

        BufferedImage output = new BufferedImage(width, 400, BufferedImage.TYPE_INT_RGB);
        for (int col = 0; col < width; col++) {
            // System.out.println("Calculating column for value " + values[col]);
            for (int row = 0; row < 400; row++) {
                // fill from the bottom until value reached
                this.fillWithColor(col, row, values[col], output);
            }
        }

        return output;
    }

    public BufferedImage makeHistogram(BufferedImage input) {
        int length = input.getHeight();
        long[] values = new long[length];
        this.importantRows = new boolean[length];
        long maxValue = 0;

        for (int row = 0; row < length; row++) {
            long value = this.sumValuesInRow(input, true, row);
            values[row] = value;
            if (maxValue < value) {
                maxValue = value;
            }
        }

        // adjust values to 400 pixel
        if (maxValue > 400) {
            this.adjustValues(values, maxValue);
            maxValue = 400;
        }

        BufferedImage output = new BufferedImage(length, 400, BufferedImage.TYPE_INT_RGB);
        for (int col = 0; col < length; col++) {
           // System.out.println("Calculating column for value " + values[col]);
            for (int row = 0; row < 400; row++) {
                // fill from the bottom until value reached
                this.fillWithColor(col, row, values[col], output);
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

    // true when in row, false when in column
    private long sumValuesInRow(BufferedImage input, boolean inRow, int rowOrCol) {
        long value = 0;
        if (inRow) {
            for (int col = 0; col < input.getWidth(); col++) {
                value += new Color(input.getRGB(col, rowOrCol)).getRed();
            }
        } else {
            for (int row = 0; row < input.getHeight(); row++) {
                value += new Color(input.getRGB(rowOrCol, row)).getRed();
            }
        }
        return value;
    }

    private void adjustValues(long[] values, long maxValue) {
        for (int i = 0; i < values.length; i++) {
            double diff = (double) values[i] / (double) maxValue;
            double result = diff * 400;
            values[i] = (int) result;
        }
    }

    private void fillWithColor(int col, int row, long value, BufferedImage output) {
        if (value < row) {
            output.setRGB(col, row, 0);
        } else {
            output.setRGB(col, row, new Color(255,255,255).getRGB());
        }
    }

}
