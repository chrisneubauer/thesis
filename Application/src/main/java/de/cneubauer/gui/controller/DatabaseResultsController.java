package de.cneubauer.gui.controller;

import de.cneubauer.domain.service.DatabaseResultsService;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.fxml.FXML;

import java.time.LocalDateTime;

/**
 * Created by Christoph Neubauer on 25.10.2016.
 * This controller manages requests from the user to find pdfs in the database
 * It is called from the DatabaseController and returns a list of db entries
 */
public class DatabaseResultsController extends GUIController {
    private LocalDateTime date;
    private String deb;
    private String cred;
    private double value;

    //TABLE VIEW AND DATA
    private ObservableList<ObservableList> data;

    @FXML
    private TableView dbResultList;

    void initData(LocalDateTime date, String debitor, String creditor, double value) {
        this.date = date;
        this.deb = debitor;
        this.cred = creditor;
        this.value = value;
    }

    private void fillListWithValues() {
        data = this.getFromDb();
        dbResultList.setItems(data);
    }

    private ObservableList<ObservableList> getFromDb() {
        DatabaseResultsService service = new DatabaseResultsService();
        service.getFromDatabase(date, deb, cred, value);
        return null;
    }
}
