package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Position;
import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.service.AccountingRecordWriter;
import de.cneubauer.domain.service.DatabaseResultsService;
import de.cneubauer.gui.Start;
import de.cneubauer.gui.model.SearchResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Christoph Neubauer on 25.10.2016.
 * This controller manages requests from the user to find pdfs in the database
 * It is called from the DatabaseController and returns a list of db entries
 */
public class DatabaseResultsController extends GUIController {
    private LocalDate date;
    private LocalDate dateTo;
    private String deb;
    private String cred;
    private double value;

    @FXML private TableView<SearchResult> dbResultList;
    @FXML private TableColumn<SearchResult, String> dateColumn;
    @FXML private TableColumn<SearchResult, Double> valueColumn;
    @FXML private TableColumn<SearchResult, String> debitorColumn;
    @FXML private TableColumn<SearchResult, String> creditorColumn;
    @FXML private TableColumn<SearchResult, Scan> downloadColumn;

    @FXML
    private void initialize() {
        // Initialize the table with the columns
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty().asString());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());
        debitorColumn.setCellValueFactory(cellData -> cellData.getValue().debitorProperty());
        creditorColumn.setCellValueFactory(cellData -> cellData.getValue().creditorProperty());
        downloadColumn.setCellValueFactory(cellData -> cellData.getValue().scanProperty()); //.fileProperty());
        downloadColumn.setCellFactory(p -> new ButtonCell());
    }

    void initData(LocalDate date, String debitor, String creditor, double value, LocalDate dateToValue) {
        this.date = date;
        this.deb = debitor;
        this.cred = creditor;
        this.value = value;
        this.dateTo = dateToValue;

        this.fillListWithValues();
    }

    public void returnToDatabaseSearch(ActionEvent e) {
        try {
            super.openDatabaseMenu(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fillListWithValues() {
        ObservableList<SearchResult> data = this.getFromDb();
        this.dbResultList.setItems(data);
    }

    private ObservableList<SearchResult> getFromDb() {
        DatabaseResultsService service = new DatabaseResultsService();
        List<Scan> scanList = service.getFromDatabase(date, deb, cred, value, dateTo);

        ObservableList<SearchResult> allData = FXCollections.observableArrayList();
        for (Scan s : scanList) {
            SearchResult sr = new SearchResult();
            sr.setDate(s.getInvoiceInformation().getIssueDate());
            sr.setValue(s.getInvoiceInformation().getGrandTotal());
            sr.setCreditor(s.getInvoiceInformation().getCreditor().toString());
            sr.setDebitor(s.getInvoiceInformation().getDebitor().toString());
            sr.setScan(s);

            allData.add(sr);
        }

        return allData;
    }

    // Holds logic for the open pdf button
    private class ButtonCell extends TableCell<SearchResult, Scan> {
        private Button button;

        ButtonCell() {
            button = new Button();
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    DirectoryChooser dir = new DirectoryChooser();
                    File fileDir = dir.showDialog(new Stage());

                    if (fileDir != null) {
                        OutputStream out;
                        try {
                            String file = fileDir + "\\" + LocalDate.now().toString() + "_" + getItem().getId();
                            File pdfFile = new File(file + ".pdf");

                            out = new FileOutputStream(pdfFile);
                            out.write(getItem().getFile());
                            out.close();
                            Logger.getLogger(this.getClass()).log(Level.INFO, "opening pdf on " + file);

                            AccountingRecordWriter writer = new AccountingRecordWriter();
                            File textFile = new File(file + ".txt");
                            out = new FileOutputStream(textFile);
                            StringBuilder output = new StringBuilder();
                            for (Position pos : getItem().getPositions()) {
                                output.append(writer.convert(pos));
                                Logger.getLogger(this.getClass()).log(Level.INFO, writer.convert(pos));
                            }
                            out.write(output.toString().getBytes(Charset.forName("UTF-8")));
                            out.close();

                            Start.getHostServicesInternal().showDocument(file + ".pdf");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Scan result, boolean empty) {
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
