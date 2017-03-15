package de.cneubauer.ocr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Christoph Neubauer on 14.11.2016.
 * This class uses the Histogram of the HistogramMaker
 * And crops out areas where important rows are
 * param threshold defines how many important rows have to be together in order to be cropped out
 */
public class ImageCropper {
    private int threshold = 1;
    private boolean[] importantRows;
    private BufferedImage imageToCrop;
    private int counter = 1;

    public ImageCropper(int threshold) {
        this.threshold = threshold;
    }

    public void cropImages() {
        int width = this.imageToCrop.getWidth();
        String[] rows = this.calcRows();

        for (String s : rows) {
            if (s != null) {
                int start = Integer.parseInt(s.split("-")[0]);
                int end = Integer.parseInt(s.split("-")[1]);
                BufferedImage sub = this.imageToCrop.getSubimage(0, start, width, end - start);

                String outputDir = ".\\temp\\areas\\";
                File test = new File(outputDir + "cropped_" + this.counter + ".png");
                try {
                    ImageIO.write(sub, "png", test);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.counter ++;
            }
        }
    }

    private String[] calcRows() {
        String[] rows = new String[this.importantRows.length];
        int count = 0;
        for (int i = 0; i < this.importantRows.length; i++) {
            boolean isImportant = this.importantRows[i];
            for (int j = 0; j < this.threshold; j++) {
                if (i+j < this.importantRows.length) {
                    isImportant &= this.importantRows[i + j];
                } else {
                    isImportant = false;
                }
            }
            if (isImportant) {
                rows[count] = i + "-" + (i+this.threshold);
                i = i + this.threshold;
                count++;
            }
        }
        return rows;
    }

    public void setImageToCrop(BufferedImage imageToCrop, boolean[] importantRows) {
        this.imageToCrop = imageToCrop;
        this.importantRows = importantRows;
    }
}
