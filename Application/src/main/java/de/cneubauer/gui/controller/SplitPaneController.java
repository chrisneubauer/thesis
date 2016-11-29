package de.cneubauer.gui.controller;

import com.google.common.io.Files;
import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.gui.model.ExtractionModel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Christoph Neubauer on 22.11.2016.
 * Controller for the Splitpane
 */
public class SplitPaneController extends GUIController {
    @FXML public AnchorPane rightPane;
    @FXML public AnchorPane leftPane;
    @FXML public ImageView pdfImage;

    @FXML public VBox invoiceTab;
    @FXML public VBox accountingRecordsTab;
    @FXML private ResultsController invoiceTabController;
    @FXML private AccountingRecordsController accountingRecordsTabController;
    @FXML public TabPane tabPane;

    void initResults(ExtractionModel extractedInformation, File fileToScan) {
        invoiceTabController.initData(extractedInformation.getInvoiceInformation());
        this.initAccountingRecordResults(extractedInformation.getAccountingRecords());
        try {
            byte[] img = Files.toByteArray(fileToScan);
            this.initImage(img);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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

    private void initAccountingRecordResults(List<AccountingRecord> data) {
        accountingRecordsTabController.initData(data);
    }

    private void initImage(byte[] image) {
        BufferedImage img = null;
        try {
            PDDocument pdf = PDDocument.load(image);
            PDFRenderer renderer = new PDFRenderer(pdf);
            img = renderer.renderImageWithDPI(0, 1200);
        } catch (Exception ex) {
            // no pdf, try again as image
            try {
                InputStream in = new ByteArrayInputStream(image);
                img = ImageIO.read(in);
            } catch (Exception ex2) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to parse image!");
            }
        }

        if (img != null) {
            Image imageToView = SwingFXUtils.toFXImage(img, null);
            pdfImage.setImage(imageToView);
            pdfImage.setPreserveRatio(true);
            pdfImage.setSmooth(true);
            pdfImage.setCache(true);
        }
    }
}
