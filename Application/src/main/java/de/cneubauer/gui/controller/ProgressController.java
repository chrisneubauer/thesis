package de.cneubauer.gui.controller;

import com.sun.javafx.collections.ObservableListWrapper;
import de.cneubauer.gui.model.ProcessResult;
import de.cneubauer.domain.service.ScanTask;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

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
        this.progressFiles();
    }

    private void progressFiles() {
        List<ProcessResult> list = new ArrayList<>(files.length);

        ScanTask scanTask = new ScanTask(files, fileName, filesScanned, progressBar, status);

        scanTask.setOnSucceeded(t -> {
            list.addAll(scanTask.getResult());
            Platform.runLater(() -> status.setText("Extracting information..."));
            ObservableList<ProcessResult> processResults = new ObservableListWrapper<>(list);
            this.openProgressedList(processResults);
        });

        Thread t = new Thread(scanTask);
        t.start();
    }

    private void openProgressedList(ObservableList<ProcessResult> processResults) {
        try {
            Stage stage = (Stage) this.progressBar.getScene().getWindow();

            Locale locale = this.getCurrentLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/Application", locale);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../FXML/processedList.fxml"), bundle);

            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setMaximized(true);

            ProcessedListController ctrl = loader.getController();
            ctrl.initData(processResults);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
