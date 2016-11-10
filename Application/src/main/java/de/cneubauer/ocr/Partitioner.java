package de.cneubauer.ocr;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Christoph Neubauer on 10.11.2016.
 * Thiss class scans a pdf form and partitions it into three parts: header, body and footer
 * The header contains the information about debitor and creditor as well as invoice date and other meta data
 * Body contains all the positions and services done
 * The footer has the total value to pay as well as payment information and skonto / tax information
 */
public class Partitioner {
    public byte[] getCompleteFile() {
        return completeFile;
    }

    private byte[] completeFile;
    private byte[] header;
    private byte[] body;
    private byte[] footer;

    public Partitioner(byte[] completeFile) {
        this.completeFile = completeFile;
    }

    public void splitPage() throws IOException {
        PDDocument pdf = PDDocument.load(this.completeFile);
        //PDDocument pdf = PDDocument.load(this.pdfFile);
        PDFRenderer renderer = new PDFRenderer(pdf);
        BufferedImage image = renderer.renderImageWithDPI(0, 600);
        //BufferedImage image = renderer.renderImage(0);
        int totalY = image.getHeight();
        int totalX = image.getWidth();

        //TODO: Make it more intelligent
        BufferedImage headerImage = image.getSubimage(0,0,totalX, totalY / 3);
        BufferedImage bodyImage = image.getSubimage(0,totalY / 3,totalX, totalY / 2);
        BufferedImage footerImage = image.getSubimage(0,totalY / 3 + totalY / 2,totalX, totalY / 6);

        File headerImagefile = new File(".\\temp\\header.png");
        System.out.println("Image Created -> "+ headerImagefile.getName());
        ImageIO.write(headerImage, "png", headerImagefile);

        File bodyImagefile = new File(".\\temp\\body.png");
        System.out.println("Image Created -> "+ bodyImagefile.getName());
        ImageIO.write(bodyImage, "png", bodyImagefile);

        File footerImagefile = new File(".\\temp\\footer.png");
        System.out.println("Image Created -> "+ footerImagefile.getName());
        ImageIO.write(footerImage, "png", footerImagefile);
    }

    public void splitFile() throws IOException {
        PDDocument pdf = PDDocument.load(this.completeFile);
        PDFRenderer renderer = new PDFRenderer(pdf);
        int pages = pdf.getNumberOfPages();
        System.out.println("Total files to be converted -> "+ pages);

        for (int i = 0; i < pages; i++) {
            BufferedImage image = renderer.renderImage(i);
            File outputfile = new File("/temp/" + pdf.getDocumentInformation().getTitle() +"_"+ i +".png");
            System.out.println("Image Created -> "+ outputfile.getName());
            ImageIO.write(image, "png", outputfile);
        }
        pdf.close();
    }

    public byte[] getHeader() {
        return this.header;
    }

    public byte[] getBody() {
        return this.body;
    }

    public byte[] getFooter() {
        return this.footer;
    }

    public void separateImageByLayout(BufferedImage img) {
        boolean separationMode = false;

        // if puffer capacity reached, we think that the area is done
        int puffer = 10000000;
        int startAreaX = 0;
        int startAreaY = 0;
        int endAreaX = 0;
        int endAreaY = 0;
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j,i));
                boolean isWhite = color.getRed() > 230 && color.getBlue() > 230 && color.getGreen() > 230;
                if (!isWhite && !separationMode) {
                    separationMode = true;
                    startAreaX = j;
                    startAreaY = i;
                }
                else if (!isWhite && separationMode) {
                    //continue inside the area
                }
                else if (isWhite && separationMode) {
                    puffer--;
                    if (puffer == 0) {
                        endAreaX = j;
                        endAreaY = i;
                        separationMode = false;
                        int width;
                        int height;
                        // startX = 300
                        // endX = 120 -> 120 anfangen
                        if (endAreaX < startAreaX) {
                            width = startAreaX - endAreaX + 1;
                            startAreaX = endAreaX;
                        } else {
                            width = endAreaX - startAreaX + 1;
                        }
                        if (endAreaY < startAreaY) {
                            height = startAreaY - endAreaY + 1;
                            startAreaX = endAreaX;
                        } else {
                            height = endAreaY - startAreaY + 1;
                        }
                        this.makeAreaImage(startAreaX, startAreaY, width, height, img);
                        puffer = 10000000;
                    }
                }
            }
        }
    }

    public void separateImageByLayoutAndChunk(BufferedImage img) {
        boolean separationMode = false;

        // if puffer capacity reached, we think that the area is done
        int puffer = 10000000;
        int startAreaX = 0;
        int startAreaY = 0;
        int endAreaX = 0;
        int endAreaY = 0;

        // should be 10 px each
        int chunksX = img.getWidth() / 10;
        int chunksY = img.getHeight() / 10;
        int totalChunks = chunksX * chunksY;

        BufferedImage[] imgInChunks = new BufferedImage[totalChunks];
        int count = 0;

        int counterColumns = 0;
        int counterRows = 0;

        for (int i = 0; i < totalChunks; i++) {
            boolean relevant = false;
            Color[] colors = new Color[100];
            for (int totalX = 0; totalX < img.getWidth() + counterColumns; totalX++) {
                for (int totalY = 0; totalY < img.getHeight() + counterRows; totalY++) {
                    // in here, iterate through the chunk
                    Color color = new Color(img.getRGB(totalX,totalY));
                }
            }
            int[] avg = this.averageColor(colors);
            boolean isWhite = avg[0] > 230 && avg[1] > 230 && avg[2] > 230;
            relevant = !isWhite;
            if (relevant) {
                makeAreaImage(counterColumns, counterRows, 10, 10, img);
            }
        }
        for (int y = 0; y < chunksY; y++) {
            for (int x = 0; x < chunksX; x++) {
                imgInChunks[count] = new BufferedImage(chunksX, chunksY, img.getType());
                count++;
            }
        }

        for (int i = 1; i < imgInChunks.length; i++) {
            File imagefile = new File(".\\temp\\areas\\" + i + ".png");
            System.out.println("Image Created -> " + imagefile.getName());
            try {
                ImageIO.write(imgInChunks[i], "png", imagefile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private int[] averageColor(Color[] values) {
        long redBucket = 0;
        long greenBucket = 0;
        long blueBucket = 0;
        long pixelCount = values.length;

        //values contains:
        //row
        //column

        for (Color c : values) {
            //boolean isWhite = c.getRed() > 230 && c.getBlue() > 230 && c.getGreen() > 230;
            redBucket += c.getRed();
            greenBucket += c.getGreen();
            blueBucket += c.getBlue();
        }

        return new int[] {(int) (redBucket / pixelCount), (int) (greenBucket / pixelCount), (int) (blueBucket / pixelCount)};
        //Color averageColor = Color.rgb(redBucket / pixelCount,
          //      greenBucket / pixelCount,
            //    blueBucket / pixelCount);

    }

    private void makeAreaImage(int startX, int startY, int endX, int endY, BufferedImage img) {
        try {
            File imagefile = new File(".\\temp\\areas\\" + startX + startY + "-" + endX + endY + ".png");
            System.out.println("Image Created -> " + imagefile.getName());
            ImageIO.write(img.getSubimage(startX, startY, endX, endY), "png", imagefile);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass()).log(Level.WARN, "Unable to write area image");
        }
    }
}
