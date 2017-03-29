package de.cneubauer.ocr.tesseract;

import de.cneubauer.AbstractTest;
import de.cneubauer.ocr.ImagePreprocessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

public class TesseractWrapperTest extends AbstractTest {

    private TesseractWrapper wrapper;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        databaseChanged = false;
        wrapper = new TesseractWrapper();
    }

    @After
    public void tearDown() throws Exception {
        wrapper = null;
    }

    @Test
    @Ignore
    public void testOCRKeywordBruttoSum() throws Exception {
        String path1 = ".\\src\\test\\resources\\invoice\\KeywordSuccess\\Bruttosumme\\Scan_20160822_161042_012.jpg";
        String path2 = ".\\src\\test\\resources\\invoice\\KeywordSuccess\\Bruttosumme\\Scan_20160822_161042_019.jpg";
        String path3 = ".\\src\\test\\resources\\invoice\\KeywordSuccess\\Bruttosumme\\Scan_20160822_163306_001.jpg";
        String path4 = ".\\src\\test\\resources\\invoice\\KeywordSuccess\\Bruttosumme\\Scan_20160822_163306_005.jpg";
        String path5 = ".\\src\\test\\resources\\invoice\\KeywordSuccess\\Bruttosumme\\Scan_20160822_163306_007.jpg";
        String result1 = wrapper.initOcr(ImageIO.read(new File(path1)));
        String result2 = wrapper.initOcr(ImageIO.read(new File(path2)));
        String result3 = wrapper.initOcr(ImageIO.read(new File(path3)));
        String result4 = wrapper.initOcr(ImageIO.read(new File(path4)));
        String result5 = wrapper.initOcr(ImageIO.read(new File(path5)));

        if (result1 != null) {
            Assert.assertTrue(result1.contains("Bruttosumme"));
        }
        if (result2 != null) {
            Assert.assertTrue(result2.contains("Bruttosumme"));
        }
        if (result3 != null) {
            System.out.println(result3);
            boolean found = assertOCR(result3, "Bruttosumme");
            Assert.assertTrue(found);
        }
        if (result4 != null) {
            boolean found = assertOCR(result4, "Bruttosumme");
            Assert.assertTrue(found);
        }
        if (result5 != null) {
            System.out.println(result5);
            boolean found = assertOCR(result5, "Bruttosumme");
            Assert.assertTrue(found);
        }
    }

    @Test
    public void testImprovementByDictionaryAndPreprocessing() throws Exception{
        String path = "..\\Data\\Datenwerk4.pdf";
        //BufferedImage file = ImageIO.read(new File(path));
        ImagePreprocessor preprocessor = new ImagePreprocessor(path);

        BufferedImage inputImage = preprocessor.preprocess();

        this.wrapper.setLanguage("deu+eng");
        String output = this.wrapper.initOcr(inputImage);

        System.out.println(output);
    }

    @Test
    @Ignore
    public void testDifferenceBetweenSize() throws Exception {
        String path = ".\\src\\test\\resources\\invoice\\Resizing\\";
        this.doOcrAndSave(path + "body.jpg", path + "body.txt");
        this.doOcrAndSave(path + "body_75.jpg", path + "body_75.txt");
        this.doOcrAndSave(path + "body_150.jpg", path + "body_150.txt");
        this.doOcrAndSave(path + "footer.jpg", path + "footer.txt");
        this.doOcrAndSave(path + "footer_75.jpg", path + "footer_75.txt");
        this.doOcrAndSave(path + "footer_150.jpg", path + "footer_150.txt");
    }

    @Test
    public void testHOCROutput() throws IOException {
        InputStream file = this.getClass().getResourceAsStream("/data/generation/template1_generated0.pdf");
        PDDocument pdf = PDDocument.load(file);
        PDFRenderer renderer = new PDFRenderer(pdf);
        ImagePreprocessor preprocessor = new ImagePreprocessor(renderer.renderImageWithDPI(0, 300));

        BufferedImage inputImage = preprocessor.preprocess();
        String result = this.wrapper.initOcr(inputImage, true);
        File noocrFile = new File("..\\hocrStringOutput.txt");

        String lines[] = result.split("\\r?\\n");

        File outputFile = new File("..\\hocrOutput.xml");
        File outputFile2 = new File("..\\hocrOutput.jpg");
        try {
            Files.write(outputFile.toPath(), Arrays.asList(lines));
            ImageIO.write(inputImage, "jpg", outputFile2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void doOcrAndSave(String input, String output) throws Exception {
        String result = wrapper.initOcr(new File(input));
        String[] lines = result.split("\\r?\\n");
        File outputFile = new File(output);
        Files.write(outputFile.toPath(), Arrays.asList(lines));
    }

    private boolean assertOCR(String ocrResult, String stringToCheck) {
        boolean found = false;
        String result = ocrResult.replace("\n", "");
        for (String s : result.split(" ")) {
            if (this.compareStrings(s, stringToCheck) > 0.8) {
                found = true;
                break;

            }
        }
        return found;
    }

    private double compareStrings(String stringA, String stringB) {
        return StringUtils.getJaroWinklerDistance(stringA, stringB);
    }
}