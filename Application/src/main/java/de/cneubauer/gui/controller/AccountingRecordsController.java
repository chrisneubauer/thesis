package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountType;
import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.AccountTypeDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.domain.dao.impl.AccountTypeDaoImpl;
import de.cneubauer.gui.model.AccountingRecordModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 * Controller for managing extracted accounting records
 */
public class AccountingRecordsController extends GUIController {

    public ChoiceBox<AccountType> fromDropDownAccountType;
    public ChoiceBox<Account> fromDropDownAccount;
    public ChoiceBox<AccountType> toDropDownAccountType;
    public ChoiceBox<Account> toDropDownAccount;
    public TextField positionValue;
    public ImageView confidenceImage;
    public CheckBox recordRevised;
    public Label currentRecord;

    private List<AccountingRecordModel> recordsFound;

    public void initData(List<AccountingRecord> data) {
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
    }

    @FXML
    private void initialize() {
        AccountTypeDao accountTypeDao = new AccountTypeDaoImpl();
        AccountDao accountDao = new AccountDaoImpl();
        ObservableList<AccountType> types = (ObservableList<AccountType>) accountTypeDao.getAll();
        ObservableList<Account> accounts = (ObservableList<Account>) accountDao.getAll();

        fromDropDownAccountType.setItems(types);
        fromDropDownAccount.setItems(accounts);
        toDropDownAccountType.setItems(types);
        toDropDownAccount.setItems(accounts);

        fromDropDownAccountType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!Objects.equals(oldValue, newValue)) {
                    fromDropDownAccount.setItems((ObservableList<Account>) accountDao.getAllByType(accountTypeDao.getById(newValue.intValue())));
                }
            }
        });

        toDropDownAccountType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!Objects.equals(oldValue, newValue)) {
                    toDropDownAccount.setItems((ObservableList<Account>) accountDao.getAllByType(accountTypeDao.getById(newValue.intValue())));
                }
            }
        });
    }

    public void saveToDatabase(ActionEvent actionEvent) {
        // check if all records have been revised before saving
        if (this.validateAccountingRecords()) {

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
