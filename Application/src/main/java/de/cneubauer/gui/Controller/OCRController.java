package de.cneubauer.gui.controller;

import com.google.common.io.Files;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.service.OCRDataExtractorService;
import de.cneubauer.ocr.tesseract.TesseractWrapper;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Christoph Neubauer on 04.10.2016.
 * Provides controls for performing OCR in the UI
 */
public class OCRController extends SplitPaneController {
    @FXML private TextField fileInput;

    @FXML
    protected void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            this.fileInput.setText(file.getPath());
        }
    }

    @FXML
    protected void scanFile(ActionEvent e) {
        boolean valid = this.validateFileInput();
        if (valid) {
            File fileToScan = new File(this.fileInput.getText());
            TesseractWrapper wrapper = new TesseractWrapper();
            String result = wrapper.initOcr(fileToScan.getPath());
            System.out.println(result);
            OCRDataExtractorService service = new OCRDataExtractorService(result);
            Scan scan = new Scan();
            Invoice extractedInformation = service.extractInformation();
            scan.setInvoiceInformation(extractedInformation);
            try {
                scan.setFile(Files.toByteArray(fileToScan));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            this.openExtractionInformationMenu(e, scan);
        }
    }

    //this method opens invoice information after ocr processing using ResultsController
    @FXML
    private void openExtractionInformationMenu(Event e, Scan extractedInformation) {
        try {
            Node n = (Node) e.getSource();
            Node parent = n.getParent().getParent().getParent();
            AnchorPane leftPane = (AnchorPane) parent.getScene().lookup("#leftPane");

            Locale locale = super.getCurrentLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/Application", locale);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../FXML/tab.fxml"), bundle);

            Parent root = loader.load();
            leftPane.getChildren().clear();
            leftPane.getChildren().add(root);

            root.getScene().getStylesheets().add(String.valueOf(getClass().getResource("../../../../css/validationError.css")));

            TabController ctrl = loader.getController();
            ctrl.initResults(extractedInformation, this.fileInput.getText());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean validateFileInput() {
        return this.fileInput.getText() != null;
    }

}
