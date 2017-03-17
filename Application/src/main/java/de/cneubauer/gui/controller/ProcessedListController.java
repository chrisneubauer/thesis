package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Position;
import de.cneubauer.domain.service.DatabaseService;
import de.cneubauer.gui.model.ExtractionModel;
import de.cneubauer.gui.model.ProcessResult;
import de.cneubauer.ml.LearningService;
import de.cneubauer.ml.Model;
import de.cneubauer.ml.ModelWriter;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Christoph Neubauer on 29.11.2016.
 * ProcessedListController shows list of all processed files
 */
public class ProcessedListController extends GUIController {
    public TableView<ProcessResult> processedList;
    public TableColumn<ProcessResult, ScanStatus> statusColumn;
    public TableColumn<ProcessResult, String> documentColumn;
    public TableColumn<ProcessResult, String> problemColumn;
    public TableColumn<ProcessResult, File> fileColumn;
    public Button saveRevised;
    private List<ProcessResult> model;

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
        } else if (state.equals(ScanStatus.ISSUE)) {
            view = new ImageView("img/Circle_Yellow.png");
        } else {
            view = new ImageView("img/Circle_Green.png");
        }
        view.setFitHeight(32);
        view.setFitWidth(32);
        return view;
    }

    void initData(ObservableList<ProcessResult> data) {
        processedList.setItems(data);
        this.model = data;
        this.initialize();
    }

    // if called, all results should be stored to the database except the ones that have not been revised yet
    public void saveRevised() {
        int counter = 0;
        DatabaseService service = new DatabaseService();
        for (ProcessResult result : this.model) {
            if (result.getStatus().equals(ScanStatus.OK)) {
                service.saveProcessResult(result);
                ModelWriter writer = new ModelWriter();
                LearningService service1 = new LearningService();
                for (Position r : result.getExtractionModel().getUpdatedRecords()) {
                    Model m = service1.createModel(r);
                    writer.writeToFile(m);
                }
                counter++;
            }
        }
        Alert information = new Alert(Alert.AlertType.INFORMATION);
        information.setContentText(counter + " documents have been saved.");
        information.setHeaderText("Saving successful");
        information.show();
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
                        Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getMaxX() - 100, Screen.getPrimary().getVisualBounds().getMaxY() - 20);

                        Stage popupStage = new Stage(StageStyle.DECORATED);
                        popupStage.setX(stage.getX() + 20);
                        popupStage.setY(stage.getY() + 20);
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

    private ProcessedListController getControllerReference() {
        return this;
    }

    void updateSelected(int index, ExtractionModel newModel) {
        this.processedList.getItems().get(index).setExtractionModel(newModel);
        this.processedList.getItems().get(index).setStatus(ScanStatus.OK);
        this.processedList.getItems().get(index).setProblem("No Problems detected");
        this.processedList.refresh();
    }
}
