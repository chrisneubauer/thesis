package de.cneubauer.ocr.tesseract;

import com.google.common.io.Files;
import de.cneubauer.ocr.OCRStrategy;
import de.cneubauer.util.config.ConfigHelper;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode.OEM_TESSERACT_ONLY;

/**
 * Created by Christoph on 17.08.2016.
 * Uses Tess4J as a wrapper for googles Tesseract
 */
public class TesseractWrapper implements OCRStrategy {
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

    private String initOcr(BufferedImage imageFile, File file, boolean hocr) {
        Tesseract instance = (Tesseract) this.getTesseractInstance();

        if (hocr) {
            instance.setHocr(true);
            instance.setPageSegMode(3);
        }

        String result = "";
        try {
            if (imageFile != null) {
                result = instance.doOCR(imageFile);
            } else {
                result = instance.doOCR(file);
            }
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        if (ConfigHelper.isDebugMode() && hocr) {
            File hocrFile = new File(".\\temp\\hocr.xml");
            File hocrImage = new File(".\\temp\\hocr.jpg");
            try {
                FileUtils.writeByteArrayToFile(hocrFile, result.getBytes(Charset.forName("UTF-8")));
                if (imageFile == null) {
                    FileUtils.writeByteArrayToFile(hocrImage, Files.toByteArray(file));
                } else {
                    ImageIO.write(imageFile, "jpg", hocrImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public String initOcr(BufferedImage file, boolean hocr) {
        return this.initOcr(file, null, hocr);
    }

    @Override
    public String initOcr(File file, boolean hocr) {
        return this.initOcr(null, file, hocr);
    }

    /**
     * @param file  the file to be scanned
     * @return  the ocr result as a String
     */
    @Override
    public String initOcr(BufferedImage file) {
        return this.initOcr(file, true);
    }

    /**
     * @param file  the file to be scanned
     * @return  the ocr result as a String
     */
    @Override
    public String initOcr(File file) {
        return this.initOcr(file, true);
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