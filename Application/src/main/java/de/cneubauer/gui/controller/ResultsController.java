package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.service.ZugFerdExtendService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Christoph Neubauer on 21.10.2016.
 * Shows scanned data before writing into database
 * Also contains validation logic
 */
public class ResultsController extends GUIController {
    private String filePath;

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

    void initData(Invoice extractedInformation, String pdfPath) {
        this.filePath = pdfPath;
        this.showResultsBeforeSave(extractedInformation);
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
    public void saveToDatabase() {
        if (this.validateFieldsBeforeSave()) {
            try {
                ZugFerdExtendService service = new ZugFerdExtendService();

                Invoice i = this.convertToInvoice();
                byte[] originalPdf = Files.readAllBytes(new File(this.filePath).toPath());

                byte[] pdf = service.appendInvoiceToPDF(originalPdf, i);
                service.save(pdf, i);
                this.generateSuccessMessage();
            } catch (Exception ex){
                this.generateErrorMessage(ex.getMessage());
            }
        } else {
            // make message and validation errors
            this.generateErrorMessage("Validation failed! Please correct the errors.");
        }
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
        alert.show();
    }

    private boolean validateFieldsBeforeSave() {
        boolean result;
        result = this.extractedInvoiceNumber.getText() != null && !this.extractedInvoiceNumber.getText().isEmpty();
        result = result && this.extractedCreditor.getText() != null && !this.extractedCreditor.getText().isEmpty();
        result = result && this.extractedDebitor.getText() != null && !this.extractedDebitor.getText().isEmpty();
        result = result && this.extractedIssueDate.getText() != null && !this.extractedIssueDate.getText().isEmpty();
        result = result && this.extractedLineTotal.getText() != null && !this.extractedLineTotal.getText().isEmpty();
        result = result && this.extractedChargeTotal.getText() != null && !this.extractedChargeTotal.getText().isEmpty();
        result = result && this.extractedTaxBasisTotal.getText() != null && !this.extractedTaxBasisTotal.getText().isEmpty();
        result = result && this.extractedTaxTotal.getText() != null && !this.extractedTaxTotal.getText().isEmpty();
        result = result && this.extractedAllowanceTotal.getText() != null && !this.extractedAllowanceTotal.getText().isEmpty();
        result = result && this.extractedGrandTotal.getText() != null && !this.extractedGrandTotal.getText().isEmpty();
        if (this.extractedHasSkonto.isSelected()) {
            result = result && this.extractedSkonto.getText() != null && !this.extractedSkonto.getText().isEmpty();
        }
        result = result && this.extractedDeliveryDate.getText() != null && !this.extractedDeliveryDate.getText().isEmpty();
        return result;
    }

    private Invoice convertToInvoice() {
        Invoice result = new Invoice();
        //TODO Check conversation from dd-mm-yyyy to timestamp works
        result.setIssueDate(Timestamp.valueOf(this.extractedIssueDate.getText()));
        result.setInvoiceNumber(this.extractedInvoiceNumber.getText());
        result.setDebitor(new LegalPerson(this.extractedDebitor.getText()));
        result.setCreditor(new LegalPerson(this.extractedCreditor.getText()));
        result.setDeliveryDate(Timestamp.valueOf(this.extractedDeliveryDate.getText()));
        result.setLineTotal(Double.valueOf(this.extractedLineTotal.getText()));
        result.setAllowanceTotal(Double.valueOf(this.extractedAllowanceTotal.getText()));
        result.setChargeTotal(Double.valueOf(this.extractedChargeTotal.getText()));
        result.setGrandTotal(Double.valueOf(this.extractedGrandTotal.getText()));
        result.setTaxBasisTotal(Double.valueOf(this.extractedTaxBasisTotal.getText()));
        result.setTaxTotal(Double.valueOf(this.extractedTaxTotal.getText()));
        result.setHasSkonto(Boolean.valueOf(this.extractedHasSkonto.getText()));
        result.setSkonto(Double.valueOf(this.extractedSkonto.getText()));
        return result;
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
}
