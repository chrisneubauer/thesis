package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.Scan;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import java.io.InputStream;

/**
 * Created by Christoph Neubauer on 22.11.2016.
 */
public class TabController extends SplitPaneController {

    @FXML public VBox invoiceTab;
    @FXML public VBox accountingRecordsTab;
    @FXML private ResultsController invoiceTabController;
    @FXML public TabPane tabPane;

    public void initResults(Scan extractedInformation, String text) {
        invoiceTabController.initData(extractedInformation.getInvoiceInformation(), text);
        BufferedImage img = null;
        try {
            PDDocument pdf = PDDocument.load(extractedInformation.getFile());
            PDFRenderer renderer = new PDFRenderer(pdf);
            img = renderer.renderImageWithDPI(0, 600);
        } catch (Exception ex) {
            // no pdf, try again as image
            try {
                InputStream in = new ByteArrayInputStream(extractedInformation.getFile());
                img = ImageIO.read(in);
            } catch (Exception ex2) {
                Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to parse image!");
            }
        }

        if (img != null) {

            Parent p = tabPane.getParent().getParent().getParent();
            Scene total = p.getScene();
            ImageView view = (ImageView) p.lookup("#pdfImage");
            //ImageView view = (ImageView) total.lookup("#rightPane").lookup("#pdfImage");
            view.setImage(SwingFXUtils.toFXImage(img, null));
        }
    }

    public void setRightPane(AnchorPane rightPane) {
        this.rightPane = rightPane;
    }

    public void setLeftPane(AnchorPane leftPane) {
        this.leftPane = leftPane;
    }
}
