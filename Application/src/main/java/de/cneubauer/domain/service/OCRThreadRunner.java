package de.cneubauer.domain.service;

import de.cneubauer.ocr.tesseract.TesseractWrapper;

import java.io.File;

/**
 * Created by Christoph Neubauer on 29.11.2016.
 * This class can be used as a thread to execute ocr
 * Replaced by ScanTask
 */
@Deprecated
public class OCRThreadRunner implements Runnable {
    private File[] files;
    private volatile String[] results;
    private volatile int done = 0;
    private volatile String currentFile;

    public OCRThreadRunner(File[] filesToScan) {
        this.files = filesToScan;
        this.results = new String[filesToScan.length];
    }

    @Override
    public void run() {
        for (int i = 0; i < files.length; i++){
            try {
                this.currentFile = files[i].getName();
                TesseractWrapper wrapper = new TesseractWrapper();
                this.results[i] = wrapper.initOcr(files[i].getPath());
                this.done++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String[] getResults() {
        return results;
    }

    public int getDone() {
        return done;
    }

    public String getCurrentFile() {
        return currentFile;
    }
}
