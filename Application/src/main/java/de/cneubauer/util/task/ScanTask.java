package de.cneubauer.util.task;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.Record;
import de.cneubauer.domain.service.DataExtractorService;
import de.cneubauer.domain.service.validation.AccountingRecordValidator;
import de.cneubauer.domain.service.validation.InvoiceValidator;
import de.cneubauer.gui.model.ExtractionModel;
import de.cneubauer.gui.model.ProcessResult;
import de.cneubauer.ocr.ImagePartitioner;
import de.cneubauer.ocr.ImagePreprocessor;
import de.cneubauer.ocr.hocr.HocrDocument;
import de.cneubauer.ocr.tesseract.TesseractWorker;
import de.cneubauer.util.DocumentCaseSet;
import de.cneubauer.util.enumeration.ScanStatus;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 29.11.2016.
 * ScanTask that executes OCR and updates the UI
 */
public class ScanTask extends Task {
    private File[] filesToScan;
    private List<ProcessResult> processResults;
    private double percentage;
    private double current = 0;
    private Label currentFile;
    private Label filesScanned;
    private Label status;
    private ProgressBar progressBar;

    public ScanTask(File[] filesToScan, Label currentFileName, Label amountOfFilesScanned, ProgressBar progress, Label statusLabel) {
        this.filesToScan = filesToScan;
        this.processResults = new ArrayList<>(filesToScan.length);
        this.percentage = (double) (100 / filesToScan.length) / 100;
        this.currentFile = currentFileName;
        this.filesScanned = amountOfFilesScanned;
        this.status = statusLabel;
        this.progressBar = progress;
    }

    private ExtractionModel extractInformation(HocrDocument doc, String[] parts) {
        DataExtractorService copy = new DataExtractorService(doc, parts);
        DataExtractorService copy2 = new DataExtractorService(doc, parts);
        copy.extractInvoice = true;
        copy2.extractInvoice = false;
        Thread invoiceThread = new Thread(copy);
        Thread recordThread = new Thread(copy2);

        boolean allFinished = false;
        boolean invoiceFinished = false;
        boolean recordFinished = false;
        Invoice i = null;
        List<Record> recordList = null;
        DocumentCaseSet caseSet;

        invoiceThread.start();
        recordThread.start();

        while (!allFinished) {
            if (!invoiceFinished && invoiceThread.getState() == Thread.State.TERMINATED) {
                i = copy.getThreadInvoice();
                invoiceFinished = true;
            }
            if (!recordFinished && recordThread.getState() == Thread.State.TERMINATED) {
                recordList = copy2.getThreadRecord();
                recordFinished = true;
            }
            allFinished = invoiceFinished &&  recordFinished;
        }
        caseSet = copy.getCaseSet();

        ExtractionModel m = new ExtractionModel();
        m.setInvoiceInformation(i);
        m.setRecords(recordList);
        m.setHocrDocument(doc);
        m.setCaseSet(caseSet);
        return m;
    }

    @Override
    @Deprecated
    protected List<ProcessResult> call() throws Exception {
        int counter = 0;
        Platform.runLater(() -> filesScanned.setText("0 / " + filesToScan.length));
        for (File f : filesToScan) {
            Platform.runLater(() -> currentFile.setText(f.getName()));
            ProcessResult r = new ProcessResult();
            r.setDocName(f.getName());
            r.setFile(f);
            try {
                Logger.getLogger(this.getClass()).log(Level.INFO, "reading file on path: " + f.getPath());
                ImagePreprocessor preprocessor = new ImagePreprocessor(f.getPath());
                BufferedImage preprocessedImage = preprocessor.preprocess();

                Logger.getLogger(this.getClass()).log(Level.INFO, "initiating ocr threads...");
                Platform.runLater(() -> this.status.setText("Scanning header, body and footer..."));

                // TODO: remove partitioning
                ImagePartitioner partitioner = new ImagePartitioner(preprocessedImage);
                BufferedImage[] imageParts = partitioner.process();
                String[] ocrParts = this.performOCR(imageParts, preprocessedImage);

                Platform.runLater(() -> status.setText("Extracting information..."));

                // TODO: parallelize account records and invoice information
                HocrDocument hocrDocument = new HocrDocument(ocrParts[4]);

                ExtractionModel m = this.extractInformation(hocrDocument, ocrParts);
                r.setExtractionModel(m);
                r.setProblem("");

                if (this.resultValid(r)) {
                    r.setStatus(ScanStatus.OK);
                } else {
                    r.setStatus(ScanStatus.ISSUE);
                    r.setProblem("Missing Information");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                r.setProblem(ex.getMessage());
                r.setStatus(ScanStatus.ERROR);
            }
            current += percentage;
            processResults.add(counter, r);
            counter++;
            int finalCounter = counter;
            Platform.runLater(() -> filesScanned.setText(String.valueOf(finalCounter) + " / " + filesToScan.length));
            Logger.getLogger(this.getClass()).log(Level.INFO, "new progress: " + current * 100 + "%.");
            Platform.runLater(() -> progressBar.setProgress(current));
        }
        return processResults;
    }

    private String[] performOCR(BufferedImage[] imageParts, BufferedImage hocrImage) {
        // TODO: put stuff out of gui
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
                if (rightHeaderFinished) {
                    String statusText = this.status.getText();
                    Platform.runLater(() -> status.setText(statusText.replace("header, ", "")));
                }
            }
            if (!rightHeaderFinished && rightHeaderThread.getState()== Thread.State.TERMINATED) {
                ocrParts[1] = rightHeaderWorker.getResultIfFinished();
                rightHeaderFinished = true;
                if (leftHeaderFinished) {
                    String statusText = this.status.getText();
                    Platform.runLater(() -> status.setText(statusText.replace("header, ", "")));
                }
            }
            if (!bodyFinished && bodyThread.getState() == Thread.State.TERMINATED) {
                ocrParts[2] = bodyWorker.getResultIfFinished();
                bodyFinished = true;
                String statusText = this.status.getText();
                Platform.runLater(() -> status.setText(statusText.replace("body ", "")));
            }
            if (!footerFinished && footerThread.getState() == Thread.State.TERMINATED) {
                ocrParts[3] = footerWorker.getResultIfFinished();
                footerFinished = true;
                String statusText = this.status.getText();
                Platform.runLater(() -> status.setText(statusText.replace("and footer", "")));
            }
            allFinished = leftHeaderFinished && rightHeaderFinished && bodyFinished && footerFinished && hocrFinished;
        }
        return ocrParts;
    }

    private boolean resultValid(ProcessResult r) {
        InvoiceValidator invoiceValidator = new InvoiceValidator(r.getExtractionModel().getInvoiceInformation());
        boolean invoiceValid = invoiceValidator.isValid();
        AccountingRecordValidator accountingValidator = new AccountingRecordValidator(r.getExtractionModel().getRecords());
        boolean accountingRecordsValid = accountingValidator.isValid();
        return invoiceValid && accountingRecordsValid;
    }

    public List<ProcessResult> getResult() {
        return processResults;
    }
}
