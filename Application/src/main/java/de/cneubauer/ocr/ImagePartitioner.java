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
    private BufferedImage table;

    public ImagePartitioner(File fileToScan) {
        this.scanFile = fileToScan;
    }

    public ImagePartitioner(BufferedImage fileToScan) {
        this.scanFile = null;
        this.fullImage = fileToScan;
    }

    /**
     * Processes the file, splits it into the following parts and temporarily saves it as a .png
     * <p><ul>
     *     <li>[0]: left header image</li>
     *     <li>[1]: right header image</li>
     *     <li>[2]: body image</li>
     *     <li>[3]: footer image</li>
     * </ul></p>
     * @return the array of images in the given order
     */
    public BufferedImage[] process() {
        try {
            if (this.scanFile != null) {
                this.convertFileToImage();
            }
            BufferedImage header = this.cropHeader();
            this.separateHeader(header);
            this.cropBody();
            this.cropFooter();
            this.table = this.findTableInInvoice(this.body, false);
            if (this.table != null) {
                this.body = this.table;
                this.table.flush();
            }
            if (ConfigHelper.isDebugMode()) {
                File leftHeaderFile = new File(".\\temp\\leftHeader.png");
                File rightHeaderFile = new File(".\\temp\\rightHeader.png");
                File bodyFile = new File(".\\temp\\body" + System.currentTimeMillis() + ".png");
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

    public BufferedImage findTableInInvoice(BufferedImage image, boolean whiteTable) {
        //int xPosOfFirstHorizontalLine = 0;
        int yPosOfFirstHorizontalLine = 0;
        //int xPosOfFirstVerticalLine;
        //int yPosOfFirstVerticalLine;

        //int xPosOfLastHorizontalLine = 0;
        int yPosOfLastHorizontalLine = 0;
        //int xPosOfLastVerticalLine;
        //int yPosOfLastVerticalLine;


        //Normalizer normalizer = new Normalizer();
        //BufferedImage normalizedImage = normalizer.process(image);
        //BufferedImage thickened = this.thickenImage(normalizedImage);
        HistogramMaker maker = new HistogramMaker();

        //expect image to be preprocessed
        //ImagePreprocessor preprocessor = new ImagePreprocessor(image);
        //BufferedImage processedImage = preprocessor.preprocess();
        BufferedImage processedImage = image;
        BufferedImage histogram;
        BufferedImage tobeRemoved = maker.makeVerticalHistogram(processedImage, true);
        if (whiteTable) {
            histogram = maker.makeWhiteHistogram(processedImage);
        } else {
            histogram = maker.makeHistogram(processedImage);
        }
        long[] values = maker.getValues();
        long max = maker.getMaxValue();

        int maxWidth = image.getWidth();
        int maxHeight = image.getHeight();

        String outputDir = ".\\temp\\";
        File test = new File(outputDir + "histogram.png");
        try {
            ImageIO.write(tobeRemoved, "png", test);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < values.length; i++) {
            if (values[i] < max * 0.5) {
                if (yPosOfFirstHorizontalLine == 0) {
                    yPosOfFirstHorizontalLine = i;
                } else {
                    yPosOfLastHorizontalLine = i;
                }
            }
        }
        if (yPosOfFirstHorizontalLine == 0) {
            // original image return since no table has been found
            return processedImage;
        } else {
            return processedImage.getSubimage(0, yPosOfFirstHorizontalLine, maxWidth, yPosOfLastHorizontalLine - yPosOfFirstHorizontalLine);
        }
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
