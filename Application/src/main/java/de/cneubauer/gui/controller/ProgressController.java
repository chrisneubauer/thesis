package de.cneubauer.gui.controller;

import com.sun.javafx.collections.ObservableListWrapper;
import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.service.OCRDataExtractorService;
import de.cneubauer.gui.model.ExtractionModel;
import de.cneubauer.gui.model.ProcessResult;
import de.cneubauer.ocr.tesseract.TesseractWrapper;
import de.cneubauer.util.enumeration.ScanStatus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Christoph Neubauer on 25.11.2016.
 * ProgressController shows the progress of scanned files
 */
public class ProgressController extends GUIController {
    @FXML public ProgressBar progressBar;
    private File[] files;

    // the status text what is currently be done
    @FXML public Label status;

    // the current file name that is scanned
    @FXML public Label fileName;

    // files scanned / all files
    @FXML public Label filesScanned;

    void initData(File[] files) {
        this.files = files;
        this.progressBar.setProgress(0);
        this.setFilesScanned(0);
        // TODO: use task to update progress bar: https://gist.github.com/jewelsea/2774481
    }

    private void progressFiles() {
        // TODO: results should be extracted and delivered with the processResult
        String[] results = new String[files.length];
        this.setFileName("");
        this.progressBar.setProgress(0);
        int counter = 0;
        double percentage = 100 / files.length;
        double current = 0;
        List<ProcessResult> list = new ArrayList<>(files.length);

        for (File f : files) {
            ProcessResult r = new ProcessResult();
            r.setDocName(f.getName());
            r.setFile(f);

            try {
                this.setFileName(f.getName());
                TesseractWrapper wrapper = new TesseractWrapper();
                String result = wrapper.initOcr(f);
                results[counter] = result;

                OCRDataExtractorService service = new OCRDataExtractorService(result);
                Invoice extractedInformation = service.extractInvoiceInformation();
                List<AccountingRecord> recordList = service.extractAccountingRecordInformation();

                ExtractionModel m = new ExtractionModel();
                m.setInvoiceInformation(extractedInformation);
                m.setAccountingRecords(recordList);

                r.setExtractionModel(m);

                this.progressBar.setProgress(current + percentage);
                current += percentage;
                r.setProblem("");
                r.setStatus(ScanStatus.OK);
            } catch (Exception ex) {
                ex.printStackTrace();
                r.setProblem(ex.getMessage());
                r.setStatus(ScanStatus.ERROR);
            }
            list.add(counter, r);
            counter++;
            this.setFilesScanned(counter);
        }

        ObservableList<ProcessResult> processResults = new ObservableListWrapper<>(list);
        this.openProgressedList(processResults);
    }

    private void openProgressedList(ObservableList<ProcessResult> processResults) {
        try {
            Stage stage = (Stage) this.progressBar.getScene().getWindow();

            Locale locale = this.getCurrentLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/Application", locale);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../FXML/processedList.fxml"), bundle);

            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);

            ProcessedListController ctrl = loader.getController();
            ctrl.initData(processResults);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setFilesScanned(int current) {
        this.filesScanned.setText(current + " / " + files.length);
    }

    private void setFileName(String fileName) {
        this.fileName.setText(fileName);
    }

    public void startProgress() {
        this.progressFiles();
    }
}
