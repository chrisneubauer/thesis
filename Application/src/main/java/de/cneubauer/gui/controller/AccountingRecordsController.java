package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountRecord;
import de.cneubauer.domain.bo.AccountType;
import de.cneubauer.domain.bo.Record;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.AccountTypeDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.domain.dao.impl.AccountTypeDaoImpl;
import de.cneubauer.domain.helper.AccountFileHelper;
import de.cneubauer.gui.model.AccountingRecordModel;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
    //@FXML public ComboBox<AccountType> fromDropDownAccountType;
    //@FXML public ComboBox<AccountType> toDropDownAccountType;
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

    private List<AccountingRecordModel> recordsFound;
    private List<AccountType> types;
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

            /*if (record.getCredit() != null) {
                model.setToPossibleAccount(record.getCredit());
                if (record.getCredit().getType() != null) {
                    model.setToPossibleType(record.getCredit().getType());
                }
            }
            if (record.getDebit() != null) {
                model.setFromPossibleAccount(record.getDebit());
                if (record.getDebit().getType() != null) {
                    model.setFromPossibleType(record.getDebit().getType());
                }
            }*/
            records.add(model);
        }
        return records;
    }

    private StringConverter<Account> createAccountConverter() {
        return new StringConverter<Account>() {
            @Override
            public String toString(Account object) {
                if (object != null) {
                    return object.getAccountNo();
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
        /*fromDropDownAccountType.setConverter(new StringConverter<AccountType>() {
            @Override
            public String toString(AccountType object) {
                return object.getName();
            }

            @Override
            public AccountType fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });*/
        fromDropDownAccountOne.setConverter(this.createAccountConverter());
        fromDropDownAccountTwo.setConverter(this.createAccountConverter());
        fromDropDownAccountThree.setConverter(this.createAccountConverter());
        fromDropDownAccountFour.setConverter(this.createAccountConverter());
        /*toDropDownAccountType.setConverter(new StringConverter<AccountType>() {
            @Override
            public String toString(AccountType object) {
                return object.getName();
            }

            @Override
            public AccountType fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });*/
        toDropDownAccountOne.setConverter(this.createAccountConverter());
        toDropDownAccountTwo.setConverter(this.createAccountConverter());
        toDropDownAccountThree.setConverter(this.createAccountConverter());
        toDropDownAccountFour.setConverter(this.createAccountConverter());

        AccountTypeDao accountTypeDao = new AccountTypeDaoImpl();
        AccountDao accountDao = new AccountDaoImpl();

        this.types = accountTypeDao.getAll();

        ObservableList<Account> accounts = FXCollections.observableArrayList(accountDao.getAll());

        Logger.getLogger(this.getClass()).log(Level.INFO, "adding " + types.size() + " elements to type dropdowns");
        Logger.getLogger(this.getClass()).log(Level.INFO, "adding " + accounts.size() + " elements to account dropdowns");
        //fromDropDownAccountType.setItems(FXCollections.observableArrayList(this.types));
        fromDropDownAccountOne.setItems(accounts);
        fromDropDownAccountTwo.setItems(accounts);
        fromDropDownAccountThree.setItems(accounts);
        fromDropDownAccountFour.setItems(accounts);
        //toDropDownAccountType.setItems(FXCollections.observableArrayList(this.types));
        toDropDownAccountOne.setItems(accounts);
        toDropDownAccountTwo.setItems(accounts);
        toDropDownAccountThree.setItems(accounts);
        toDropDownAccountFour.setItems(accounts);

        /*fromDropDownAccountType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<AccountType>() {
            @Override
            public void changed(ObservableValue<? extends AccountType> observable, AccountType oldValue, AccountType newValue) {
                Logger.getLogger(this.getClass()).log(Level.INFO, "searching for accounts of account type with id " + newValue.getId());
                fromDropDownAccount.setItems(FXCollections.observableArrayList(accountDao.getAllByType(newValue.getId())));
            }
        });
        toDropDownAccountType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<AccountType>() {
            @Override
            public void changed(ObservableValue<? extends AccountType> observable, AccountType oldValue, AccountType newValue) {
                Logger.getLogger(this.getClass()).log(Level.INFO, "searching for accounts of account type with id " + newValue.getId());
                toDropDownAccount.setItems(FXCollections.observableArrayList(accountDao.getAllByType(newValue.getId())));
            }
        });*/

        if (model.getRecord().getRecordAccounts().size() > 0) {
            int idxDebit = 0;
            int idxCredit = 0;
            Set<AccountRecord> entries = model.getRecord().getRecordAccounts();
            for (AccountRecord current : entries) {
                if (current.getIsDebit()) {
                    switch (idxDebit) {
                        case 0:
                            fromDropDownAccountOne.setValue(current.getAccount());
                            positionValueFromAccountOne.setText(String.valueOf(current.getBruttoValue()));
                            break;
                        case 1:
                            fromDropDownAccountTwo.setValue(current.getAccount());
                            positionValueFromAccountTwo.setText(String.valueOf(current.getBruttoValue()));
                            break;
                        case 2:
                            fromDropDownAccountThree.setValue(current.getAccount());
                            positionValueFromAccountThree.setText(String.valueOf(current.getBruttoValue()));
                            break;
                        case 3:
                            fromDropDownAccountFour.setValue(current.getAccount());
                            positionValueFromAccountFour.setText(String.valueOf(current.getBruttoValue()));
                            break;
                    }
                    idxDebit++;
                } else {
                    switch (idxCredit) {
                        case 0:
                            toDropDownAccountOne.setValue(current.getAccount());
                            positionValueToAccountOne.setText(String.valueOf(current.getBruttoValue()));
                            break;
                        case 1:
                            toDropDownAccountTwo.setValue(current.getAccount());
                            positionValueToAccountTwo.setText(String.valueOf(current.getBruttoValue()));
                            break;
                        case 2:
                            toDropDownAccountThree.setValue(current.getAccount());
                            positionValueToAccountThree.setText(String.valueOf(current.getBruttoValue()));
                            break;
                        case 3:
                            toDropDownAccountFour.setValue(current.getAccount());
                            positionValueToAccountFour.setText(String.valueOf(current.getBruttoValue()));
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
            /*newRecord.setCredit(getFromDropDownAccountOne());
            newRecord.setDebit(getToDropDownAccountOne());
            newRecord.setBruttoValue(getPositionValueFromAccountOne());*/
            newRecord.setEntryText(model.getPosition());
            result.add(index++, newRecord);
        }
        return result;
    }

    private void addAllListeners() {
        //this.fromDropDownAccountType.valueProperty().addListener(this.addListenerToComboBoxType(this.fromDropDownAccountType));
        this.fromDropDownAccountOne.valueProperty().addListener(this.addListenerToComboBox(this.fromDropDownAccountOne));
        this.fromDropDownAccountTwo.valueProperty().addListener(this.addListenerToComboBox(this.fromDropDownAccountTwo));
        this.fromDropDownAccountThree.valueProperty().addListener(this.addListenerToComboBox(this.fromDropDownAccountThree));
        this.fromDropDownAccountFour.valueProperty().addListener(this.addListenerToComboBox(this.fromDropDownAccountFour));
        //this.toDropDownAccountType.valueProperty().addListener(this.addListenerToComboBoxType(this.toDropDownAccountType));
        this.toDropDownAccountOne.valueProperty().addListener(this.addListenerToComboBox(this.toDropDownAccountOne));
        this.toDropDownAccountTwo.valueProperty().addListener(this.addListenerToComboBox(this.toDropDownAccountTwo));
        this.toDropDownAccountThree.valueProperty().addListener(this.addListenerToComboBox(this.toDropDownAccountThree));
        this.toDropDownAccountFour.valueProperty().addListener(this.addListenerToComboBox(this.toDropDownAccountFour));

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

    private ChangeListener<Account> addListenerToComboBox(ComboBox<Account> comboBox) {
        return (observable, oldValue, newValue) -> {
            if (newValue != null && comboBox.getItems().contains(newValue)) {
                comboBox.getStyleClass().remove("error");
            } else {
                comboBox.getStyleClass().add("error");
            }
        };
    }

    private ChangeListener<AccountType> addListenerToComboBoxType(ComboBox<AccountType> comboBox) {
        return (observable, oldValue, newValue) -> {
            if (newValue != null && this.types.contains(newValue)) {
                comboBox.getStyleClass().remove("error");
            } else {
                comboBox.getStyleClass().add("error");
            }
        };
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

    void addRevisedToFile() {
        // check if all records have been revised before saving
        if (this.validateAccountingRecords()) {
            for (AccountingRecordModel acc : this.recordsFound) {
                if (acc.isRevised()) {
                    AccountFileHelper.write(acc.getRecord().getEntryText(), acc.getFromPossibleAccount().getAccountNo());
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
        if (this.getFromDropDownAccountOne() != null)
        {
            //currentRecord.setCredit(this.getFromDropDownAccountOne());
        }

        /*if (this.getFromDropDownAccountType() != null) {
            currentRecord.getCredit().setType(this.getFromDropDownAccountType());
        }*/

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

        /*if (this.getFromDropDownAccountType() != null) {
            currentRecord.getCredit().setType(this.getFromDropDownAccountType());
        }*/
    }

    // core method to update the whole view when new information is present
    private void updateAccountingRecordView(AccountingRecordModel currentModel) {
        Record record = currentModel.getRecord();
        /*if (record.getCredit() != null) {
             if (record.getCredit().getType() != null) {
                this.setFromDropDownAccountType(record.getCredit().getType());
            }
            this.setFromDropDownAccountOne(record.getCredit());
        }

        if (record.getDebit() != null) {
            if (record.getDebit().getType() != null) {
                this.setToDropDownAccountType(record.getDebit().getType());
            }
            this.setToDropDownAccountOne(record.getDebit());
        }

        if (record.getBruttoValue() > 0) {
            this.setPositionValueFromAccountOne(record.getBruttoValue());
        }*/

        if (record.getEntryText() != null) {
            this.setPossiblePosition(record.getEntryText());
        }

        Logger.getLogger(this.getClass()).log(Level.INFO, "current confidence: " + currentModel.getConfidence());
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

    /*private AccountType getFromDropDownAccountType() {
        return fromDropDownAccountType.getValue();
    }*/

    /*private void setFromDropDownAccountType(AccountType fromDropDownAccountType) {
        this.fromDropDownAccountType.getSelectionModel().select(fromDropDownAccountType);
    }*/

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

   /* public AccountType getToDropDownAccountType() {
        return toDropDownAccountType.getValue();
    }

    public void setToDropDownAccountType(AccountType toDropDownAccountType) {
        this.toDropDownAccountType.getSelectionModel().select(toDropDownAccountType);
    }*/

    private Account getToDropDownAccountOne() {
        return toDropDownAccountOne.getValue();
    }

    public void setToDropDownAccountOne(Account toDropDownAccount) {
        this.toDropDownAccountOne.getSelectionModel().select(toDropDownAccount);
    }

    private Account getToDropDownAccountTwo() {
        return toDropDownAccountTwo.getValue();
    }

    public void setToDropDownAccountTwo(Account toDropDownAccount) {
        this.toDropDownAccountTwo.getSelectionModel().select(toDropDownAccount);
    }

    private Account getToDropDownAccountThree() {
        return toDropDownAccountThree.getValue();
    }

    public void setToDropDownAccountThree(Account toDropDownAccount) {
        this.toDropDownAccountThree.getSelectionModel().select(toDropDownAccount);
    }

    private Account getToDropDownAccountFour() {
        return toDropDownAccountFour.getValue();
    }

    public void setToDropDownAccountFour(Account toDropDownAccount) {
        this.toDropDownAccountFour.getSelectionModel().select(toDropDownAccount);
    }

    public ImageView getConfidenceImage() {
        return confidenceImage;
    }

    public void setConfidenceImage(ImageView confidenceImage) {
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

    // checks if accounting record can be set as revised
    public boolean checkRevised(ActionEvent actionEvent) {
        if (this.recordRevised.isSelected()) {
            AccountingRecordModel model = this.getRecordsFound().get(Integer.valueOf(this.getCurrentRecord()));
            Record record = model.getRecord();

            //if (record.getCredit() != null && record.getDebit() != null && record.getBruttoValue() > 0) {
                model.setRevised(true);
                return  true;
            /*} else {
                return false;
            }*/
        } else {
            AccountingRecordModel model = this.getRecordsFound().get(Integer.valueOf(this.getCurrentRecord()));
            model.setRevised(false);
            return false;
        }
    }

    public String getPossiblePosition() {
        return possiblePosition.getText();
    }

    public void setPossiblePosition(String possiblePosition) {
        this.possiblePosition.setText(possiblePosition);
    }

    // when called, invoice has been reviewed by the user
    // set invoice to be reviewed and update all information given
    public void setReviewed(ActionEvent actionEvent) {
        superCtrl.reviseAll();
    }

    boolean validateFieldsBeforeSave() {
        boolean result = true;

        /*if (this.fromDropDownAccountType.getSelectionModel().getSelectedItem() == null) {
            this.fromDropDownAccountType.getStyleClass().add("error");
            result = false;
        }*/
        if (this.fromDropDownAccountOne.getSelectionModel().getSelectedItem() == null) {
            this.fromDropDownAccountOne.getStyleClass().add("error");
            result = false;
        }
        /*if (this.toDropDownAccountType.getSelectionModel().getSelectedItem() == null) {
            this.toDropDownAccountType.getStyleClass().add("error");
            result = false;
        }*/
        if (this.toDropDownAccountOne.getSelectionModel().getSelectedItem() == null) {
            this.toDropDownAccountOne.getStyleClass().add("error");
            result = false;
        }
        if (this.positionValueFromAccountOne.getText() == null || this.positionValueFromAccountOne.getText().isEmpty()) {
            this.positionValueFromAccountOne.getStyleClass().add("error");
            result = false;
        }
        if (this.possiblePosition.getText() == null || this.possiblePosition.getText().isEmpty()) {
            this.possiblePosition.getStyleClass().add("error");
            result = false;
        }
        return result;
    }

    List<Record> updateInformation() {
        return this.convertToAccountingRecords();
    }

}
