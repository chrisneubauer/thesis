package de.cneubauer.ocr;

import de.cneubauer.util.config.ConfigHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Christoph Neubauer on 12.12.2016.
 * This class holds logic to partition the file into multiple smaller images
 * It will separate the file into header, footer and body
 * The header will be separated again into left and right
 * The header + body as well as body + footer will overlap to make sure no information is left out
 */
public class ImagePartitioner {
    private File scanFile;
    private BufferedImage fullImage;
    private BufferedImage leftHeader;
    private BufferedImage rightHeader;
    private BufferedImage body;
    private BufferedImage footer;

    public ImagePartitioner(File fileToScan) {
        this.scanFile = fileToScan;
    }

    public ImagePartitioner(BufferedImage fileToScan) {
        this.scanFile = null;
        this.fullImage = fileToScan;
    }

    // @return array of images in the following order:
    // 1. left header image
    // 2. right header image
    // 3. body image
    // 4. footer image
    public BufferedImage[] process() {
        try {
            if (this.scanFile != null) {
                this.convertFileToImage();
            }
            BufferedImage header = this.cropHeader();
            this.separateHeader(header);
            this.cropBody();
            this.cropFooter();
            if (ConfigHelper.isDebugMode()) {
                File leftHeaderFile = new File(".\\temp\\leftHeader.png");
                File rightHeaderFile = new File(".\\temp\\rightHeader.png");
                File bodyFile = new File(".\\temp\\body.png");
                File footerFile = new File(".\\temp\\footer.png");
                ImageIO.write(this.leftHeader, "png", leftHeaderFile);
                ImageIO.write(this.rightHeader, "png", rightHeaderFile);
                ImageIO.write(this.body, "png", bodyFile);
                ImageIO.write(this.footer, "png", footerFile);
            }
            return new BufferedImage[] { this.leftHeader, this.rightHeader, this.body, this.footer};
        } catch (Exception ex) {
            Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to process image! Error: " + ex.getMessage());
        }
        return null;
    }

    private void convertFileToImage() throws IOException {
        if (this.scanFile != null) {
            if (this.scanFile.getName().endsWith(".pdf")) {
                PDDocument pdf = PDDocument.load(this.scanFile);
                PDFRenderer renderer = new PDFRenderer(pdf);
                this.fullImage = renderer.renderImageWithDPI(0, 600);
                pdf.close();
            } else {
                this.fullImage = ImageIO.read(this.scanFile);
            }
        }
    }

    private BufferedImage cropHeader() {
        // take 40% of image height
        int height = (int) (this.fullImage.getHeight() * 0.4);
        return this.fullImage.getSubimage(0, 0, this.fullImage.getWidth(), height);
    }

    private void separateHeader(BufferedImage header) {
        this.leftHeader = header.getSubimage(0, 0, header.getWidth() / 2, header.getHeight());
        this.rightHeader = header.getSubimage(header.getWidth() / 2, 0, header.getWidth() / 2, header.getHeight());
    }

    private void cropBody() {
        // take 30% - 80% of image (= 50%)
        int height = (int) (this.fullImage.getHeight() * 0.5);
        this.body = this.fullImage.getSubimage(0, (int) (this.fullImage.getHeight()*0.3), this.fullImage.getWidth(), height);
    }

    private void cropFooter() {
        // take last 30% of image
        int height = (int) (this.fullImage.getHeight() * 0.3);
        this.footer = this.fullImage.getSubimage(0, (int) (this.fullImage.getHeight()*0.7), this.fullImage.getWidth(), height);
    }
}
