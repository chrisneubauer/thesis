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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 * Controller for managing extracted accounting records
 */
public class AccountingRecordsController extends GUIController {

    @FXML public ComboBox<AccountType> fromDropDownAccountType;
    @FXML public ComboBox<Account> fromDropDownAccount;
    @FXML public ComboBox<AccountType> toDropDownAccountType;
    @FXML public ComboBox<Account> toDropDownAccount;
    @FXML public TextField positionValue;
    @FXML public ImageView confidenceImage;
    @FXML public CheckBox recordRevised;
    @FXML public Label currentRecord;

    private List<AccountingRecordModel> recordsFound;
    private List<AccountType> types;

    void initData(List<AccountingRecord> data) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "initiating AccountingRecordsController data");
        List<AccountingRecordModel> records = new ArrayList<>(data.size());
        int index = 1;
        for (AccountingRecord record : data) {
            AccountingRecordModel model = new AccountingRecordModel(index++);
            model.setRevised(false);
            model.setRecord(record);
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
        this.setRecordsFound(records);
        this.initiateDropdowns(records.get(0));
        this.showAccountingRecords(data.get(0));
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

    // TODO: Necessary for validation
    private void addAllListeners() {
        /*this.fromDropDownAccountType.textProperty().addListener(this.addListenerToTextField(this.extractedInvoiceNumber));
        this.fromDropDownAccount.textProperty().addListener(this.addListenerToTextField(this.extractedIssueDate));
        this.toDropDownAccountType.textProperty().addListener(this.addListenerToTextField(this.extractedCreditor));
        this.toDropDownAccount.textProperty().addListener(this.addListenerToTextField(this.extractedDebitor));
        this.positionValue.textProperty().addListener(this.addListenerToTextField(this.extractedLineTotal));
        Logger.getLogger(this.getClass()).log(Level.INFO, "Listeners added to textfields");*/
    }

    private void showAccountingRecords(AccountingRecord record) {
        if (recordsFound != null && recordsFound.size() > 0) {
            positionValue.setText((String.valueOf(record.getBruttoValue())));

            if (record.getCredit() != null) {
                fromDropDownAccount.setValue(record.getCredit());
            }
            if (record.getDebit() != null) {
                toDropDownAccount.setValue(record.getDebit());
            }
        }
    }

    public void saveToDatabase(ActionEvent actionEvent) {
        // check if all records have been revised before saving
        if (this.validateAccountingRecords()) {
            for (AccountingRecordModel acc : this.recordsFound) {
                AccountFileHelper.write(acc.getRecord().getEntryText(), acc.getFromPossibleAccount().getAccountNo());
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

    public void nextRecord(ActionEvent actionEvent) {
        int current = Integer.parseInt(this.getCurrentRecord().getText()) - 1;
        if (this.getRecordsFound() != null && this.getRecordsFound().size() > current) {
            this.saveCurrentValuesToRecord();
            this.setCurrentRecord(new Label(String.valueOf(current + 1)));
            this.updateAccountingRecordView(this.getRecordsFound().get(current + 1));
        }
    }

    public void prevRecord(ActionEvent actionEvent) {
        int current = Integer.parseInt(this.getCurrentRecord().getText()) - 1;
        if (this.getRecordsFound() != null && current > 0) {
            this.saveCurrentValuesToRecord();
            this.setCurrentRecord(new Label(String.valueOf(current - 1)));
            this.updateAccountingRecordView(this.getRecordsFound().get(current - 1));
        }
    }

    private void saveCurrentValuesToRecord() {
        AccountingRecordModel current = this.getRecordsFound().get(Integer.valueOf(this.getCurrentRecord().getText()) - 1);
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
            if (accountingRecord.getCredit() != null && accountingRecord.getCredit().getType() != null) {
                this.setFromDropDownAccountType(accountingRecord.getCredit().getType());
            }
            this.setFromDropDownAccount(accountingRecord.getCredit());
        }

        if (accountingRecord.getDebit() != null) {

            if (accountingRecord.getDebit().getType() != null) {
                this.setFromDropDownAccountType(accountingRecord.getDebit().getType());

            }
            this.setFromDropDownAccount(accountingRecord.getDebit());
        }

        if (accountingRecord.getBruttoValue() > 0) {
            this.setPositionValue(accountingRecord.getBruttoValue());
        }
    }

    private Label getCurrentRecord() {
        return currentRecord;
    }

    private void setCurrentRecord(Label currentRecord) {
        this.currentRecord = currentRecord;
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
        this.fromDropDownAccountType.setValue(fromDropDownAccountType);
    }

    private Account getFromDropDownAccount() {
        return fromDropDownAccount.getValue();
    }

    private void setFromDropDownAccount(Account fromDropDownAccount) {
        this.fromDropDownAccount.setValue(fromDropDownAccount);
    }

    public AccountType getToDropDownAccountType() {
        return toDropDownAccountType.getValue();
    }

    public void setToDropDownAccountType(AccountType toDropDownAccountType) {
        this.toDropDownAccountType.setValue(toDropDownAccountType);
    }

    private Account getToDropDownAccount() {
        return toDropDownAccount.getValue();
    }

    public void setToDropDownAccount(Account toDropDownAccount) {
        this.toDropDownAccount.setValue(toDropDownAccount);
    }

    public ImageView getConfidenceImage() {
        return confidenceImage;
    }

    public void setConfidenceImage(ImageView confidenceImage) {
        this.confidenceImage = confidenceImage;
    }

    private double getPositionValue() {
        return Double.valueOf(this.positionValue.getText());
    }

    private void setPositionValue(double newValue) {
        this.positionValue.setText(String.valueOf(newValue));
    }

    // checks if accounting record can be set as revised
    public void checkRevised(ActionEvent actionEvent) {
        if (this.recordRevised.isSelected()) {
            AccountingRecordModel model = this.getRecordsFound().get(Integer.valueOf(this.getCurrentRecord().getText()));
            AccountingRecord record = model.getRecord();

            if (record.getCredit() != null && record.getDebit() != null && record.getBruttoValue() > 0) {
                model.setRevised(true);
            }
        } else {
            AccountingRecordModel model = this.getRecordsFound().get(Integer.valueOf(this.getCurrentRecord().getText()));
            model.setRevised(false);
        }
    }
}
