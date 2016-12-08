package de.cneubauer.gui.controller;

import de.cneubauer.gui.model.ExtractionModel;
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
        accountingRecordsTabController.initData(extractedInformation.getAccountingRecords(), this);
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
        boolean accountingCorrect = accountingRecordsTabController.validateFieldsBeforeSave();
        boolean invoiceCorrect = invoiceTabController.validateFieldsBeforeSave();
        if (accountingCorrect && invoiceCorrect) {
            this.updateAndReturn();
        } else {
            Alert info = new Alert(Alert.AlertType.WARNING);
            info.setContentText("Could not update the document! \n Please review all fields again and make sure that there are no more errors.");
            info.setHeaderText("Review Issue");
            info.show();
        }
    }

    // updates all reviewed information and return to list view
    private void updateAndReturn() {
        model.setAccountingRecords(accountingRecordsTabController.updateInformation());
        model.setInvoiceInformation(invoiceTabController.updateInformation());
        accountingRecordsTabController.addRevisedToFile();
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
