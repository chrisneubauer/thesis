package de.cneubauer.ocr.tesseract;

import de.cneubauer.ocr.OCRStrategy;
import de.cneubauer.util.config.ConfigHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

/**
 * Created by Christoph Neubauer on 09.12.2016.
 * Worker for multiple Tesseract Threads
 **/
public class TesseractWorker implements Runnable {
    private String result;
    private File fileToScan;
    private BufferedImage imgToScan;
    private boolean runWithHocr;

    public TesseractWorker(BufferedImage img, boolean hocr) {
        this.imgToScan = img;
        this.fileToScan = null;
        this.runWithHocr = hocr;
    }

    /**
     * Executes tesseract ocr using a wrapper
     * The result can be obtained using the getResultIfFinished() method
     */
    @Override
    public void run() {
        if (!Objects.equals(ConfigHelper.getOCREngine(), "Tesseract")) {
            // here could other ocr engines be implemented
        } else {
            OCRStrategy wrapper = new TesseractWrapper();
            if (this.imgToScan == null) {
                this.result = wrapper.initOcr(this.fileToScan, runWithHocr);
            } else {
                this.result = wrapper.initOcr(this.imgToScan, runWithHocr);
            }
            Logger.getLogger(this.getClass()).log(Level.INFO, "Finished OCR");
        }
    }

    /**
     * @return  the ocr result as a String or null when still in ocr process
     */
    public String getResultIfFinished() {
        return this.result;
    }
}
