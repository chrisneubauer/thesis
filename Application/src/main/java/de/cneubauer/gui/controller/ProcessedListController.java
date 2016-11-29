package de.cneubauer.gui.controller;

import de.cneubauer.gui.ApplicationStart;
import de.cneubauer.gui.model.ProcessResult;
import de.cneubauer.util.enumeration.ScanStatus;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;

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

    private ObservableList<ProcessResult> results;

    @FXML
    private void initialize() {
        // Initialize the table with the columns
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        documentColumn.setCellValueFactory(cellData -> cellData.getValue().docNameProperty());
        problemColumn.setCellValueFactory(cellData -> cellData.getValue().problemProperty());
        fileColumn.setCellValueFactory(cellData -> cellData.getValue().fileProperty());
        fileColumn.setCellFactory(p -> new ButtonCell());
    }

    void initData(ObservableList<ProcessResult> data) {
        this.results = data;

        this.fillListWithValues();
        this.initialize();
    }

    private void fillListWithValues() {
        progressedList.setItems(results);
    }


    // Holds logic for the open pdf button
    private class ButtonCell extends TableCell<ProcessResult, File> {
        private Button button;

        ButtonCell() {
            button = new Button();
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FileChooser fileChooser = new FileChooser();

                    //Set extension filter
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
                    fileChooser.getExtensionFilters().add(extFilter);

                    //Show save file dialog
                    File file = fileChooser.showSaveDialog(new Stage());

                    if (file != null) {
                        //OutputStream out;
                        try {
                            //out = new FileOutputStream(file);
                            //out.write(getItem());
                            //out.close();
                            Logger.getLogger(this.getClass()).log(Level.INFO, "opening pdf on " + file.getPath());

                            ApplicationStart.getHostServicesInternal().showDocument(file.getPath());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
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
}
