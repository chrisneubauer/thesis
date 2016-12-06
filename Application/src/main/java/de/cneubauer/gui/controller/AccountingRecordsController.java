package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountType;
import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.AccountTypeDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.domain.dao.impl.AccountTypeDaoImpl;
import de.cneubauer.domain.helper.AccountFileHelper;
import de.cneubauer.gui.model.AccountingRecordModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 * Controller for managing extracted accounting records
 */
public class AccountingRecordsController extends SplitPaneController {
    @FXML public ComboBox<AccountType> fromDropDownAccountType;
    @FXML public ComboBox<Account> fromDropDownAccount;
    @FXML public ComboBox<AccountType> toDropDownAccountType;
    @FXML public ComboBox<Account> toDropDownAccount;
    @FXML public TextField positionValue;
    @FXML public ImageView confidenceImage;
    @FXML public CheckBox recordRevised;
    @FXML public Label currentRecord;
    @FXML public Button SaveAccountingRecords;
    @FXML public TextField possiblePosition;

    private List<AccountingRecordModel> recordsFound;
    private List<AccountType> types;
    private SplitPaneController superCtrl;
    private int index = 1;

    void initData(List<AccountingRecord> data, SplitPaneController superCtrl) {
        this.superCtrl = superCtrl;
        Logger.getLogger(this.getClass()).log(Level.INFO, "initiating AccountingRecordsController data");
        List<AccountingRecordModel> records = this.convertToAccountingRecordModel(data);
        this.setRecordsFound(records);
        this.possiblePosition.getScene().getStylesheets().add(String.valueOf(getClass().getResource("../../../../css/validationError.css")));

        Logger.getLogger(this.getClass()).log(Level.INFO, records.size() + " records found!");

        if (records.size() > 0) {
            this.initiateDropdowns(records.get(0));
            this.updateAccountingRecordView(records.get(0));
        }
    }

    private List<AccountingRecordModel> convertToAccountingRecordModel(List<AccountingRecord> data) {
        List<AccountingRecordModel> records = new ArrayList<>(data.size());
        int idx = 1;
        for (AccountingRecord record : data) {
            AccountingRecordModel model = new AccountingRecordModel(idx++);
            model.setRevised(false);
            model.setRecord(record);
            if (record.getEntryText() != null) {
                model.setPosition(record.getEntryText());
            }

            if (record.getCredit() != null) {
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
            }
            records.add(model);
        }
        return records;
    }

    private void initiateDropdowns(AccountingRecordModel model) {
        fromDropDownAccountType.setConverter(new StringConverter<AccountType>() {
            @Override
            public String toString(AccountType object) {
                return object.getName();
            }

            @Override
            public AccountType fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
        fromDropDownAccount.setConverter(new StringConverter<Account>() {
            @Override
            public String toString(Account object) {
                return object.getAccountNo();
            }

            @Override
            public Account fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
        toDropDownAccountType.setConverter(new StringConverter<AccountType>() {
            @Override
            public String toString(AccountType object) {
                return object.getName();
            }

            @Override
            public AccountType fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });
        toDropDownAccount.setConverter(new StringConverter<Account>() {
            @Override
            public String toString(Account object) {
                return object.getAccountNo();
            }

            @Override
            public Account fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });

        AccountTypeDao accountTypeDao = new AccountTypeDaoImpl();
        AccountDao accountDao = new AccountDaoImpl();

        this.types = accountTypeDao.getAll();

        ObservableList<Account> accounts = FXCollections.observableArrayList(accountDao.getAll());

        Logger.getLogger(this.getClass()).log(Level.INFO, "adding " + types.size() + " elements to type dropdowns");
        Logger.getLogger(this.getClass()).log(Level.INFO, "adding " + accounts.size() + " elements to account dropdowns");
        fromDropDownAccountType.setItems(FXCollections.observableArrayList(this.types));
        fromDropDownAccount.setItems(accounts);
        toDropDownAccountType.setItems(FXCollections.observableArrayList(this.types));
        toDropDownAccount.setItems(accounts);

        fromDropDownAccountType.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<AccountType>() {
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
        });
    }

    private List<AccountingRecord> convertToAccountingRecords() {
        List<AccountingRecord> result = new ArrayList<>(this.recordsFound.size());
        int index = 0;
        for (AccountingRecordModel model : recordsFound) {
            AccountingRecord newRecord = new AccountingRecord();
            newRecord.setCredit(getFromDropDownAccount());
            newRecord.setDebit(getToDropDownAccount());
            newRecord.setBruttoValue(getPositionValue());
            newRecord.setEntryText(model.getPosition());
            result.add(index++, newRecord);
        }
        return result;
    }

    // TODO: Necessary for validation
    private void addAllListeners() {
        /*this.fromDropDownAccountType.textProperty().addListener(this.addListenerToTextField(this.extractedInvoiceNumber));
        this.fromDropDownAccount.textProperty().addListener(this.addListenerToTextField(this.extractedIssueDate));
        this.toDropDownAccountType.textProperty().addListener(this.addListenerToTextField(this.extractedCreditor));
        this.toDropDownAccount.textProperty().addListener(this.addListenerToTextField(this.extractedDebitor));
        this.positionValue.textProperty().addListener(this.addListenerToTextField(this.extractedLineTotal));
        Logger.getLogger(this.getClass()).log(Level.INFO, "Listeners added to textfields");*/
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
        AccountingRecord currentRecord = current.getRecord();
        if (this.getFromDropDownAccount() != null)
        {
            currentRecord.setCredit(this.getFromDropDownAccount());
        }

        if (this.getFromDropDownAccountType() != null) {
            currentRecord.getCredit().setType(this.getFromDropDownAccountType());
        }

        if (this.getToDropDownAccount() != null) {
            currentRecord.setDebit(this.getToDropDownAccount());
        }

        if (this.getFromDropDownAccountType() != null) {
            currentRecord.getCredit().setType(this.getFromDropDownAccountType());
        }

        if (this.getPositionValue() > 0) {
            currentRecord.setBruttoValue(this.getPositionValue());
        }
    }

    // core method to update the whole view when new information is present
    private void updateAccountingRecordView(AccountingRecordModel currentModel) {
        AccountingRecord accountingRecord = currentModel.getRecord();
        if (accountingRecord.getCredit() != null) {
            if (accountingRecord.getCredit().getType() != null) {
                this.setFromDropDownAccountType(accountingRecord.getCredit().getType());
            }
            this.setFromDropDownAccount(accountingRecord.getCredit());
        }

        if (accountingRecord.getDebit() != null) {
            if (accountingRecord.getDebit().getType() != null) {
                this.setToDropDownAccountType(accountingRecord.getDebit().getType());
            }
            this.setToDropDownAccount(accountingRecord.getDebit());
        }

        if (accountingRecord.getBruttoValue() > 0) {
            this.setPositionValue(accountingRecord.getBruttoValue());
        }

        if (accountingRecord.getEntryText() != null) {
            this.setPossiblePosition(accountingRecord.getEntryText());
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

    private AccountType getFromDropDownAccountType() {
        return fromDropDownAccountType.getValue();
    }

    private void setFromDropDownAccountType(AccountType fromDropDownAccountType) {
        this.fromDropDownAccountType.getSelectionModel().select(fromDropDownAccountType);
    }

    private Account getFromDropDownAccount() {
        return fromDropDownAccount.getValue();
    }

    private void setFromDropDownAccount(Account fromDropDownAccount) {
        this.fromDropDownAccount.getSelectionModel().select(fromDropDownAccount);
    }

    public AccountType getToDropDownAccountType() {
        return toDropDownAccountType.getValue();
    }

    public void setToDropDownAccountType(AccountType toDropDownAccountType) {
        this.toDropDownAccountType.getSelectionModel().select(toDropDownAccountType);
    }

    private Account getToDropDownAccount() {
        return toDropDownAccount.getValue();
    }

    public void setToDropDownAccount(Account toDropDownAccount) {
        this.toDropDownAccount.getSelectionModel().select(toDropDownAccount);
    }

    public ImageView getConfidenceImage() {
        return confidenceImage;
    }

    public void setConfidenceImage(ImageView confidenceImage) {
        this.confidenceImage = confidenceImage;
    }

    private double getPositionValue() {
        if (this.positionValue.getText() != null) {
            try {
                return Double.valueOf(this.positionValue.getText());
            } catch (NumberFormatException ex) {
                Logger.getLogger(this.getClass()).log(Level.WARN, "Unable to parse value! Returning 0");
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void setPositionValue(double newValue) {
        this.positionValue.setText(String.valueOf(newValue));
    }

    // checks if accounting record can be set as revised
    public boolean checkRevised(ActionEvent actionEvent) {
        if (this.recordRevised.isSelected()) {
            AccountingRecordModel model = this.getRecordsFound().get(Integer.valueOf(this.getCurrentRecord()));
            AccountingRecord record = model.getRecord();

            if (record.getCredit() != null && record.getDebit() != null && record.getBruttoValue() > 0) {
                model.setRevised(true);
                return  true;
            } else {
                return false;
            }
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

        if (this.fromDropDownAccountType.getSelectionModel().getSelectedItem() == null) {
            this.fromDropDownAccountType.getStyleClass().add("error");
            result = false;
        }
        if (this.fromDropDownAccount.getSelectionModel().getSelectedItem() == null) {
            this.fromDropDownAccount.getStyleClass().add("error");
            result = false;
        }
        if (this.toDropDownAccountType.getSelectionModel().getSelectedItem() == null) {
            this.toDropDownAccountType.getStyleClass().add("error");
            result = false;
        }
        if (this.toDropDownAccount.getSelectionModel().getSelectedItem() == null) {
            this.toDropDownAccount.getStyleClass().add("error");
            result = false;
        }
        if (this.positionValue.getText() == null || this.positionValue.getText().isEmpty()) {
            this.positionValue.getStyleClass().add("error");
            result = false;
        }
        if (this.possiblePosition.getText() == null || this.possiblePosition.getText().isEmpty()) {
            this.possiblePosition.getStyleClass().add("error");
            result = false;
        }
        return result;
    }

    List<AccountingRecord> updateInformation() {
        return this.convertToAccountingRecords();
    }

}
