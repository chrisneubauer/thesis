package de.cneubauer.ocr.tesseract;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Christoph Neubauer on 09.12.2016.
 * Worker for multiple Tesseract Threads
 **/
public class TesseractWorker implements Runnable {
    private String result;
    private File fileToScan;
    private BufferedImage imgToScan;

    public TesseractWorker(File f) {
        this.fileToScan = f;
        this.imgToScan = null;
    }

    public TesseractWorker(BufferedImage img) {
        this.imgToScan = img;
        this.fileToScan = null;
    }

    /**
     * Executes tesseract ocr using a wrapper
     * The result can be obtained using the getResultIfFinished() method
     */
    @Override
    public void run() {
        TesseractWrapper wrapper = new TesseractWrapper();
        if (this.imgToScan == null) {
            this.result = wrapper.initOcr(this.fileToScan);
        } else {
            this.result = wrapper.initOcr(this.imgToScan);
        }
        Logger.getLogger(this.getClass()).log(Level.INFO, "Finished OCR");
    }

    /**
     * @return  the ocr result as a String or null when still in ocr process
     */
    public String getResultIfFinished() {
        return this.result;
    }
}
