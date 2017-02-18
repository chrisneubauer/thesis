package de.cneubauer.gui.model;

import de.cneubauer.util.DocumentCaseSet;
import de.cneubauer.util.enumeration.ScanStatus;
import javafx.beans.property.*;
import java.io.File;

/**
 * Created by Christoph Neubauer on 29.11.2016.
 * Model for ProcessResult of OCR
 * UsedBy: ProgressedListController
 */
public class ProcessResult {
    private ObjectProperty<de.cneubauer.util.enumeration.ScanStatus> status;
    private StringProperty docName;
    private StringProperty problem;
    private ObjectProperty<java.io.File> file;
    private ExtractionModel extractionModel;

    public ProcessResult() {
        this.status = new SimpleObjectProperty<>();
        this.docName = new SimpleStringProperty();
        this.problem = new SimpleStringProperty();
        this.file = new SimpleObjectProperty<>();
    }

    public ScanStatus getStatus() {
        return status.get();
    }

    public ObjectProperty<ScanStatus> statusProperty() {
        return status;
    }

    public void setStatus(ScanStatus status) {
        this.status.set(status);
    }

    public String getDocName() {
        return docName.get();
    }

    public StringProperty docNameProperty() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName.set(docName);
    }

    public String getProblem() {
        return problem.get();
    }

    public StringProperty problemProperty() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem.set(problem);
    }

    public File getFile() {
        return file.get();
    }

    public ObjectProperty<File> fileProperty() {
        return file;
    }

    public void setFile(File file) {
        this.file.set(file);
    }

    public ExtractionModel getExtractionModel() {
        return extractionModel;
    }

    public void setExtractionModel(ExtractionModel extractionModel) {
        this.extractionModel = extractionModel;
    }
}
