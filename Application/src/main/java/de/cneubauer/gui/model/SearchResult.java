package de.cneubauer.gui.model;

import de.cneubauer.domain.bo.Scan;
import javafx.beans.property.*;

import java.sql.Date;

/**
 * Created by Christoph Neubauer on 01.11.2016.
 * Model for Database SearchResults
 * UsedBy: DatabaseResultsController
 */
public class SearchResult {
    private ObjectProperty<Date> date;
    private DoubleProperty value;
    private StringProperty debitor;
    private StringProperty creditor;
    private ObjectProperty<Scan> scan;
    //private ObjectProperty<byte[]> file;

    public SearchResult() {
        this.date = new SimpleObjectProperty<>();
        this.value = new SimpleDoubleProperty();
        this.debitor = new SimpleStringProperty();
        this.creditor = new SimpleStringProperty();
        this.scan = new SimpleObjectProperty<>();
        //this.file = new SimpleObjectProperty<>();
    }

    public Date getDate() {
        return date.get();
    }

    public ObjectProperty<Date> dateProperty() {
        return date;
    }

    public void setDate(Date date) {
        this.date.set(date);
    }

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    public String getDebitor() {
        return debitor.get();
    }

    public StringProperty debitorProperty() {
        return debitor;
    }

    public void setDebitor(String debitor) {
        this.debitor.set(debitor);
    }

    public String getCreditor() {
        return creditor.get();
    }

    public StringProperty creditorProperty() {
        return creditor;
    }

    public void setCreditor(String creditor) {
        this.creditor.set(creditor);
    }

    public Scan getScan() {
        return scan.get();
    }

    public ObjectProperty<Scan> scanProperty() {
        return scan;
    }

    public void setScan(Scan scan) {
        this.scan.set(scan);
    }
/*
    public byte[] getFile() {
        return file.get();
    }

    public ObjectProperty<byte[]> fileProperty() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file.set(file);
    }
*/
}
