package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.gui.model.ExtractionModel;
import de.cneubauer.util.enumeration.ValidationStatus;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Christoph Neubauer on 22.11.2016.
 * Controller for the Splitpane
 */
public class SplitPaneController extends GUIController {
    @FXML public SplitPane splitPaneInclude;
    @FXML public AnchorPane rightPane;
    @FXML public AnchorPane leftPane;
    @FXML public ImageView pdfImage;

    @FXML public VBox invoiceTab;
    @FXML public VBox accountingRecordsTab;

    // values for image scaling
    private final DoubleProperty zoomProperty = new SimpleDoubleProperty(300);
    private double oldX;
    private double oldY;

    @FXML private ResultsController invoiceTabController;
    @FXML private AccountingRecordsController accountingRecordsTabController;
    @FXML public TabPane tabPane;

    private int index;
    private ExtractionModel model;
    private ProcessedListController caller;

    void initResults(int index, ExtractionModel extractedInformation, File fileToScan, ProcessedListController caller) {
        this.caller = caller;
        this.index = index;
        invoiceTabController.initData(extractedInformation.getInvoiceInformation(), this);
        accountingRecordsTabController.initData(extractedInformation.getRecords(), this);
        this.initImage(fileToScan);
        model = extractedInformation;
    }

    // this method opens the page where the user can import files
    @FXML
    protected void openScanFormMenu(Event e) {
        try {
            this.leftPane.getChildren().clear();
            Locale locale = super.getCurrentLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/Application", locale);
            this.leftPane.getChildren().add(FXMLLoader.load(getClass().getResource("../../../../FXML/scanForm.fxml"), bundle));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // this method opens the page where the user can search in the database
    @FXML
    protected void openDatabaseMenu(Event e) {
        try {
            leftPane.getChildren().clear();
            Locale locale = super.getCurrentLocale();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/Application", locale);
            leftPane.getChildren().add(FXMLLoader.load(getClass().getResource("../../../../FXML/searchDatabase.fxml"), bundle));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void reviseAll() {
        Logger.getLogger(this.getClass()).log(Level.INFO, "Reviewed Button clicked! Validating the fields");
        List<ValidationStatus> accountingErrors = accountingRecordsTabController.validateFieldsBeforeSave();
        List<ValidationStatus> invoiceErrors = invoiceTabController.validateFieldsBeforeSave();
        if (accountingErrors.size() == 0 && invoiceErrors.size() == 0) {
            this.updateAndReturn();
        } else {
            Alert info = new Alert(Alert.AlertType.WARNING);
            String content = "Could not update the document! \n Please review the following errors: \n";
            StringBuilder sb = new StringBuilder();
            sb.append(content);
            for (ValidationStatus error : invoiceErrors) {
                sb.append(this.getValidationError(error)).append("\n");
            }
            for (ValidationStatus error : accountingErrors) {
                sb.append(this.getValidationError(error)).append("\n");
            }
            info.setContentText(sb.toString());
            info.setHeaderText("Review Issue");
            info.show();
        }
    }

    private String getValidationError(ValidationStatus error) {
        Locale locale = super.getCurrentLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("bundles/ValidationErrors", locale);
        return bundle.getString(error.name());
    }

    // updates all reviewed information and return to list view
    private void updateAndReturn() {
        Invoice oldInfo = model.getInvoiceInformation();
        model.setUpdatedRecords(accountingRecordsTabController.updateInformation());
        model.setUpdatedInvoiceInformation(invoiceTabController.updateInformation());

        //accountingRecordsTabController.addRevisedToFile();
        invoiceTabController.addRevisedToFile();
        caller.updateSelected(index, model);
        Stage popup = (Stage) this.pdfImage.getScene().getWindow();
        popup.close();
    }

    private void initImage(File image) {
        try {
            Image i;
            if (image.getName().endsWith("pdf")) {
                PDDocument pdf = PDDocument.load(image);
                PDFRenderer renderer = new PDFRenderer(pdf);
                BufferedImage img = renderer.renderImageWithDPI(0, 300);
                i = SwingFXUtils.toFXImage(img, null);
                pdf.close();
            } else {
                InputStream in = new FileInputStream(image);
                i = new Image(in);
            }
            pdfImage.setImage(i);
            pdfImage.setFitWidth(zoomProperty.get() * 3);
            pdfImage.setFitHeight(zoomProperty.get() * 4);
            pdfImage.setPreserveRatio(true);
            pdfImage.setSmooth(true);
            pdfImage.setCache(true);

            pdfImage.setOnDragDetected(event -> {
                oldX = event.getX();
                oldY = event.getY();
            });

            pdfImage.setOnMouseReleased(event -> {
                pdfImage.setTranslateX(event.getX() - oldX);
                pdfImage.setTranslateY(event.getY() - oldY);
            });

            pdfImage.addEventFilter(ScrollEvent.ANY, event -> {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            });

            zoomProperty.addListener(arg0 -> {
                pdfImage.setFitWidth(zoomProperty.get() * 3);
                pdfImage.setFitHeight(zoomProperty.get() * 4);
            });
        } catch (Exception e) {
            Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to parse image!");
        }
    }
}
