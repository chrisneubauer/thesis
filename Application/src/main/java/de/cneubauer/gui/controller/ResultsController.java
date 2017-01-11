package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.helper.InvoiceFileHelper;
import de.cneubauer.domain.service.ZugFerdExtendService;
import de.cneubauer.util.enumeration.ValidationStatus;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Christoph Neubauer on 21.10.2016.
 * Shows scanned data before writing into database
 * Also contains validation logic
 */
public class ResultsController extends GUIController {
    public Button SaveExtractedInvoiceToDatabase;
    private SplitPaneController superCtrl;

    @FXML private TextField extractedInvoiceNumber;
    @FXML private TextField extractedCreditor;
    @FXML private TextField extractedDebitor;
    @FXML private TextField extractedIssueDate;
    @FXML private TextField extractedLineTotal;
    @FXML private TextField extractedChargeTotal;
    @FXML private TextField extractedTaxBasisTotal;
    @FXML private TextField extractedTaxTotal;
    @FXML private TextField extractedAllowanceTotal;
    @FXML private TextField extractedGrandTotal;
    @FXML private CheckBox extractedHasSkonto;
    @FXML private Label extractedSkontoLabel;
    @FXML private TextField extractedSkonto;
    @FXML private TextField extractedDeliveryDate;
    private Invoice model;

    void initData(Invoice extractedInformation, SplitPaneController superCtrl) {
        this.superCtrl = superCtrl;
        this.model = extractedInformation;
        this.showResultsBeforeSave(extractedInformation);
        this.extractedDebitor.getScene().getStylesheets().add(String.valueOf(getClass().getResource("../../../../css/validationError.css")));
        this.addAllListeners();
    }

    private void addAllListeners() {
        this.extractedInvoiceNumber.textProperty().addListener(this.addListenerToTextField(this.extractedInvoiceNumber));
        this.extractedIssueDate.textProperty().addListener(this.addListenerToTextField(this.extractedIssueDate));
        this.extractedDebitor.textProperty().addListener(this.addListenerToTextField(this.extractedDebitor));
        this.extractedCreditor.textProperty().addListener(this.addListenerToTextField(this.extractedCreditor));
        this.extractedLineTotal.textProperty().addListener(this.addListenerToTextField(this.extractedLineTotal));
        this.extractedChargeTotal.textProperty().addListener(this.addListenerToTextField(this.extractedChargeTotal));
        this.extractedTaxBasisTotal.textProperty().addListener(this.addListenerToTextField(this.extractedTaxBasisTotal));
        this.extractedAllowanceTotal.textProperty().addListener(this.addListenerToTextField(this.extractedAllowanceTotal));
        this.extractedTaxTotal.textProperty().addListener(this.addListenerToTextField(this.extractedTaxTotal));
        this.extractedGrandTotal.textProperty().addListener(this.addListenerToTextField(this.extractedGrandTotal));
        this.extractedSkonto.textProperty().addListener(this.addListenerToTextField(this.extractedSkonto));
        this.extractedDeliveryDate.textProperty().addListener(this.addListenerToTextField(this.extractedDeliveryDate));
        Logger.getLogger(this.getClass()).log(Level.INFO, "Listeners added to textfields");
    }

    private ChangeListener<String> addListenerToTextField(TextField field) {
        return (observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 0) {
                field.getStyleClass().remove("error");
            } else {
                field.getStyleClass().add("error");
            }
        };
    }

    @FXML
    private void showResultsBeforeSave(Invoice invInfo) {
        if(invInfo.getInvoiceNumber() != null) {
            this.extractedInvoiceNumber.setText(invInfo.getInvoiceNumber());
        }
        if(invInfo.getIssueDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            String date = formatter.format(new Date(invInfo.getIssueDate().getTime()));
            this.extractedIssueDate.setText(date);
        }
        if(invInfo.getDebitor() != null) {
            this.extractedDebitor.setText(invInfo.getDebitor().toString());
        }
        if(invInfo.getCreditor() != null) {
            this.extractedCreditor.setText(invInfo.getCreditor().toString());
        }

        this.extractedLineTotal.setText(String.valueOf(invInfo.getLineTotal()));
        this.extractedChargeTotal.setText(String.valueOf(invInfo.getChargeTotal()));
        this.extractedTaxBasisTotal.setText(String.valueOf(invInfo.getTaxBasisTotal()));
        this.extractedTaxTotal.setText(String.valueOf(invInfo.getTaxTotal()));
        this.extractedAllowanceTotal.setText(String.valueOf(invInfo.getAllowanceTotal()));
        this.extractedGrandTotal.setText(String.valueOf(invInfo.getGrandTotal()));

        this.extractedHasSkonto.setSelected(invInfo.isHasSkonto());
        if (invInfo.isHasSkonto()) {
            this.extractedSkonto.setVisible(true);
            this.extractedSkontoLabel.setVisible(true);
            this.extractedSkonto.setText(String.valueOf(invInfo.getSkonto()));
        } else {
            this.extractedSkontoLabel.setVisible(false);
            this.extractedSkonto.setVisible(false);
        }

        if(invInfo.getDeliveryDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            String date = formatter.format(new Date(invInfo.getDeliveryDate().getTime()));
            this.extractedDeliveryDate.setText(date);
        }
    }

    @FXML
    @Deprecated
    public void saveToDatabase() {
        //if (this.validateFieldsBeforeSave()) {
            try {
                Logger.getLogger(this.getClass()).log(Level.INFO, "Fields valid. Initiating save sequence");
                ZugFerdExtendService service = new ZugFerdExtendService();

                Invoice i = this.convertToInvoice();
                this.generateSuccessMessage();
                this.closeExtractionAfterSave();
            } catch (Exception ex){
                this.generateErrorMessage(ex.getMessage());
            }
        //} else {
            // make message and validation errors
        //    this.generateErrorMessage("Validation failed! Please correct the errors.");
        //}
    }

    private void generateErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Could not save!");
        alert.setContentText(message);
        alert.show();
    }

    private void generateSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Saved successfully");
        alert.setContentText("The invoice has been successfully saved!");
        alert.showAndWait();
    }

    ValidationStatus validateFieldsBeforeSave() {
        ValidationStatus result = ValidationStatus.OK;

        if (this.extractedInvoiceNumber.getText() == null || this.extractedInvoiceNumber.getText().isEmpty()) {
            this.extractedInvoiceNumber.getStyleClass().add("error");
            result = ValidationStatus.UNKNOWNISSUE;
        }
        if (this.extractedCreditor.getText() == null || this.extractedCreditor.getText().isEmpty()) {
            ObservableList<String> styles = this.extractedCreditor.getStyleClass();
            styles.add("error");
            this.extractedCreditor.getStyleClass().add("error");
            result = ValidationStatus.UNKNOWNISSUE;
        }
        if (this.extractedDebitor.getText() == null || this.extractedDebitor.getText().isEmpty()) {
            this.extractedDebitor.getStyleClass().add("error");
            result = ValidationStatus.UNKNOWNISSUE;
        }
        if (this.extractedIssueDate.getText() == null || this.extractedIssueDate.getText().isEmpty()) {
            this.extractedIssueDate.getStyleClass().add("error");
            result = ValidationStatus.UNKNOWNISSUE;
        }
        if (this.extractedLineTotal.getText() == null || this.extractedLineTotal.getText().isEmpty()) {
            this.extractedLineTotal.getStyleClass().add("error");
            result = ValidationStatus.UNKNOWNISSUE;
        }
        if (this.extractedChargeTotal.getText() == null || this.extractedChargeTotal.getText().isEmpty()) {
            this.extractedChargeTotal.getStyleClass().add("error");
            result = ValidationStatus.UNKNOWNISSUE;
        }
        if (this.extractedTaxBasisTotal.getText() == null || this.extractedTaxBasisTotal.getText().isEmpty()) {
            this.extractedTaxBasisTotal.getStyleClass().add("error");
            result = ValidationStatus.UNKNOWNISSUE;
        }
        if (this.extractedTaxTotal.getText() == null || this.extractedTaxTotal.getText().isEmpty()) {
            this.extractedTaxTotal.getStyleClass().add("error");
            result = ValidationStatus.UNKNOWNISSUE;
        } else {
            this.extractedTaxTotal.getStyleClass().remove("error");
        }
        if (this.extractedAllowanceTotal.getText() == null || this.extractedAllowanceTotal.getText().isEmpty()) {
            this.extractedAllowanceTotal.getStyleClass().add("error");
            result = ValidationStatus.UNKNOWNISSUE;
        }
        if (this.extractedGrandTotal.getText() == null || this.extractedGrandTotal.getText().isEmpty()) {
            this.extractedGrandTotal.getStyleClass().add("error");
            result = ValidationStatus.UNKNOWNISSUE;
        }
        if (this.extractedHasSkonto.isSelected()) {
            if (this.extractedSkonto.getText() == null || this.extractedSkonto.getText().isEmpty()) {
                this.extractedSkonto.getStyleClass().add("error");
                result = ValidationStatus.UNKNOWNISSUE;
            }
        }
        if (this.extractedDeliveryDate.getText() == null || this.extractedDeliveryDate.getText().isEmpty()) {
            this.extractedDeliveryDate.getStyleClass().add("error");
            result = ValidationStatus.UNKNOWNISSUE;
        }
        return result;
    }

    private Invoice convertToInvoice() {
        Invoice result = new Invoice();
        //TODO Check conversation from dd-mm-yyyy to timestamp works
        result.setIssueDate(this.convertDateToTimestamp(this.extractedIssueDate.getText()));
        result.setInvoiceNumber(this.extractedInvoiceNumber.getText());
        result.setDebitor(new LegalPerson(this.extractedDebitor.getText()));
        result.setCreditor(new LegalPerson(this.extractedCreditor.getText()));
        result.setDeliveryDate(this.convertDateToTimestamp(this.extractedDeliveryDate.getText()));
        result.setLineTotal(Double.valueOf(this.extractedLineTotal.getText()));
        result.setAllowanceTotal(Double.valueOf(this.extractedAllowanceTotal.getText()));
        result.setChargeTotal(Double.valueOf(this.extractedChargeTotal.getText()));
        result.setGrandTotal(Double.valueOf(this.extractedGrandTotal.getText()));
        result.setTaxBasisTotal(Double.valueOf(this.extractedTaxBasisTotal.getText()));
        result.setTaxTotal(Double.valueOf(this.extractedTaxTotal.getText()));
        result.setHasSkonto(this.extractedHasSkonto.isSelected());
        if (this.extractedHasSkonto.isSelected()) {
            result.setSkonto(Double.valueOf(this.extractedSkonto.getText()));
        } else {
            result.setSkonto(0);
        }
        return result;
    }

    private Timestamp convertDateToTimestamp(String date) {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        Date d;
        try {
            d = format.parse(date);
        } catch (Exception e) {
            d = new Date(System.currentTimeMillis());
        }
        return new Timestamp(d.getTime());
    }

    @FXML
    private void checkSkonto() {
        if (this.extractedHasSkonto.isSelected()) {
            this.extractedSkonto.setVisible(true);
            this.extractedSkontoLabel.setVisible(true);
        } else {
            this.extractedSkonto.setVisible(false);
            this.extractedSkontoLabel.setVisible(false);
        }
    }


    //this method closes the view from ResultsController after save
    @FXML
    private void closeExtractionAfterSave() {
        try {
            Stage stage = (Stage) this.extractedSkontoLabel.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../FXML/mainMenu.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);

            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // when called, invoice has been reviewed by the user
    // set invoice to be reviewed and update all information given
    public void setReviewed() {
        superCtrl.reviseAll();
    }

    Invoice updateInformation() {
        return this.convertToInvoice();
    }

    void addRevisedToFile() {
        // check if all records have been revised before saving
        //if (this.validateFieldsBeforeSave()) {
            if (this.model.isRevised()) {
                InvoiceFileHelper.write(this.model.getDebitor().getName(), this.model.getCreditor().getName());
            }
        //}
    }

    public void checkReviewed() {
        this.model.setRevised(true);
    }
}
