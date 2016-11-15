package de.cneubauer.ocr.tesseract;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.cneubauer.util.config.ConfigHelper;
import net.sourceforge.tess4j.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode.OEM_TESSERACT_CUBE_COMBINED;
import static net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode.OEM_TESSERACT_ONLY;

/**
 * Created by Christoph on 17.08.2016.
 * Uses Tess4J as a wrapper for googles Tesseract
 */
public class TesseractWrapper {
    private String language = ConfigHelper.getValue("tesseractLanguage");
    private final Logger logger = Logger.getLogger(this.getClass());

    public String initOcr(BufferedImage file) {
        logger.log(Level.INFO, "initiating tesseract instance");
        long time = System.currentTimeMillis();
        ITesseract instance = new Tesseract();
        logger.log(Level.INFO, "initialization of tesseract completed. Time taken: " + (System.currentTimeMillis() - time));

        instance.setDatapath(".");
        instance.setLanguage(this.getLanguage());

        instance.setOcrEngineMode(OEM_TESSERACT_ONLY);
        logger.log(Level.INFO, "Using language(s): " + this.getLanguage());

        List<String> configs = new ArrayList<>(4);
        configs.add(0, "tessdata\\configs\\api_config");
        configs.add(1, "tessdata\\configs\\digits");
        configs.add(2, "tessdata\\configs\\letters");
        configs.add(3, "tessdata\\configs\\hocr");
        instance.setConfigs(configs);

        String result = "";
        try {
            result = instance.doOCR(file);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public String initOcr(String path) {
        File imageFile = new File(path);

        logger.log(Level.INFO, "initiating tesseract instance");
        long time = System.currentTimeMillis();
        ITesseract instance = new Tesseract();
        logger.log(Level.INFO, "initialization of tesseract completed. Time taken: " + (System.currentTimeMillis() - time));

        instance.setDatapath(".");
        instance.setLanguage(this.getLanguage());
        instance.setOcrEngineMode(OEM_TESSERACT_ONLY);
        logger.log(Level.INFO, "Using language(s): " + this.getLanguage());

        List<String> configs = new ArrayList<>(4);
        configs.add(0, "tessdata\\configs\\api_config");
        configs.add(1, "tessdata\\configs\\digits");
        configs.add(2, "tessdata\\configs\\letters");
        configs.add(3, "tessdata\\configs\\hocr");
        instance.setConfigs(configs);

        String result = "";
        try {
            result = instance.doOCR(imageFile);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}