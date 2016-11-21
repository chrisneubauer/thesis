package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountType;
import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.AccountTypeDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.domain.dao.impl.AccountTypeDaoImpl;
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

    private List<AccountingRecord> recordsFound;

    public void initData(List<AccountingRecord> data) {
        this.recordsFound = data;
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
        return false;
    }

    public void nextRecord(ActionEvent actionEvent) {
        int current = Integer.parseInt(this.getCurrentRecord().getText()) - 1;
        if (this.getRecordsFound() != null && this.getRecordsFound().size() > current) {
            this.saveCurrentValuesToRecord();
            this.setCurrentRecord(new Label(String.valueOf(current + 1)));
            this.updateAccountingRecordView(this.getRecordsFound().get(current + 1));
        }
    }

    private void saveCurrentValuesToRecord() {
        AccountingRecord current = this.getRecordsFound().get(Integer.valueOf(this.getCurrentRecord().getText()) - 1);
        if (this.getFromDropDownAccount() != null)
        {
            current.setCredit(this.getFromDropDownAccount());
        }

        if (this.getFromDropDownAccountType() != null) {
            current.getCredit().setType(this.getFromDropDownAccountType());
        }

        if (this.getToDropDownAccount() != null) {
            current.setDebit(this.getToDropDownAccount());
        }

        if (this.getFromDropDownAccountType() != null) {
            current.getCredit().setType(this.getFromDropDownAccountType());
        }

        if (this.getPositionValue() > 0) {
            current.setBruttoValue(this.getPositionValue());
        }
    }

    // core method to update the whole view when new information is present
    private void updateAccountingRecordView(AccountingRecord accountingRecord) {
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

    public void prevRecord(ActionEvent actionEvent) {
        int current = Integer.parseInt(this.getCurrentRecord().getText()) - 1;
        if (this.getRecordsFound() != null && current > 0) {
            this.saveCurrentValuesToRecord();
            this.setCurrentRecord(new Label(String.valueOf(current - 1)));
            this.updateAccountingRecordView(this.getRecordsFound().get(current - 1));
        }
    }

    public Label getCurrentRecord() {
        return currentRecord;
    }

    public void setCurrentRecord(Label currentRecord) {
        this.currentRecord = currentRecord;
    }

    public List<AccountingRecord> getRecordsFound() {
        return recordsFound;
    }

    public void setRecordsFound(List<AccountingRecord> recordsFound) {
        this.recordsFound = recordsFound;
    }

    public AccountType getFromDropDownAccountType() {
        return fromDropDownAccountType.getValue();
    }

    public void setFromDropDownAccountType(AccountType fromDropDownAccountType) {
        this.fromDropDownAccountType.setValue(fromDropDownAccountType);
    }

    public Account getFromDropDownAccount() {
        return fromDropDownAccount.getValue();
    }

    public void setFromDropDownAccount(Account fromDropDownAccount) {
        this.fromDropDownAccount.setValue(fromDropDownAccount);
    }

    public AccountType getToDropDownAccountType() {
        return toDropDownAccountType.getValue();
    }

    public void setToDropDownAccountType(AccountType toDropDownAccountType) {
        this.toDropDownAccountType.setValue(toDropDownAccountType);
    }

    public Account getToDropDownAccount() {
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

    public double getPositionValue() {
        return Double.valueOf(this.positionValue.getText());
    }

    public void setPositionValue(double newValue) {
        this.positionValue.setText(String.valueOf(newValue));
    }
}
