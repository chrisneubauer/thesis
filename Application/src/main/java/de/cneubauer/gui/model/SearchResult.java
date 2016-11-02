package de.cneubauer.gui.model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Created by Christoph Neubauer on 01.11.2016.
 */
public class SearchResult {
    private ObjectProperty<LocalDate> date;
    private DoubleProperty value;
    private StringProperty debitor;
    private StringProperty creditor;
    private ObjectProperty<byte[]> file;

    public SearchResult() {
        this.date = new SimpleObjectProperty<>();
        this.value = new SimpleDoubleProperty();
        this.debitor = new SimpleStringProperty();
        this.creditor = new SimpleStringProperty();
        this.file = new SimpleObjectProperty<>();
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public void setDate(LocalDate date) {
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

    public byte[] getFile() {
        return file.get();
    }

    public ObjectProperty<byte[]> fileProperty() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file.set(file);
    }

}
