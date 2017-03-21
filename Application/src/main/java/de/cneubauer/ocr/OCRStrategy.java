package de.cneubauer.ocr;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Christoph on 21.03.2017.
 * Defines a strategy how ocr should be done
 */
public interface OCRStrategy {
    String initOcr(BufferedImage file, boolean hocr);

    String initOcr(File file, boolean hocr);

    String initOcr(BufferedImage file);

    String initOcr(File file);
}
