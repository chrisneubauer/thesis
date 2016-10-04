package de.cneubauer.ocr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tess4j.*;
/**
 * Created by Christoph on 17.08.2016.
 * Uses Tess4J as a wrapper for googles Tesseract
 */
public class TesseractWrapper {
    private String language = "deu+eng";

    public String initOcr(String path) {
        File imageFile = new File(path);
        ITesseract instance = new Tesseract();

        instance.setDatapath(".");
        instance.setLanguage(this.getLanguage());

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