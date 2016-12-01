package de.cneubauer.gui.controller;

import de.cneubauer.gui.model.ExtractionModel;
import de.cneubauer.gui.model.ProcessResult;
import de.cneubauer.util.enumeration.ScanStatus;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Christoph Neubauer on 29.11.2016.
 * ProcessedListController shows list of all processed files
 */
public class ProcessedListController extends GUIController {
    public TableView<ProcessResult> progressedList;
    public TableColumn<ProcessResult, ScanStatus> statusColumn;
    public TableColumn<ProcessResult, String> documentColumn;
    public TableColumn<ProcessResult, String> problemColumn;
    public TableColumn<ProcessResult, File> fileColumn;
    public Button saveRevised;

    @FXML
    private void initialize() {
        // Initialize the table with the columns
        statusColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        statusColumn.setCellFactory(column -> new TableCell<ProcessResult, ScanStatus>() {
            @Override
            protected void updateItem(ScanStatus item, boolean empty) {
                if (!empty && item != null) {
                    ImageView v = setStatusImage(item);
                    setGraphic(v);
                }
            }
        });
        documentColumn.setCellValueFactory(cellData -> cellData.getValue().docNameProperty());
        problemColumn.setCellValueFactory(cellData -> cellData.getValue().problemProperty());
        fileColumn.setCellValueFactory(cellData -> cellData.getValue().fileProperty());
        fileColumn.setCellFactory(p -> new ButtonCell());
    }

    private ImageView setStatusImage(ScanStatus state) {
        ImageView view;
        if (state.equals(ScanStatus.ERROR)) {
            view = new ImageView("img/Circle_Red.png");
        } else {
            view = new ImageView("img/Circle_Green.png");
        }
        view.setFitHeight(32);
        view.setFitWidth(32);
        return view;
    }

    void initData(ObservableList<ProcessResult> data) {
        progressedList.setItems(data);
        this.initialize();
    }

    // Holds logic for the open pdf button
    private class ButtonCell extends TableCell<ProcessResult, File> {
        private Button button;

        ButtonCell() {
            button = new Button();
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        TableRow row = (TableRow) button.getParent().getParent();
                        ProcessResult selected = (ProcessResult) row.getItem();

                        Stage stage = (Stage) button.getScene().getWindow();

                        Locale locale = getCurrentLocale();
                        ResourceBundle bundle = ResourceBundle.getBundle("bundles/Application", locale);
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../FXML/splitPane.fxml"), bundle);
                        Parent root = loader.load();
                        Scene scene = new Scene(root, 800, 600);

                        Stage popupStage = new Stage(StageStyle.DECORATED);
                        popupStage.setX(stage.getX() + 100);
                        popupStage.setY(stage.getY() + 100);
                        popupStage.setTitle("Review");
                        popupStage.initOwner(stage);
                        popupStage.initModality(Modality.APPLICATION_MODAL);
                        popupStage.setScene(scene);
                        popupStage.show();

                        SplitPaneController ctrl = loader.getController();

                        ctrl.initResults(row.getIndex(), selected.getExtractionModel(), selected.getFile(), getControllerReference());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(File result, boolean empty) {
            super.updateItem(result, empty);
            if(!empty){
                button.setText("View PDF");
                setGraphic(button);
            } else {
                setGraphic(null);
            }
        }
    }

    public ProcessedListController getControllerReference() {
        return this;
    }

    protected void updateSelected(int index, ExtractionModel newModel) {
        this.progressedList.getItems().get(index).setExtractionModel(newModel);
    }
}
