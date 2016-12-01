package de.cneubauer.gui.controller;

import com.google.common.io.Files;
import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.bo.Scan;
import de.cneubauer.gui.model.ExtractionModel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
 * Controller for Tab view
 */
@Deprecated
public class TabController extends SplitPaneController {

    @FXML public VBox invoiceTab;
    @FXML public VBox accountingRecordsTab;
    @FXML private ResultsController invoiceTabController;
    @FXML private AccountingRecordsController accountingRecordsTabController;
    @FXML public TabPane tabPane;

    void initResults(Scan extractedInformation, String text, List<AccountingRecord> recordList, File fileToScan) {
       // invoiceTabController.initData(extractedInformation.getInvoiceInformation()); // contained String text
        this.initAccountingRecordResults(recordList);
        this.initImage(extractedInformation.getFile());
        //this.initImage(fileToScan);
    }

    void initResults(ExtractionModel extractedInformation, File fileToScan) {
        //invoiceTabController.initData(extractedInformation.getInvoiceInformation());
        try {
            byte[] img = Files.toByteArray(fileToScan);
            this.initImage(img);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initAccountingRecordResults(List<AccountingRecord> data) {
       // accountingRecordsTabController.initData(data);
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
            Stage current = (Stage) tabPane.getScene().getWindow();
            ImageView view = (ImageView) current.getScene().lookup("#pdfImage");
            //Parent p = tabPane.getParent();
            //Parent p = tabPane.getParent().getParent().getParent();
            //ImageView view = (ImageView) p.lookup("#pdfImage");
            Image imageToView = SwingFXUtils.toFXImage(img, null);
            view.setImage(imageToView);
            view.setPreserveRatio(true);
            view.setSmooth(true);
            view.setCache(true);
        }
    }
}
