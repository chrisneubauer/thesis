package de.cneubauer.util;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.service.DataExtractorService;
import de.cneubauer.ocr.ImagePartitioner;
import de.cneubauer.ocr.ImagePreprocessor;
import de.cneubauer.ocr.hocr.HocrDocument;
import de.cneubauer.ocr.tesseract.TesseractWorker;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.*;

/**
 * Created by Christoph on 08.03.2017.
 * Used for executing with big data
 */
public class BigDataTest {
    String resultPath = "D:\\Christoph\\Documents\\1_Studium\\1.2_Master\\MA\\Thesis\\Application\\src\\test\\resources\\data\\results.txt";
    File resultFile;

    @Before
    public void setUp() {
        resultFile = new File(resultPath);
    }

    @Test
    public void execute() throws IOException {
        String outputPath = "D:\\Christoph\\Documents\\1_Studium\\1.2_Master\\MA\\Thesis\\Application\\src\\test\\resources\\data\\output\\";
        FileWriter fw = new FileWriter(resultFile, true);

        //int[] number = new int[] {0, 8 , 18, 40 , 55 , 79, 85 , 95, 96, 97};
        //int[] number = new int[] {0, 8, 14, 16, 21, 32, 53, 57, 71, 84};
        int[] number = new int[] {7, 12, 26, 31, 33, 41, 77, 80, 81, 89};

        for (int k = 0; k < 10; k++) {
        //for (int k = 3; k < 4; k++) {
            //for (int j = 75; j < 100; j++) {
            //for (int j = 0; j < 10; j++) {
                File outFile = new File(outputPath + "template3_generated" + number[k] + ".pdf");
                System.out.println("Scanning file: " + outFile.getName());

                ImagePreprocessor preprocessor = new ImagePreprocessor(outFile.getPath());
                BufferedImage preprocessedImage = preprocessor.preprocess();

                ImagePartitioner partitioner = new ImagePartitioner(preprocessedImage);
                BufferedImage[] imageParts = partitioner.process();

                String[] ocrParts = this.performOCR(imageParts, preprocessedImage);
                HocrDocument hocrDocument = new HocrDocument(ocrParts[4]);

                DataExtractorService extractorService = new DataExtractorService(hocrDocument, ocrParts);
                extractorService.extractInvoice = true;
                Thread invoiceThread = new Thread(extractorService);

                boolean invoiceFinished = false;
                Invoice i = null;

                invoiceThread.start();

                while (!invoiceFinished) {
                    if (invoiceThread.getState() == Thread.State.TERMINATED) {
                        i = extractorService.getThreadInvoice();
                        invoiceFinished = true;
                    }
                }

                try {
                    StringBuilder result = new StringBuilder();
                    if (i.getCreditor() != null) {
                        result.append(i.getCreditor().getName()).append(";");
                    } else {
                        result.append(";");
                    }

                    if (i.getDebitor() != null) {
                        result.append(i.getDebitor().getName()).append(";");
                    } else {
                        result.append(";");
                    }
                    result.append(i.getInvoiceNumber()).append(";");
                    result.append(i.getIssueDate().toString()).append(";");
                    result.append(i.getLineTotal()).append(";");
                    result.append(i.getChargeTotal()).append(";");
                    result.append(i.getAllowanceTotal()).append(";");
                    result.append(i.getTaxBasisTotal()).append(";");
                    result.append(i.getTaxTotal()).append(";");
                    result.append(i.getGrandTotal()).append(";");
                    result.append(i.getDeliveryDate().toString()).append(";");
                    fw.append(result.toString());
                    fw.write(System.lineSeparator());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            //}
        }
        fw.close();
    }

    @Test
    public void compare() throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(resultFile)));
        String line = reader.readLine();
        String[] comparisonValues;


        int[] number1 = new int[] {0, 8 , 18, 40 , 55 , 79, 85 , 95, 96, 97};
        int[] number2 = new int[] {0, 8, 14, 16, 21, 32, 53, 57, 71, 84};
        int[] number3 = new int[] {7, 12, 26, 31, 33, 41, 77, 80, 81, 89};
        int[] number = number1;
        for (int i = 1; i < 4; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 2) {
                    number = number2;
                } else if (i == 3) {
                    number = number3;
                }
                String file = "D:\\Christoph\\Documents\\1_Studium\\1.2_Master\\MA\\Thesis\\Application\\src\\test\\resources\\data\\output\\template" + i + "_generated" + number[j] + ".xml";
                File temp1File = new File(file);
                String text = FileUtils.readFileToString(temp1File, "UTF-8");
                String debitor = this.getKeyword(text, "debitor");
                String creditor = this.getKeyword(text, "creditor");
                String issueDate = this.getKeyword(text, "issueDate");
                String invoiceNo = this.getKeyword(text, "invoiceNo");
                String taxBasisTotal = this.getKeyword(text, "totalNetto").replace(",", ".");
                String taxTotal = this.getKeyword(text, "taxTotal").replace(",", ".");
                String grandTotal = this.getKeyword(text, "totalValue").replace(",", ".");
                String deliveryDate = null;
                if (i == 3) {
                    deliveryDate = this.getKeyword(text, "deliveryDate");
                } else {
                    deliveryDate = issueDate;
                }

                comparisonValues = new String[]{creditor, debitor, invoiceNo, issueDate, taxBasisTotal, "0", "0", taxBasisTotal, taxTotal, grandTotal, deliveryDate};

                String[] parts = line.split(";");
                int found = this.checkValues(comparisonValues, parts, i);
                double resultingValue = (double) found / (double) parts.length;
                resultingValue = resultingValue * 100;
                //resultingValue = Math.round(resultingValue * 100);

                String value = String.valueOf(resultingValue);
                value = value.replace(".", ",");
                int end = value.indexOf(",") + 3;
                if (value.indexOf(",") == 3) {
                    value = "100";
                } else {
                    value = value.substring(0, end);
                }
                System.out.println(value + " % accuracy for file template" + i + "_generated" + number[j]);
                line = reader.readLine();
            }
        }
    }

    private int checkValues(String[] comparisonValues, String[] parts, int i) throws ParseException {
        int found = 0;
        if (parts[0].length() > 0 && comparisonValues[0].contains(parts[0])) {
            found++;
        }
        if (parts[1].length() > 0 && comparisonValues[1].contains(parts[1])) {
            found++;
        }
        if (parts[2].length() > 0 && comparisonValues[2].contains(parts[2])) {
            found++;
        }
        Date correctDate = new SimpleDateFormat("DD.MM.YYYY").parse(comparisonValues[3]);
        Date checkDate = new SimpleDateFormat("YYYY-MM-DD").parse(parts[3]);
        if (correctDate.compareTo(checkDate) == 0) {
            found++;
        }
        double taxBasisTotal1 = 0;
        if (comparisonValues[4].length() > 0) {
            taxBasisTotal1 = Double.valueOf(comparisonValues[4]);
        }
        double taxBasisTotal2 = Double.valueOf(parts[4].replace(",","."));
        if (taxBasisTotal1 - taxBasisTotal2 < 1) {
            found++;
        }
        double allowance1 = 0;
        if (comparisonValues[5].length() > 0) {
            allowance1 = Double.valueOf(comparisonValues[5]);
        }
        double allowance2 = Double.valueOf(parts[5].replace(",","."));
        if (allowance1 - allowance2 < 1) {
            found++;
        }
        double charge1 = 0;
        if (comparisonValues[6].length() > 0) {
            charge1 = Double.valueOf(comparisonValues[6]);
        }
        double charge2 = Double.valueOf(parts[6].replace(",","."));
        if (charge1 - charge2 < 1) {
            found++;
        }
        double line1 = 0;
        if (comparisonValues[7].length() > 0) {
            line1 = Double.valueOf(comparisonValues[7]);
        }
        double line2 = Double.valueOf(parts[7].replace(",","."));
        if (line1 - line2 < 1) {
            found++;
        }

        double taxTotal1 = 0;
        if (comparisonValues[8].length() > 0) {
            taxTotal1 = Double.valueOf(comparisonValues[8]);
        }
        double taxTotal2 = Double.valueOf(parts[8].replace(",","."));
        if (taxTotal1 - taxTotal2 < 1) {
            found++;
        }
        double grandTotal1 = 0;
        if (comparisonValues[9].length() > 0) {
            grandTotal1 = Double.valueOf(comparisonValues[9]);
        }
        double grandTotal2 = Double.valueOf(parts[9].replace(",","."));
        if (grandTotal1 - grandTotal2 < 1) {
            found++;
        }
        Date delDate = new SimpleDateFormat("DD.MM.YYYY").parse(comparisonValues[10]);
        Date checkDelDate = new SimpleDateFormat("YYYY-MM-DD").parse(parts[10]);

        if (delDate.compareTo(checkDelDate) == 0) {
            found++;
        }
        return found;
    }

    private String getKeyword(String completeText, String keyword) {
        try {
            int idx = completeText.indexOf("<w:alias w:val=\"" + keyword + "\"/>");
            completeText = completeText.substring(idx);
            int idx2 = completeText.indexOf("<w:t>");
            completeText = completeText.substring(idx2 + 5);
            String result = completeText.split("<")[0];
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    private String[] performOCR(BufferedImage[] imageParts, BufferedImage hocrImage) {
        TesseractWorker leftHeaderWorker = new TesseractWorker(imageParts[0], false);
        Thread leftHeaderThread = new Thread(leftHeaderWorker);

        TesseractWorker rightHeaderWorker = new TesseractWorker(imageParts[1], false);
        Thread rightHeaderThread = new Thread(rightHeaderWorker);

        TesseractWorker bodyWorker = new TesseractWorker(imageParts[2], false);
        Thread bodyThread = new Thread(bodyWorker);

        TesseractWorker footerWorker = new TesseractWorker(imageParts[3], false);
        Thread footerThread = new Thread(footerWorker);

        TesseractWorker hocrWorker = new TesseractWorker(hocrImage, true);
        Thread hocrThread = new Thread(hocrWorker);

        boolean leftHeaderFinished = false;
        boolean rightHeaderFinished = false;
        boolean bodyFinished = false;
        boolean footerFinished = false;
        boolean hocrFinished = false;
        boolean allFinished = false;

        String[] ocrParts = new String[5];

        hocrThread.start();
        leftHeaderThread.start();
        rightHeaderThread.start();
        bodyThread.start();
        footerThread.start();

        while (!allFinished) {
            if (!hocrFinished && hocrThread.getState() == Thread.State.TERMINATED) {
                ocrParts[4] = hocrWorker.getResultIfFinished();
                hocrFinished = true;
            }
            if (!leftHeaderFinished && leftHeaderThread.getState() == Thread.State.TERMINATED) {
                ocrParts[0] = leftHeaderWorker.getResultIfFinished();
                leftHeaderFinished = true;
            }
            if (!rightHeaderFinished && rightHeaderThread.getState()== Thread.State.TERMINATED) {
                ocrParts[1] = rightHeaderWorker.getResultIfFinished();
                rightHeaderFinished = true;
            }
            if (!bodyFinished && bodyThread.getState() == Thread.State.TERMINATED) {
                ocrParts[2] = bodyWorker.getResultIfFinished();
                bodyFinished = true;
            }
            if (!footerFinished && footerThread.getState() == Thread.State.TERMINATED) {
                ocrParts[3] = footerWorker.getResultIfFinished();
                footerFinished = true;
            }
            allFinished = leftHeaderFinished && rightHeaderFinished && bodyFinished && footerFinished && hocrFinished;
        }
        return ocrParts;
    }

}
