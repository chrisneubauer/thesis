package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountRecord;
import de.cneubauer.domain.bo.Record;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.domain.helper.AccountFileHelper;
import de.cneubauer.gui.model.AccountingRecordModel;
import de.cneubauer.gui.util.AutoCompleteComboBoxListener;
import de.cneubauer.util.enumeration.ValidationStatus;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 * Controller for managing extracted accounting records
 */
public class AccountingRecordsController extends SplitPaneController {
    @FXML public ComboBox<Account> fromDropDownAccountOne;
    @FXML public ComboBox<Account> fromDropDownAccountTwo;
    @FXML public ComboBox<Account> fromDropDownAccountThree;
    @FXML public ComboBox<Account> fromDropDownAccountFour;
    @FXML public ComboBox<Account> toDropDownAccountOne;
    @FXML public ComboBox<Account> toDropDownAccountTwo;
    @FXML public ComboBox<Account> toDropDownAccountThree;
    @FXML public ComboBox<Account> toDropDownAccountFour;

    @FXML public TextField positionValueFromAccountOne;
    @FXML public TextField positionValueFromAccountTwo;
    @FXML public TextField positionValueFromAccountThree;
    @FXML public TextField positionValueFromAccountFour;
    @FXML public TextField positionValueToAccountOne;
    @FXML public TextField positionValueToAccountTwo;
    @FXML public TextField positionValueToAccountThree;
    @FXML public TextField positionValueToAccountFour;

    @FXML public ImageView confidenceImage;
    @FXML public CheckBox recordRevised;
    @FXML public Label currentRecord;
    @FXML public Button SaveAccountingRecords;
    @FXML public TextField possiblePosition;

    @FXML public Button addEntryButton;
    @FXML public Button deleteCurrentEntryButton;

    private List<AccountingRecordModel> recordsFound;
    private SplitPaneController superCtrl;
    private int index = 1;

    void initData(List<Record> data, SplitPaneController superCtrl) {
        this.superCtrl = superCtrl;
        Logger.getLogger(this.getClass()).log(Level.INFO, "initiating AccountingRecordsController data");
        List<AccountingRecordModel> records = this.convertToAccountingRecordModel(data);
        this.setRecordsFound(records);
        this.possiblePosition.getScene().getStylesheets().add(String.valueOf(getClass().getResource("../../../../css/validationError.css")));
        this.addAllListeners();

        Logger.getLogger(this.getClass()).log(Level.INFO, records.size() + " records found!");

        if (records.size() > 0) {
            this.initiateDropdowns(records.get(0));
            this.updateAccountingRecordView(records.get(0));
        }
    }

    private List<AccountingRecordModel> convertToAccountingRecordModel(List<Record> data) {
        List<AccountingRecordModel> records = new ArrayList<>(data.size());
        int idx = 1;
        for (Record record : data) {
            AccountingRecordModel model = new AccountingRecordModel(idx++);
            model.setRevised(false);
            model.setRecord(record);
            if (record.getEntryText() != null) {
                model.setPosition(record.getEntryText());
            }

            records.add(model);
        }
        return records;
    }

    private StringConverter<Account> createAccountConverter() {
        return new StringConverter<Account>() {
            @Override
            public String toString(Account object) {
                if (object != null) {
                    return object.getAccountNo() + " - " + object.getName();
                } else {
                    return "";
                }
            }

            @Override
            public Account fromString(String string) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private void initiateDropdowns(AccountingRecordModel model) {
        fromDropDownAccountOne.setConverter(this.createAccountConverter());
        fromDropDownAccountTwo.setConverter(this.createAccountConverter());
        fromDropDownAccountThree.setConverter(this.createAccountConverter());
        fromDropDownAccountFour.setConverter(this.createAccountConverter());

        toDropDownAccountOne.setConverter(this.createAccountConverter());
        toDropDownAccountTwo.setConverter(this.createAccountConverter());
        toDropDownAccountThree.setConverter(this.createAccountConverter());
        toDropDownAccountFour.setConverter(this.createAccountConverter());

        AccountDao accountDao = new AccountDaoImpl();
        ObservableList<Account> accounts = FXCollections.observableArrayList(accountDao.getAll());

        Logger.getLogger(this.getClass()).log(Level.INFO, "adding " + accounts.size() + " elements to account dropdowns");

        fromDropDownAccountOne.setItems(accounts);
        fromDropDownAccountTwo.setItems(accounts);
        fromDropDownAccountThree.setItems(accounts);
        fromDropDownAccountFour.setItems(accounts);
        toDropDownAccountOne.setItems(accounts);
        toDropDownAccountTwo.setItems(accounts);
        toDropDownAccountThree.setItems(accounts);
        toDropDownAccountFour.setItems(accounts);

        if (model.getRecord().getRecordAccounts().size() > 0) {
            int idxDebit = 0;
            int idxCredit = 0;
            Set<AccountRecord> entries = model.getRecord().getRecordAccounts();
            for (AccountRecord current : entries) {
                if (current.getIsDebit()) {
                    switch (idxDebit) {
                        case 0:
                            this.setFromDropDownAccountOne(current.getAccount());
                            this.setPositionValueFromAccountOne(current.getBruttoValue());
                            break;
                        case 1:
                            this.setFromDropDownAccountTwo(current.getAccount());
                            this.setPositionValueFromAccountTwo(current.getBruttoValue());
                            break;
                        case 2:
                            this.setFromDropDownAccountThree(current.getAccount());
                            this.setPositionValueFromAccountThree(current.getBruttoValue());
                            break;
                        case 3:
                            this.setFromDropDownAccountFour(current.getAccount());
                            this.setPositionValueFromAccountFour(current.getBruttoValue());
                            break;
                    }
                    idxDebit++;
                } else {
                    switch (idxCredit) {
                        case 0:
                            this.setToDropDownAccountOne(current.getAccount());
                            this.setPositionValueToAccountOne(current.getBruttoValue());
                            break;
                        case 1:
                            this.setToDropDownAccountTwo(current.getAccount());
                            this.setPositionValueToAccountTwo(current.getBruttoValue());
                            break;
                        case 2:
                            this.setToDropDownAccountThree(current.getAccount());
                            this.setPositionValueToAccountThree(current.getBruttoValue());
                            break;
                        case 3:
                            this.setToDropDownAccountFour(current.getAccount());
                            this.setPositionValueToAccountFour(current.getBruttoValue());
                            break;
                    }
                    idxCredit++;
                }
            }
        }
    }

    private List<Record> convertToAccountingRecords() {
        List<Record> result = new ArrayList<>(this.recordsFound.size());
        int index = 0;
        for (AccountingRecordModel model : recordsFound) {
            Record newRecord = new Record();
            newRecord.setEntryText(model.getPosition());
            result.add(index++, newRecord);
        }
        return result;
    }

    private void addAllListeners() {
        this.addAutoCompleteListener(this.fromDropDownAccountOne);
        this.addAutoCompleteListener(this.fromDropDownAccountTwo);
        this.addAutoCompleteListener(this.fromDropDownAccountThree);
        this.addAutoCompleteListener(this.fromDropDownAccountFour);
        //this.fromDropDownAccountOne.valueProperty().addListener(this.addListenerToComboBox(this.fromDropDownAccountOne));
        //this.fromDropDownAccountTwo.valueProperty().addListener(this.addListenerToComboBox(this.fromDropDownAccountTwo));
        //this.fromDropDownAccountThree.valueProperty().addListener(this.addListenerToComboBox(this.fromDropDownAccountThree));
        //this.fromDropDownAccountFour.valueProperty().addListener(this.addListenerToComboBox(this.fromDropDownAccountFour));
        this.addAutoCompleteListener(this.toDropDownAccountOne);
        this.addAutoCompleteListener(this.toDropDownAccountTwo);
        this.addAutoCompleteListener(this.toDropDownAccountThree);
        this.addAutoCompleteListener(this.toDropDownAccountFour);
        /*this.toDropDownAccountOne.valueProperty().addListener(this.addListenerToComboBox(this.toDropDownAccountOne));
        this.toDropDownAccountTwo.valueProperty().addListener(this.addListenerToComboBox(this.toDropDownAccountTwo));
        this.toDropDownAccountThree.valueProperty().addListener(this.addListenerToComboBox(this.toDropDownAccountThree));
        this.toDropDownAccountFour.valueProperty().addListener(this.addListenerToComboBox(this.toDropDownAccountFour));*/

        this.positionValueFromAccountOne.textProperty().addListener(this.addListenerToTextField(this.positionValueFromAccountOne));
        this.positionValueFromAccountTwo.textProperty().addListener(this.addListenerToTextField(this.positionValueFromAccountTwo));
        this.positionValueFromAccountThree.textProperty().addListener(this.addListenerToTextField(this.positionValueFromAccountThree));
        this.positionValueFromAccountFour.textProperty().addListener(this.addListenerToTextField(this.positionValueFromAccountFour));
        this.positionValueToAccountOne.textProperty().addListener(this.addListenerToTextField(this.positionValueToAccountOne));
        this.positionValueToAccountTwo.textProperty().addListener(this.addListenerToTextField(this.positionValueToAccountTwo));
        this.positionValueToAccountThree.textProperty().addListener(this.addListenerToTextField(this.positionValueToAccountThree));
        this.positionValueToAccountFour.textProperty().addListener(this.addListenerToTextField(this.positionValueToAccountFour));
        this.possiblePosition.textProperty().addListener(this.addListenerToTextField(this.possiblePosition));
        Logger.getLogger(this.getClass()).log(Level.INFO, "Listeners added to textfields");
    }

    private AutoCompleteComboBoxListener addAutoCompleteListener(ComboBox<Account> comboBox) {
        return new AutoCompleteComboBoxListener(comboBox);
    }

    /*private ChangeListener<Account> addListenerToComboBox(ComboBox<Account> comboBox) {
        return (observable, oldValue, newValue) -> {
            if (newValue != null && comboBox.getItems().contains(newValue)) {
                comboBox.getStyleClass().remove("error");
            } else {
                comboBox.getStyleClass().add("error");
            }
        };
    }*/

    private ChangeListener<String> addListenerToTextField(TextField field) {
        return (observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 0) {
                field.getStyleClass().remove("error");
            } else {
                field.getStyleClass().add("error");
            }
        };
    }

    void addRevisedToFile() {
        // check if all records have been revised before saving
        if (this.validateAccountingRecords()) {
            for (AccountingRecordModel acc : this.recordsFound) {
                if (acc.isRevised()) {
                    AccountFileHelper.addAccountingRecordModel(acc);
                }
            }
        }
    }

    private boolean validateAccountingRecords() {
        for (AccountingRecordModel record : this.getRecordsFound()) {
            if (!record.isRevised()) {
                return false;
            }
        }
        return true;
    }

    public void nextRecord() {
        if (this.getRecordsFound() != null && this.getRecordsFound().size() > this.index) {
            this.saveCurrentValuesToRecord();
            this.index++;
            this.setCurrentRecord(String.valueOf(this.index));
            this.updateAccountingRecordView(this.getRecordsFound().get(this.index-1));
        }
    }

    public void prevRecord() {
        if (this.getRecordsFound() != null && this.index > 1) {
            this.saveCurrentValuesToRecord();
            this.index--;
            this.setCurrentRecord(String.valueOf(this.index));
            this.updateAccountingRecordView(this.getRecordsFound().get(this.index-1));
        }
    }

    private void saveCurrentValuesToRecord() {
        AccountingRecordModel current = this.getRecordsFound().get(this.index-1);
        Record currentRecord = current.getRecord();
        if (this.getPossiblePosition() != null) {
            currentRecord.setEntryText(this.getPossiblePosition());
        }

        currentRecord.getRecordAccounts().clear();

        if (this.getToDropDownAccountOne() != null && this.getPositionValueToAccountOne() > 0) {
            AccountRecord record = new AccountRecord();
            record.setAccount(this.getToDropDownAccountOne());
            record.setBruttoValue(this.getPositionValueToAccountOne());
            record.setIsDebit(false);
            currentRecord.getRecordAccounts().add(record);
        }

        if (this.getToDropDownAccountTwo() != null && this.getPositionValueToAccountTwo() > 0) {
            AccountRecord record = new AccountRecord();
            record.setAccount(this.getToDropDownAccountTwo());
            record.setBruttoValue(this.getPositionValueToAccountTwo());
            record.setIsDebit(false);
            currentRecord.getRecordAccounts().add(record);
        }

        if (this.getToDropDownAccountThree() != null && this.getPositionValueToAccountThree() > 0) {
            AccountRecord record = new AccountRecord();
            record.setAccount(this.getToDropDownAccountThree());
            record.setBruttoValue(this.getPositionValueToAccountThree());
            record.setIsDebit(false);
            currentRecord.getRecordAccounts().add(record);
        }

        if (this.getToDropDownAccountFour() != null && this.getPositionValueToAccountFour() > 0) {
            AccountRecord record = new AccountRecord();
            record.setAccount(this.getToDropDownAccountFour());
            record.setBruttoValue(this.getPositionValueToAccountFour());
            record.setIsDebit(false);
            currentRecord.getRecordAccounts().add(record);
        }

        if (this.getFromDropDownAccountOne() != null && this.getPositionValueFromAccountOne() > 0) {
            AccountRecord record = new AccountRecord();
            record.setAccount(this.getFromDropDownAccountOne());
            record.setBruttoValue(this.getPositionValueFromAccountOne());
            record.setIsDebit(true);
            currentRecord.getRecordAccounts().add(record);
        }

        if (this.getFromDropDownAccountTwo() != null && this.getPositionValueFromAccountTwo() > 0) {
            AccountRecord record = new AccountRecord();
            record.setAccount(this.getFromDropDownAccountTwo());
            record.setBruttoValue(this.getPositionValueFromAccountTwo());
            record.setIsDebit(true);
            currentRecord.getRecordAccounts().add(record);
        }

        if (this.getFromDropDownAccountThree() != null && this.getPositionValueFromAccountThree() > 0) {
            AccountRecord record = new AccountRecord();
            record.setAccount(this.getFromDropDownAccountThree());
            record.setBruttoValue(this.getPositionValueFromAccountThree());
            record.setIsDebit(true);
            currentRecord.getRecordAccounts().add(record);
        }

        if (this.getFromDropDownAccountFour() != null && this.getPositionValueFromAccountFour() > 0) {
            AccountRecord record = new AccountRecord();
            record.setAccount(this.getFromDropDownAccountFour());
            record.setBruttoValue(this.getPositionValueFromAccountFour());
            record.setIsDebit(true);
            currentRecord.getRecordAccounts().add(record);
        }
    }

    // core method to update the whole view when new information is present
    private void updateAccountingRecordView(AccountingRecordModel currentModel) {
        Record record = currentModel.getRecord();
        // TODO: revise this method

        if (record.getEntryText() != null) {
            this.setPossiblePosition(record.getEntryText());
        }

        Logger.getLogger(this.getClass()).log(Level.INFO, "current confidence: " + currentModel.getConfidence());

        ImageView view;
        if (currentModel.getConfidence() < 0.5) {
            view = new ImageView("img/Circle_Red.png");
        } else if (currentModel.getConfidence() < 0.8 && currentModel.getConfidence() >= 0.5) {
            view = new ImageView("img/Circle_Yellow.png");
        } else {
            view = new ImageView("img/Circle_Green.png");
        }
        view.setFitHeight(32);
        view.setFitWidth(32);
        this.setConfidenceImage(view);
    }

    private String getCurrentRecord() {
        return this.currentRecord.getText();
    }

    private void setCurrentRecord(String currentRecord) {
        this.currentRecord.setText(currentRecord);
    }

    private List<AccountingRecordModel> getRecordsFound() {
        return recordsFound;
    }

    private void setRecordsFound(List<AccountingRecordModel> recordsFound) {
        this.recordsFound = recordsFound;
    }

    private Account getFromDropDownAccountOne() {
        return fromDropDownAccountOne.getValue();
    }

    private void setFromDropDownAccountOne(Account fromDropDownAccount) {
        this.fromDropDownAccountOne.getSelectionModel().select(fromDropDownAccount);
    }

    private Account getFromDropDownAccountTwo() {
        return fromDropDownAccountTwo.getValue();
    }

    private void setFromDropDownAccountTwo(Account fromDropDownAccount) {
        this.fromDropDownAccountTwo.getSelectionModel().select(fromDropDownAccount);
    }

    private Account getFromDropDownAccountThree() {
        return fromDropDownAccountThree.getValue();
    }

    private void setFromDropDownAccountThree(Account fromDropDownAccount) {
        this.fromDropDownAccountThree.getSelectionModel().select(fromDropDownAccount);
    }

    private Account getFromDropDownAccountFour() {
        return fromDropDownAccountFour.getValue();
    }

    private void setFromDropDownAccountFour(Account fromDropDownAccount) {
        this.fromDropDownAccountFour.getSelectionModel().select(fromDropDownAccount);
    }

    private Account getToDropDownAccountOne() {
        return toDropDownAccountOne.getValue();
    }

    private void setToDropDownAccountOne(Account toDropDownAccount) {
        this.toDropDownAccountOne.getSelectionModel().select(toDropDownAccount);
    }

    private Account getToDropDownAccountTwo() {
        return toDropDownAccountTwo.getValue();
    }

    private void setToDropDownAccountTwo(Account toDropDownAccount) {
        this.toDropDownAccountTwo.getSelectionModel().select(toDropDownAccount);
    }

    private Account getToDropDownAccountThree() {
        return toDropDownAccountThree.getValue();
    }

    private void setToDropDownAccountThree(Account toDropDownAccount) {
        this.toDropDownAccountThree.getSelectionModel().select(toDropDownAccount);
    }

    private Account getToDropDownAccountFour() {
        return toDropDownAccountFour.getValue();
    }

    private void setToDropDownAccountFour(Account toDropDownAccount) {
        this.toDropDownAccountFour.getSelectionModel().select(toDropDownAccount);
    }

    private void setConfidenceImage(ImageView confidenceImage) {
        this.confidenceImage = confidenceImage;
    }

    private double getPositionValueFromAccountOne() {
        if (this.positionValueFromAccountOne.getText() != null) {
            try {
                return Double.valueOf(this.positionValueFromAccountOne.getText());
            } catch (NumberFormatException ex) {
                Logger.getLogger(this.getClass()).log(Level.WARN, "Unable to parse value! Returning 0");
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void setPositionValueFromAccountOne(double newValue) {
        this.positionValueFromAccountOne.setText(String.valueOf(newValue));
    }

    private double getPositionValueFromAccountTwo() {
        if (this.positionValueFromAccountTwo.getText() != null) {
            try {
                return Double.valueOf(this.positionValueFromAccountTwo.getText());
            } catch (NumberFormatException ex) {
                Logger.getLogger(this.getClass()).log(Level.WARN, "Unable to parse value! Returning 0");
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void setPositionValueFromAccountTwo(double newValue) {
        this.positionValueFromAccountTwo.setText(String.valueOf(newValue));
    }

    private double getPositionValueFromAccountThree() {
        if (this.positionValueFromAccountThree.getText() != null) {
            try {
                return Double.valueOf(this.positionValueFromAccountThree.getText());
            } catch (NumberFormatException ex) {
                Logger.getLogger(this.getClass()).log(Level.WARN, "Unable to parse value! Returning 0");
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void setPositionValueFromAccountThree(double newValue) {
        this.positionValueFromAccountThree.setText(String.valueOf(newValue));
    }

    private double getPositionValueFromAccountFour() {
        if (this.positionValueFromAccountFour.getText() != null) {
            try {
                return Double.valueOf(this.positionValueFromAccountFour.getText());
            } catch (NumberFormatException ex) {
                Logger.getLogger(this.getClass()).log(Level.WARN, "Unable to parse value! Returning 0");
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void setPositionValueFromAccountFour(double newValue) {
        this.positionValueFromAccountFour.setText(String.valueOf(newValue));
    }

    private double getPositionValueToAccountOne() {
        if (this.positionValueToAccountOne.getText() != null) {
            try {
                return Double.valueOf(this.positionValueToAccountOne.getText());
            } catch (NumberFormatException ex) {
                Logger.getLogger(this.getClass()).log(Level.WARN, "Unable to parse value! Returning 0");
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void setPositionValueToAccountOne(double newValue) {
        this.positionValueToAccountOne.setText(String.valueOf(newValue));
    }

    private double getPositionValueToAccountTwo() {
        if (this.positionValueToAccountTwo.getText() != null) {
            try {
                return Double.valueOf(this.positionValueToAccountTwo.getText());
            } catch (NumberFormatException ex) {
                Logger.getLogger(this.getClass()).log(Level.WARN, "Unable to parse value! Returning 0");
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void setPositionValueToAccountTwo(double newValue) {
        this.positionValueToAccountTwo.setText(String.valueOf(newValue));
    }

    private double getPositionValueToAccountThree() {
        if (this.positionValueToAccountThree.getText() != null) {
            try {
                return Double.valueOf(this.positionValueToAccountThree.getText());
            } catch (NumberFormatException ex) {
                Logger.getLogger(this.getClass()).log(Level.WARN, "Unable to parse value! Returning 0");
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void setPositionValueToAccountThree(double newValue) {
        this.positionValueToAccountThree.setText(String.valueOf(newValue));
    }

    private double getPositionValueToAccountFour() {
        if (this.positionValueToAccountFour.getText() != null) {
            try {
                return Double.valueOf(this.positionValueToAccountFour.getText());
            } catch (NumberFormatException ex) {
                Logger.getLogger(this.getClass()).log(Level.WARN, "Unable to parse value! Returning 0");
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void setPositionValueToAccountFour(double newValue) {
        this.positionValueToAccountFour.setText(String.valueOf(newValue));
    }

    private String getPossiblePosition() {
        return possiblePosition.getText();
    }

    private void setPossiblePosition(String possiblePosition) {
        this.possiblePosition.setText(possiblePosition);
    }

    // when called, invoice has been reviewed by the user
    // set invoice to be reviewed and update all information given
    public void setReviewed() {
        this.saveCurrentValuesToRecord();
        superCtrl.reviseAll();
    }

    List<ValidationStatus> validateFieldsBeforeSave() {
        List<ValidationStatus> errors = new LinkedList<>();
        for (AccountingRecordModel record : this.getRecordsFound()) {
            Record current = record.getRecord();
            if (current.getEntryText() == null || Objects.equals(current.getEntryText(), "")) {
                errors.add(ValidationStatus.MISSINGPOSITION);
            }
            double debitSum = 0;
            double creditSum = 0;
            for (AccountRecord entry : current.getRecordAccounts()) {

                if (entry.getIsDebit()) {
                    debitSum += entry.getBruttoValue();
                } else {
                    creditSum += entry.getBruttoValue();
                }

                if (entry.getAccount() == null) {
                    errors.add(ValidationStatus.MISSINGACCOUNTS);
                }
                if (entry.getBruttoValue() == 0) {
                    errors.add(ValidationStatus.MISSINGVALUES);
                }
            }
            if (debitSum != creditSum) {
                errors.add(ValidationStatus.MALFORMEDVALUE);
            }
        }

        return errors;
    }

    List<Record> updateInformation() {
        return this.convertToAccountingRecords();
    }

    public void addNewEntry() {
        AccountingRecordModel model = new AccountingRecordModel(this.getRecordsFound().size() + 1);
        model.setRecord(new Record());
        this.getRecordsFound().add(model);
    }

    public void deleteCurrentEntry() {
        this.prevRecord();
        this.getRecordsFound().remove(this.index);
    }
}
