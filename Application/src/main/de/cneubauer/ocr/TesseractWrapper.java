package de.cneubauer.ocr;

import java.io.File;
import net.sourceforge.tess4j.*;
/**
 * Created by Christoph on 17.08.2016.
 * Uses Tess4J as a wrapper for googles Tesseract
 */
public class TesseractWrapper {

    public String initOcr(String path) {
        // ImageIO.scanForPlugins(); // for server environment
        File imageFile = new File(path);
        ITesseract instance = new Tesseract(); // JNA Interface Mapping
        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping
        // instance.setDatapath("<parentPath>"); // replace <parentPath> with path to parent directory of tessdata
        // instance.setLanguage("eng");
        String result = "";
        try {
            result = instance.doOCR(imageFile);
            //System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }
}