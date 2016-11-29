package de.cneubauer.gui.controller;

import com.google.common.io.Files;
import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.service.OCRDataExtractorService;
import de.cneubauer.ocr.tesseract.TesseractWrapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
            boolean valid = this.validateFileInput();
            if (valid) {
                String path = this.fileInput.getText();
                File f = new File(path);
                BufferedImage img = null;
                try {
                    PDDocument pdf = PDDocument.load(f);
                    PDFRenderer renderer = new PDFRenderer(pdf);
                    img = renderer.renderImageWithDPI(0, 1200);
                } catch (Exception ex) {
                    // no pdf, try again as image
                    try {
                        img = ImageIO.read(f);
                    } catch (Exception ex2) {
                        Logger.getLogger(this.getClass()).log(Level.ERROR, "Unable to parse image!");
                    }
                }
                if (img != null) {
                    Parent p = fileInput.getParent().getParent().getParent();
                    ImageView view = (ImageView) p.lookup("#pdfImage");
                    view.setImage(SwingFXUtils.toFXImage(img, null));
                    view.setPreserveRatio(true);
                    view.setSmooth(true);
                    view.setCache(true);
                }
            }
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
            Invoice extractedInformation = service.extractInvoiceInformation();
            List<AccountingRecord> recordList = service.extractAccountingRecordInformation();
            scan.setInvoiceInformation(extractedInformation);
            try {
                scan.setFile(Files.toByteArray(fileToScan));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            this.openExtractionInformationMenu(e, scan, recordList, fileToScan);
        }
    }

    //this method opens invoice information after ocr processing using ResultsController
    @FXML
    private void openExtractionInformationMenu(Event e, Scan extractedInformation, List<AccountingRecord> recordList, File fileToScan) {
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
            ctrl.initResults(extractedInformation, this.fileInput.getText(), recordList, fileToScan);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean validateFileInput() {
        return this.fileInput.getText() != null;
    }
/*
    double xStart = 0;
    double yStart = 0;
    double xEnd = 0;
    double yEnd = 0;

    public void selectHeader(ActionEvent actionEvent) {
        Node n = (Node) actionEvent.getSource();
        n.getScene().setCursor(Cursor.CROSSHAIR);
        // start and end points of image
        double minX = pdfImage.getX();
        double minY = pdfImage.getY();
        double maxX = pdfImage.getX() + pdfImage.getFitWidth();
        double maxY = pdfImage.getY() + pdfImage.getFitHeight();

        n.getScene().onMouseDragEnteredProperty().setValue(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getX() < minX || event.getX() > maxX || event.getY() < minY || event.getY() > maxY) {
                    n.getScene().setCursor(Cursor.DEFAULT);
                } else {
                    xStart = event.getX();
                    yStart = event.getY();
                }
            }
        });

        n.getScene().onMouseDragExitedProperty().setValue(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
                if (event.getX() < minX || event.getX() > maxX || event.getY() < minY || event.getY() > maxY) {
                    n.getScene().setCursor(Cursor.DEFAULT);
                } else {
                    xEnd = event.getX();
                    yEnd = event.getY();
                    Rectangle2D headerRectangle = new Rectangle2D(xStart, yStart, xEnd - xStart, yEnd - yStart);
                    Logger.getLogger(this.getClass()).log(Level.INFO, "header selected, from " + xStart + "," + yStart + " to " + xEnd + "," + yEnd);
                }
            }
        });
    }*/
}
