package de.cneubauer.util.task;

import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.service.OCRDataExtractorService;
import de.cneubauer.gui.model.ExtractionModel;
import de.cneubauer.gui.model.ProcessResult;
import de.cneubauer.ocr.tesseract.TesseractWrapper;
import de.cneubauer.util.enumeration.ScanStatus;
import javafx.concurrent.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 29.11.2016.
 */
public class ScanTask extends Task {
    File[] filesToScan;
    List<ProcessResult> processResults;
    double percentage;
    double current = 0;

    public ScanTask(File[] filesToScan) {
        this.filesToScan = filesToScan;
        this.processResults = new ArrayList<>(filesToScan.length);
        this.percentage = 100 / filesToScan.length;
    }

    @Override
    protected List<ProcessResult> call() throws Exception {
        int counter = 0;
        for (File f : filesToScan) {
            ProcessResult r = new ProcessResult();
            r.setDocName(f.getName());
            r.setFile(f);
            try {
                //setFileName(f.getName());
                TesseractWrapper wrapper = new TesseractWrapper();
                String result = wrapper.initOcr(f);

                OCRDataExtractorService service = new OCRDataExtractorService(result);
                Invoice extractedInformation = service.extractInvoiceInformation();
                List<AccountingRecord> recordList = service.extractAccountingRecordInformation();

                ExtractionModel m = new ExtractionModel();
                m.setInvoiceInformation(extractedInformation);
                m.setAccountingRecords(recordList);

                r.setExtractionModel(m);

                //this.progressBar.setProgress(current + percentage);
                current += percentage;
                r.setProblem("");
                r.setStatus(ScanStatus.OK);
            } catch (Exception ex) {
                ex.printStackTrace();
                r.setProblem(ex.getMessage());
                r.setStatus(ScanStatus.ERROR);
            }
            processResults.add(counter, r);
            counter++;
            //setFilesScanned(counter);
            updateProgress(current, filesToScan.length);
        }
        return processResults;
    }
}
