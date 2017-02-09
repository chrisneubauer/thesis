package de.cneubauer.ocr.tesseract;

import de.cneubauer.util.config.ConfigHelper;
import net.sourceforge.tess4j.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode.OEM_TESSERACT_ONLY;

/**
 * Created by Christoph on 17.08.2016.
 * Uses Tess4J as a wrapper for googles Tesseract
 */
public class TesseractWrapper {
    private String language = ConfigHelper.getTesseractLanguages().getValue();
    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Initiates a Tesseract instance and setting language and configuration
     * @return  the ITesseract instance
     */
    private ITesseract getTesseractInstance() {
        logger.log(Level.INFO, "initiating tesseract instance");
        long time = System.currentTimeMillis();
        ITesseract instance = new Tesseract();
        logger.log(Level.INFO, "initialization of tesseract completed. Time taken: " + (System.currentTimeMillis() - time));

        instance.setDatapath(".");
        instance.setLanguage(this.getLanguage());
        instance.setTessVariable("tessedit_write_images", "true");
        instance.setOcrEngineMode(OEM_TESSERACT_ONLY);
        logger.log(Level.INFO, "Using language(s): " + this.getLanguage());

        List<String> configs = new ArrayList<>(4);
        configs.add(0, "tessdata\\configs\\api_config");
        configs.add(1, "tessdata\\configs\\digits");
        configs.add(2, "tessdata\\configs\\letters");
        configs.add(3, "tessdata\\configs\\hocr");
        instance.setConfigs(configs);
        return instance;
    }

    public String initOcr(BufferedImage file, String config, String value) {
        ITesseract instance = this.getTesseractInstance();
        if (config != null && value != null) {
            instance.setTessVariable(config, value);
        }

        String result = "";
        try {
            result = instance.doOCR(file);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public String initOcr(BufferedImage file, boolean hocr) {
        Tesseract instance = (Tesseract) this.getTesseractInstance();


        if (hocr) {
            instance.setHocr(true);
            instance.setPageSegMode(3);
            instance.setOcrEngineMode();
        }

        String result = "";
        try {
            result = instance.doOCR(file);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    /**
     * @param file  the file to be scanned
     * @return  the ocr result as a String
     */
    public String initOcr(BufferedImage file) {
        ITesseract instance = this.getTesseractInstance();

        String result = "";
        try {
            result = instance.doOCR(file);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    /**
     * @param path  the path where the file to be scanned is located
     * @return  the ocr result as a String
     */
    public String initOcr(String path) {
        File imageFile = new File(path);
        return this.initOcr(imageFile);
    }

    /**
     * @param file  the file to be scanned
     * @return  the ocr result as a String
     */
    public String initOcr(File file) {
        ITesseract instance = this.getTesseractInstance();

        String result = "";
        try {
            result = instance.doOCR(file);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public void getLayoutInformation(String path, String lang, File file) throws IOException {
        ITessAPI.TessBaseAPI api = TessAPI1.TessBaseAPICreate();
        TessAPI1.TessBaseAPIInit3(api, path, lang);
        TessAPI1.TessBaseAPISetPageSegMode(api, TessAPI1.TessPageSegMode.PSM_AUTO);
        FileChannel fc = FileChannel.open(file.toPath());
        fc.map(FileChannel.MapMode.READ_ONLY, 23,23);
        ByteBuffer image = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        int w1 = image.get(6);
        int w2 = image.get(7);
        int h1 = image.get(8);
        int h2 = image.get(9);
        int w = (w2 << 8) | w1;
        int h = (h2 << 8) | h1;
        TessAPI1.TessBaseAPISetImage(api, image, w, h, 8, 8*w);
        //TessAPI1.TessBaseAPISetImage(api, img, w, h, bpp, bpp*w);
        TessAPI1.TessBaseAPIGetUTF8Text(api);
        TessAPI1.TessBaseAPIInitForAnalysePage(api);
        ITessAPI.TessPageIterator iterator = TessAPI1.TessBaseAPIAnalyseLayout(api);
        ITessAPI.TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(api);
        ITessAPI.TessPageIterator pi = TessAPI1.TessResultIteratorGetPageIterator(ri);
    }

    /**
     * @return  the language tesseract should work with
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language  the language tesseract should work with
     */
    public void setLanguage(String language) {
        this.language = language;
    }
}