package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Invoice;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Created by Christoph Neubauer on 22.11.2016.
 */
public class SplitPaneController extends GUIController {
    @FXML public AnchorPane rightPane;
    @FXML public AnchorPane leftPane;
    @FXML public ImageView pdfImage;

    @FXML
    public void showScanForm() {
        try {
            leftPane.getChildren().clear();
            leftPane.getChildren().add(FXMLLoader.load(getClass().getResource("../../../../FXML/scanForm.fxml")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    // this method opens the page where the user can search in the database

    @FXML
    protected void openDatabaseMenu(Event e) {
        try {
            leftPane.getChildren().clear();
            leftPane.getChildren().add(FXMLLoader.load(getClass().getResource("../../../../FXML/searchDatabase.fxml")));
            /*Node node = (Node) e.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("../../../../FXML/searchDatabase.fxml"));
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.show();*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
