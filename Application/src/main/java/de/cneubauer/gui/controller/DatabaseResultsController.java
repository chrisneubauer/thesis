package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.service.DatabaseResultsService;
import de.cneubauer.gui.model.SearchResult;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Christoph Neubauer on 25.10.2016.
 * This controller manages requests from the user to find pdfs in the database
 * It is called from the DatabaseController and returns a list of db entries
 */
public class DatabaseResultsController extends GUIController {
    public HostServices getHostServices() {
        return hostServices;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    private HostServices hostServices;

    private LocalDate date;
    private String deb;
    private String cred;
    private double value;

    //TABLE VIEW AND DATA
    private ObservableList<SearchResult> data;

    @FXML
    private TableView<SearchResult> dbResultList;

    @FXML
    private TableColumn<SearchResult, String> dateColumn;

    @FXML
    private TableColumn<SearchResult, Double> valueColumn;

    @FXML
    private TableColumn<SearchResult, String> debitorColumn;

    @FXML
    private TableColumn<SearchResult, String> creditorColumn;

    @FXML
    private TableColumn<SearchResult, byte[]> downloadColumn;

    @FXML
    private void initialize() {
        // Initialize the table with the columns
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty().asString());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());
        debitorColumn.setCellValueFactory(cellData -> cellData.getValue().debitorProperty());
        creditorColumn.setCellValueFactory(cellData -> cellData.getValue().creditorProperty());
        downloadColumn.setCellValueFactory(cellData -> cellData.getValue().fileProperty());
        //downloadColumn.setCellFactory(this.setDownloadButtonCellFactory());
    }
/*
    private Callback<TableColumn<SearchResult, byte[]>, TableCell<SearchResult, byte[]>> setDownloadButtonCellFactory() {
        return new Callback<TableColumn<SearchResult, byte[]>, TableCell<SearchResult, byte[]>>() {
            @Override
            public TableCell<SearchResult, byte[]> call(TableColumn<SearchResult, byte[]> param) {
                TableCell<SearchResult, String> cell = new TableCell<SearchResult, String>() {
                    Button btn = new Button("Download");

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction((ActionEvent event) ->
                            {
                                FileChooser fileChooser = new FileChooser();

                                //Set extension filter
                                //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                                //fileChooser.getExtensionFilters().add(extFilter);

                                //Show save file dialog
                                File file = fileChooser.showSaveDialog(this.getScene().getWindow());

                                if (file != null) {
                                    OutputStream out = null;
                                    try {
                                        out = new FileOutputStream(file);
                                        out.write(pdfFile);
                                        out.close();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                }
            }
        }
    }
*/
    void initData(LocalDate date, String debitor, String creditor, double value) {
        this.date = date;
        this.deb = debitor;
        this.cred = creditor;
        this.value = value;

        this.fillListWithValues();
        this.initialize();
        //this.addOpenPdfImages();
    }
/*
    private void addOpenPdfImages() {
        for (int i = 0; i < this.dbResultList.getItems().size(); i++) {
            SearchResult sr = this.dbResultList.getItems().get(i);
            Cell cell = new TableCell();
            cell.
            TableColumn<SearchResult, byte[]> column = (TableColumn<SearchResult, byte[]>) this.dbResultList.getColumns().get(this.dbResultList.getColumns().size());
            byte[] cellData = column.getCellData(i);
            cellData
        }
    }
*/
    public void returnToDatabaseSearch(ActionEvent e) {
        try {
            super.openDatabaseMenu(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListWithValues() {
        data = this.getFromDb();
        dbResultList.setItems(data);
    }

    private ObservableList<SearchResult> getFromDb() {
        DatabaseResultsService service = new DatabaseResultsService();
        List<Scan> scanList = service.getFromDatabase(date, deb, cred, value);

        ObservableList<SearchResult> allData = FXCollections.observableArrayList();
        for (Scan s : scanList) {
            SearchResult sr = new SearchResult();
            sr.setDate(s.getInvoiceInformation().getIssueDate().toLocalDateTime().toLocalDate());
            //sr.setDate(LocalDate.from(s.getInvoiceInformation().getIssueDate().toLocalDateTime()));//.toLocalDateTime().toLocalDate());
            sr.setValue(s.getInvoiceInformation().getGrandTotal());
            sr.setCreditor(s.getInvoiceInformation().getCreditor().toString());
            sr.setDebitor(s.getInvoiceInformation().getDebitor().toString());
            /*Byte[] file = new Byte[s.getFile().length];
            for(int i = 0; i < s.getFile().length; i++) {
                file[i] = s.getFile()[i];
            }*/
            sr.setFile(s.getFile());
            allData.add(sr);
        }

        return allData;
        //return new ObservableListWrapper<Scan>(scanList);
        //result.addAll(scanList);
        //return result;
    }

}
