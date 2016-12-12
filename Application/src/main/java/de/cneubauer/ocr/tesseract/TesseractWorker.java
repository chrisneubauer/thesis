package de.cneubauer.ocr.tesseract;

import java.io.File;

/**
 * Created by Christoph Neubauer on 09.12.2016.
 * Worker for multiple Tesseract Threads
 **/
public class TesseractWorker implements Runnable {
    private String result;
    private File fileToScan;

    public TesseractWorker(File f) {
        this.fileToScan = f;
    }

    @Override
    public void run() {
        TesseractWrapper wrapper = new TesseractWrapper();
        this.result = wrapper.initOcr(fileToScan);
    }

    public String getResultIfFinished() {
        return this.result;
    }
}
