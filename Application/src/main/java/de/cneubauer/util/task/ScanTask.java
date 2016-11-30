package de.cneubauer.util.task;

import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.service.OCRDataExtractorService;
import de.cneubauer.gui.model.ExtractionModel;
import de.cneubauer.gui.model.ProcessResult;
import de.cneubauer.ocr.tesseract.TesseractWrapper;
import de.cneubauer.util.enumeration.ScanStatus;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
    private ProgressBar progressBar;

    public ScanTask(File[] filesToScan, Label currentFileName, Label amountOfFilesScanned, ProgressBar progress) {
        this.filesToScan = filesToScan;
        this.processResults = new ArrayList<>(filesToScan.length);
        this.percentage = (double) (100 / filesToScan.length) / 100;
        this.currentFile = currentFileName;
        this.filesScanned = amountOfFilesScanned;
        this.progressBar = progress;
    }

    @Override
    protected List<ProcessResult> call() throws Exception {
        int counter = 0;
        Platform.runLater(() -> filesScanned.setText("0 / " + filesToScan.length));
        for (File f : filesToScan) {
            Platform.runLater(() -> currentFile.setText(f.getName()));
            ProcessResult r = new ProcessResult();
            r.setDocName(f.getName());
            r.setFile(f);
            try {
                TesseractWrapper wrapper = new TesseractWrapper();
                String result = wrapper.initOcr(f);

                OCRDataExtractorService service = new OCRDataExtractorService(result);
                Invoice extractedInformation = service.extractInvoiceInformation();
                List<AccountingRecord> recordList = service.extractAccountingRecordInformation();

                ExtractionModel m = new ExtractionModel();
                m.setInvoiceInformation(extractedInformation);
                m.setAccountingRecords(recordList);

                r.setExtractionModel(m);

                r.setProblem("");
                r.setStatus(ScanStatus.OK);
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

    public List<ProcessResult> getResult() {
        return processResults;
    }
}
