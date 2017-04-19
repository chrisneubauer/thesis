package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.Position;
import de.cneubauer.domain.service.AccountingRecordExtractorService;
import de.cneubauer.domain.service.DataExtractorService;
import de.cneubauer.domain.service.InvoiceExtractorService;
import de.cneubauer.domain.service.validation.InvoiceValidator;
import de.cneubauer.domain.service.validation.PositionAccountValidator;
import de.cneubauer.gui.model.ExtractionModel;
import de.cneubauer.gui.model.ProcessResult;
import de.cneubauer.ocr.ImagePartitioner;
import de.cneubauer.ocr.ImagePreprocessor;
import de.cneubauer.ocr.hocr.HocrDocument;
import de.cneubauer.ocr.tesseract.TesseractWorkerStrategy;
import de.cneubauer.gui.model.DocumentCaseSet;
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
        DataExtractorService invoiceExtractor = new InvoiceExtractorService(doc, parts);
        DataExtractorService accountingRecordExtractor = new AccountingRecordExtractorService(doc, parts);
        Thread invoiceThread = new Thread(invoiceExtractor);
        Thread recordThread = new Thread(accountingRecordExtractor);

        boolean allFinished = false;
        boolean invoiceFinished = false;
        boolean recordFinished = false;
        Invoice i = null;
        List<Position> recordList = null;
        DocumentCaseSet caseSet;

        invoiceThread.start();
        recordThread.start();

        while (!allFinished) {
            if (!invoiceFinished && invoiceThread.getState() == Thread.State.TERMINATED) {
                i = invoiceExtractor.getThreadInvoice();
                invoiceFinished = true;
            }
            if (!recordFinished && recordThread.getState() == Thread.State.TERMINATED) {
                recordList = accountingRecordExtractor.getThreadRecord();
                recordFinished = true;
            }
            allFinished = invoiceFinished &&  recordFinished;
        }
        caseSet = invoiceExtractor.getCaseSet();
        if (caseSet == null) {
            caseSet = new DocumentCaseSet();
        }
        if (accountingRecordExtractor.getCaseSet() != null) {
            caseSet.setPositionCases(accountingRecordExtractor.getCaseSet().getPositionCases());
        }

        ExtractionModel m = new ExtractionModel();
        m.setInvoiceInformation(i);
        m.setRecords(recordList);
        m.setHocrDocument(doc);
        m.setCaseSet(caseSet);
        return m;
    }

    /**
     * Initiates the process of a processing task.
     * @return a list of ProcessResult that contain information of each invoice document
     * @throws Exception if any problem occurs
     */
    @Override
    protected List<ProcessResult> call() throws Exception {
        int counter = 0;
        Platform.runLater(() -> filesScanned.setText("0 / " + filesToScan.length));
        for (File f : filesToScan) {
                Platform.runLater(() -> currentFile.setText(f.getName()));
                ProcessResult r = new ProcessResult();
                r.setDocName(f.getName());
                r.setFile(f);
                if (f.getName().endsWith(".pdf") || f.getName().endsWith(".jpg") || f.getName().endsWith(".png")) {
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

                        HocrDocument hocrDocument = new HocrDocument(ocrParts[4]);

                        ScanStatus status = this.checkDocumentBeforeExtraction(hocrDocument);

                        if (status == ScanStatus.INVOICE) {
                            ExtractionModel m = this.extractInformation(hocrDocument, ocrParts);
                            r.setExtractionModel(m);
                            r.setProblem("");

                            if (this.resultValid(r)) {
                                r.setStatus(ScanStatus.OK);
                            } else {
                                r.setStatus(ScanStatus.ISSUE);
                                r.setProblem("Missing Information");
                            }
                        } else {
                            if (status == ScanStatus.PROFORMAINVOICE) {
                                r.setProblem("Proforma invoice detected");
                            } else if (status == ScanStatus.CREDITNOTE) {
                                r.setProblem("Credit note detected");
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        r.setProblem(ex.getMessage());
                        r.setStatus(ScanStatus.ERROR);
                    }
                }
                else {
                    r.setProblem("Unsupported filetype!");
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

    /**
     * Searches hocr document for "proforma" invoice or credit notes, they should not be processed (yet)
     * @param hocrDocument the document to be searched
     * @return OK if nothing found, PROFORMA if proforma invoice or CREDITNOTE if a credit note has been found
     */
    private ScanStatus checkDocumentBeforeExtraction(HocrDocument hocrDocument) {
        ScanStatus result = ScanStatus.INVOICE;
        if (hocrDocument.getPage(0).contains("proforma")) {
            result = ScanStatus.PROFORMAINVOICE;
        } else if (hocrDocument.getPage(0).contains("credit note") || hocrDocument.getPage(0).contains("gutschrift")) {
            result = ScanStatus.CREDITNOTE;
        }
        return result;
    }

    private String[] performOCR(BufferedImage[] imageParts, BufferedImage hocrImage) {
        // TODO: put stuff out of gui
        TesseractWorkerStrategy leftHeaderWorker = new TesseractWorkerStrategy(imageParts[0], false);
        Thread leftHeaderThread = new Thread(leftHeaderWorker);

        TesseractWorkerStrategy rightHeaderWorker = new TesseractWorkerStrategy(imageParts[1], false);
        Thread rightHeaderThread = new Thread(rightHeaderWorker);

        TesseractWorkerStrategy bodyWorker = new TesseractWorkerStrategy(imageParts[2], false);
        Thread bodyThread = new Thread(bodyWorker);

        TesseractWorkerStrategy footerWorker = new TesseractWorkerStrategy(imageParts[3], false);
        Thread footerThread = new Thread(footerWorker);

        TesseractWorkerStrategy hocrWorker = new TesseractWorkerStrategy(hocrImage, true);
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
        PositionAccountValidator accountingValidator = new PositionAccountValidator(r.getExtractionModel().getRecords());
        boolean accountingRecordsValid = accountingValidator.isValid();
        return invoiceValid && accountingRecordsValid;
    }

    public List<ProcessResult> getResult() {
        return processResults;
    }
}
