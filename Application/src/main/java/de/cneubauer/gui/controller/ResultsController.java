package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.dao.LegalPersonDao;
import de.cneubauer.domain.dao.impl.LegalPersonDaoImpl;
import de.cneubauer.domain.helper.InvoiceFileHelper;
import de.cneubauer.util.enumeration.ValidationStatus;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
    private Invoice oldModel;

    void initData(Invoice extractedInformation, SplitPaneController superCtrl) {
        this.superCtrl = superCtrl;
        this.model = extractedInformation;
        this.oldModel = extractedInformation;
        this.updateModel(extractedInformation);
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
    private void updateModel(Invoice invInfo) {
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

    List<ValidationStatus> validateFieldsBeforeSave() {
        List<ValidationStatus> errors = new LinkedList<>();

        if (this.extractedInvoiceNumber.getText() == null || this.extractedInvoiceNumber.getText().isEmpty()) {
            this.extractedInvoiceNumber.getStyleClass().add("error");
            errors.add(ValidationStatus.UNKNOWNISSUE);
        }
        if (this.extractedCreditor.getText() == null || this.extractedCreditor.getText().isEmpty()) {
            ObservableList<String> styles = this.extractedCreditor.getStyleClass();
            styles.add("error");
            this.extractedCreditor.getStyleClass().add("error");
            errors.add(ValidationStatus.UNKNOWNISSUE);
        }
        if (this.extractedDebitor.getText() == null || this.extractedDebitor.getText().isEmpty()) {
            this.extractedDebitor.getStyleClass().add("error");
            errors.add(ValidationStatus.UNKNOWNISSUE);
        }
        if (this.extractedIssueDate.getText() == null || this.extractedIssueDate.getText().isEmpty()) {
            this.extractedIssueDate.getStyleClass().add("error");
            errors.add(ValidationStatus.UNKNOWNISSUE);
        }
        if (this.extractedLineTotal.getText() == null || this.extractedLineTotal.getText().isEmpty()) {
            this.extractedLineTotal.getStyleClass().add("error");
            errors.add(ValidationStatus.UNKNOWNISSUE);
        }
        if (this.extractedChargeTotal.getText() == null || this.extractedChargeTotal.getText().isEmpty()) {
            this.extractedChargeTotal.getStyleClass().add("error");
            errors.add(ValidationStatus.UNKNOWNISSUE);
        }
        if (this.extractedTaxBasisTotal.getText() == null || this.extractedTaxBasisTotal.getText().isEmpty()) {
            this.extractedTaxBasisTotal.getStyleClass().add("error");
            errors.add(ValidationStatus.UNKNOWNISSUE);
        }
        if (this.extractedTaxTotal.getText() == null || this.extractedTaxTotal.getText().isEmpty()) {
            this.extractedTaxTotal.getStyleClass().add("error");
            errors.add(ValidationStatus.UNKNOWNISSUE);
        } else {
            this.extractedTaxTotal.getStyleClass().remove("error");
        }
        if (this.extractedAllowanceTotal.getText() == null || this.extractedAllowanceTotal.getText().isEmpty()) {
            this.extractedAllowanceTotal.getStyleClass().add("error");
            errors.add(ValidationStatus.UNKNOWNISSUE);
        }
        if (this.extractedGrandTotal.getText() == null || this.extractedGrandTotal.getText().isEmpty()) {
            this.extractedGrandTotal.getStyleClass().add("error");
            errors.add(ValidationStatus.UNKNOWNISSUE);
        }
        if (this.extractedHasSkonto.isSelected()) {
            if (this.extractedSkonto.getText() == null || this.extractedSkonto.getText().isEmpty()) {
                this.extractedSkonto.getStyleClass().add("error");
                errors.add(ValidationStatus.UNKNOWNISSUE);
            }
        }
        if (this.extractedDeliveryDate.getText() == null || this.extractedDeliveryDate.getText().isEmpty()) {
            this.extractedDeliveryDate.getStyleClass().add("error");
            errors.add(ValidationStatus.UNKNOWNISSUE);
        }
        return errors;
    }

    private Invoice convertToInvoice() {
        Invoice result = new Invoice();
        result.setIssueDate(this.convertStringToDate(this.extractedIssueDate.getText()));
        result.setInvoiceNumber(this.extractedInvoiceNumber.getText());
        LegalPersonDao legalPersonDao = new LegalPersonDaoImpl();
        List<LegalPerson> lpList = legalPersonDao.getAll();
        if (lpList != null && lpList.size() > 0) {
            for (LegalPerson p : lpList) {
                if (p.getName() != null) {
                    if (p.getName().equals(this.extractedDebitor.getText())) {
                        result.setDebitor(p);
                    } else if (p.getName().equals(this.extractedCreditor.getText())) {
                        result.setCreditor(p);
                    }
                }
            }
        }
        if (result.getDebitor() == null) {
            result.setDebitor(new LegalPerson(this.extractedDebitor.getText()));
        }
        if (result.getCreditor() == null) {
            result.setCreditor(new LegalPerson(this.extractedCreditor.getText()));
        }
        result.setDeliveryDate(this.convertStringToDate(this.extractedDeliveryDate.getText()));
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

    private java.sql.Date convertStringToDate(String date) {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        Date d;
        try {
            d = format.parse(date);
        } catch (Exception e) {
            d = new java.sql.Date(System.currentTimeMillis());
        }
        return new java.sql.Date(d.getTime());
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
