package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountRecord;
import de.cneubauer.domain.bo.Record;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.gui.model.AccountingRecordModel;
import de.cneubauer.gui.util.AutoCompleteComboBoxListener;
import de.cneubauer.util.enumeration.ValidationStatus;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 * controller for managing extracted accounting records
 */
public class AccountingRecordsController extends SplitPaneController {
    @FXML
    public ComboBox<Account> fromDropDownAccountOne;
    @FXML
    public ComboBox<Account> fromDropDownAccountTwo;
    @FXML
    public ComboBox<Account> fromDropDownAccountThree;
    @FXML
    public ComboBox<Account> fromDropDownAccountFour;
    @FXML
    public ComboBox<Account> toDropDownAccountOne;
    @FXML
    public ComboBox<Account> toDropDownAccountTwo;
    @FXML
    public ComboBox<Account> toDropDownAccountThree;
    @FXML
    public ComboBox<Account> toDropDownAccountFour;

    @FXML
    public TextField positionValueFromAccountOne;
    @FXML
    public TextField positionValueFromAccountTwo;
    @FXML
    public TextField positionValueFromAccountThree;
    @FXML
    public TextField positionValueFromAccountFour;
    @FXML
    public TextField positionValueToAccountOne;
    @FXML
    public TextField positionValueToAccountTwo;
    @FXML
    public TextField positionValueToAccountThree;
    @FXML
    public TextField positionValueToAccountFour;

    @FXML
    public ImageView confidenceImage;
    @FXML
    public CheckBox recordRevised;
    @FXML
    public Label currentRecord;
    @FXML
    public Button SaveAccountingRecords;
    @FXML
    public TextField possiblePosition;

    @FXML
    public Button addEntryButton;
    @FXML
    public Button deleteCurrentEntryButton;
    @FXML
    public Button nextRecordButton;
    @FXML
    public Button prevRecordButton;

    private List<AccountingRecordModel> recordsFound;
    private SplitPaneController superCtrl;
    private int index = 1;
    private List<Account> accounts;

    void initData(List<Record> data, SplitPaneController superCtrl) {
        this.superCtrl = superCtrl;
        Logger.getLogger(this.getClass()).log(Level.INFO, "initiating AccountingRecordsController data");

        AccountDao accountDao = new AccountDaoImpl();
        this.accounts = accountDao.getAll();
        accountDao.stopAccess();

        List<AccountingRecordModel> records = this.convertToAccountingRecordModel(data);
        this.setRecordsFound(records);
        this.possiblePosition.getScene().getStylesheets().add(String.valueOf(getClass().getResource("../../../../css/validationError.css")));
        this.addAllListeners();

        Logger.getLogger(this.getClass()).log(Level.INFO, records.size() + " records found!");

        if (records.size() > 0) {
            this.initiateDropdowns(records.get(0));
            this.updateAccountingRecordView(records.get(0));
        } else {
            this.initiateDropdowns(null);
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

        ObservableList<Account> accounts = FXCollections.observableArrayList(this.accounts);
        Logger.getLogger(this.getClass()).log(Level.INFO, "adding " + accounts.size() + " elements to account dropdowns");

        fromDropDownAccountOne.setItems(accounts);
        fromDropDownAccountTwo.setItems(accounts);
        fromDropDownAccountThree.setItems(accounts);
        fromDropDownAccountFour.setItems(accounts);
        toDropDownAccountOne.setItems(accounts);
        toDropDownAccountTwo.setItems(accounts);
        toDropDownAccountThree.setItems(accounts);
        toDropDownAccountFour.setItems(accounts);

        if (model != null && model.getRecord().getRecordAccounts().size() > 0) {
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
            //Record newRecord = new Record();
            //newRecord.setEntryText(model.getPosition());
            result.add(index++, model.getRecord());
        }
        return result;
    }

    private void addAllListeners() {
        this.addAutoCompleteListener(this.fromDropDownAccountOne);
        this.addAutoCompleteListener(this.fromDropDownAccountTwo);
        this.addAutoCompleteListener(this.fromDropDownAccountThree);
        this.addAutoCompleteListener(this.fromDropDownAccountFour);
        this.addAutoCompleteListener(this.toDropDownAccountOne);
        this.addAutoCompleteListener(this.toDropDownAccountTwo);
        this.addAutoCompleteListener(this.toDropDownAccountThree);
        this.addAutoCompleteListener(this.toDropDownAccountFour);

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

    private ChangeListener<String> addListenerToTextField(TextField field) {
        return (observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 0) {
                field.getStyleClass().remove("error");
            } else {
                field.getStyleClass().add("error");
            }
        };
    }

    public void nextRecord() {
        if (this.getRecordsFound() != null && this.getRecordsFound().size() > this.index) {
            this.saveCurrentValuesToRecord();
            this.index++;
            this.setCurrentRecord(String.valueOf(this.index));
            this.updateAccountingRecordView(this.getRecordsFound().get(this.index - 1));
        }
    }

    public void prevRecord() {
        if (this.getRecordsFound() != null && this.index > 1) {
            this.saveCurrentValuesToRecord();
            this.index--;
            this.setCurrentRecord(String.valueOf(this.index));
            this.updateAccountingRecordView(this.getRecordsFound().get(this.index - 1));
        }
    }

    private void saveCurrentValuesToRecord() {
        AccountingRecordModel current;
        Record currentRecord;
        try {
            current = this.getRecordsFound().get(this.index - 1);
            currentRecord = current.getRecord();
        } catch (IndexOutOfBoundsException ex) {
            // when no current model exists -> No records found at all
            current = new AccountingRecordModel(this.index - 1);
            currentRecord = new Record();
            current.setRecord(currentRecord);

            if (this.getRecordsFound() == null) {
                this.setRecordsFound(new LinkedList<>(Collections.singleton(current)));
            } else if (this.getRecordsFound().size() == 0) {
                this.getRecordsFound().add(current);
            }
        }

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

        for (AccountRecord a : currentRecord.getRecordAccounts()) {
            a.setRecord(currentRecord);
        }
    }

    private void clearTextfields() {
        this.setFromDropDownAccountOne(null);
        this.setFromDropDownAccountTwo(null);
        this.setFromDropDownAccountThree(null);
        this.setFromDropDownAccountFour(null);
        this.setPositionValueFromAccountOne(0);
        this.setPositionValueFromAccountTwo(0);
        this.setPositionValueFromAccountThree(0);
        this.setPositionValueFromAccountFour(0);
        this.setToDropDownAccountOne(null);
        this.setToDropDownAccountTwo(null);
        this.setToDropDownAccountThree(null);
        this.setToDropDownAccountFour(null);
        this.setPositionValueToAccountOne(0);
        this.setPositionValueToAccountTwo(0);
        this.setPositionValueToAccountThree(0);
        this.setPositionValueToAccountFour(0);

        this.positionValueFromAccountOne.getStyleClass().remove("error");
        this.positionValueFromAccountTwo.getStyleClass().remove("error");
        this.positionValueFromAccountThree.getStyleClass().remove("error");
        this.positionValueFromAccountFour.getStyleClass().remove("error");
        this.positionValueToAccountOne.getStyleClass().remove("error");
        this.positionValueToAccountTwo.getStyleClass().remove("error");
        this.positionValueToAccountThree.getStyleClass().remove("error");
        this.positionValueToAccountFour.getStyleClass().remove("error");
        this.fromDropDownAccountOne.getStyleClass().remove("error");
        this.fromDropDownAccountTwo.getStyleClass().remove("error");
        this.fromDropDownAccountThree.getStyleClass().remove("error");
        this.fromDropDownAccountFour.getStyleClass().remove("error");
        this.toDropDownAccountOne.getStyleClass().remove("error");
        this.toDropDownAccountTwo.getStyleClass().remove("error");
        this.toDropDownAccountThree.getStyleClass().remove("error");
        this.toDropDownAccountFour.getStyleClass().remove("error");

        this.prevRecordButton.getStyleClass().remove("error");
        this.nextRecordButton.getStyleClass().remove("error");
    }

    // core method to update the whole view when new information is present
    private void updateAccountingRecordView(AccountingRecordModel currentModel) {
        Record record = currentModel.getRecord();

        this.clearTextfields();
        this.setPossiblePosition(record.getEntryText());
        int debitIdx = 0;
        int creditIdx = 0;
        for (AccountRecord entry : record.getRecordAccounts()) {
            if (entry.getIsDebit()) {
                switch (debitIdx) {
                    case 0:
                        this.setFromDropDownAccountOne(entry.getAccount());
                        this.setPositionValueFromAccountOne(entry.getBruttoValue());
                        debitIdx++;
                        break;
                    case 1:
                        this.setFromDropDownAccountTwo(entry.getAccount());
                        this.setPositionValueFromAccountTwo(entry.getBruttoValue());
                        debitIdx++;
                        break;
                    case 2:
                        this.setFromDropDownAccountThree(entry.getAccount());
                        this.setPositionValueFromAccountThree(entry.getBruttoValue());
                        debitIdx++;
                        break;
                    case 3:
                        this.setFromDropDownAccountFour(entry.getAccount());
                        this.setPositionValueFromAccountFour(entry.getBruttoValue());
                        debitIdx++;
                        break;
                }
            } else {
                switch (creditIdx) {
                    case 0:
                        this.setToDropDownAccountOne(entry.getAccount());
                        this.setPositionValueToAccountOne(entry.getBruttoValue());
                        creditIdx++;
                        break;
                    case 1:
                        this.setToDropDownAccountTwo(entry.getAccount());
                        this.setPositionValueToAccountTwo(entry.getBruttoValue());
                        creditIdx++;
                        break;
                    case 2:
                        this.setToDropDownAccountThree(entry.getAccount());
                        this.setPositionValueToAccountThree(entry.getBruttoValue());
                        creditIdx++;
                        break;
                    case 3:
                        this.setToDropDownAccountFour(entry.getAccount());
                        this.setPositionValueToAccountFour(entry.getBruttoValue());
                        creditIdx++;
                        break;
                }
            }
        }

        if (record.getEntryText() != null) {
            this.setPossiblePosition(record.getEntryText());
        }

        Logger.getLogger(this.getClass()).log(Level.INFO, "current confidence: " + currentModel.getRecord().getProbability());

        Image img;

        if (currentModel.getRecord().getProbability() < 0.5) {
            img = new Image("img/Circle_Red.png");
        } else if (currentModel.getRecord().getProbability() < 0.8 && currentModel.getRecord().getProbability() >= 0.5) {
            img = new Image("img/Circle_Yellow.png");
        } else {
            img = new Image("img/Circle_Green.png");
        }
        this.getConfidenceImage().setImage(img);
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
            boolean currentlyShownRecord = this.getRecordsFound().indexOf(record) == this.index - 1;
            Record current = record.getRecord();
            if (current.getEntryText() == null || Objects.equals(current.getEntryText(), "")) {
                errors.add(ValidationStatus.MISSINGPOSITION);
                if (currentlyShownRecord) {
                    this.possiblePosition.getStyleClass().add("error");
                } else {
                    if (record.getIndex() < this.index - 1) {
                        this.prevRecordButton.getStyleClass().add("error");
                    } else {
                        this.nextRecordButton.getStyleClass().add("error");
                    }
                }
            } else {
                if (currentlyShownRecord) {
                    this.possiblePosition.getStyleClass().remove("error");
                }
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
                    if (!currentlyShownRecord) {
                        if (record.getIndex() < this.index - 1) {
                            this.prevRecordButton.getStyleClass().add("error");
                        } else {
                            this.nextRecordButton.getStyleClass().add("error");
                        }
                    }
                }
                if (entry.getBruttoValue() == 0) {
                    errors.add(ValidationStatus.MISSINGVALUES);
                    if (!currentlyShownRecord) {
                        if (record.getIndex() < this.index - 1) {
                            this.prevRecordButton.getStyleClass().add("error");
                        } else {
                            this.nextRecordButton.getStyleClass().add("error");
                        }
                    }
                }
            }
            if (current.getRecordAccounts().size() == 0) {
                errors.add(ValidationStatus.MISSINGACCOUNTS);
                if (!currentlyShownRecord) {
                    if (record.getIndex() < this.index - 1) {
                        this.prevRecordButton.getStyleClass().add("error");
                    } else {
                        this.nextRecordButton.getStyleClass().add("error");
                    }
                }
                errors.add(ValidationStatus.MISSINGVALUES);
                if (!currentlyShownRecord) {
                    if (record.getIndex() < this.index - 1) {
                        this.prevRecordButton.getStyleClass().add("error");
                    } else {
                        this.nextRecordButton.getStyleClass().add("error");
                    }
                }
            }
            if (debitSum != creditSum) {
                errors.add(ValidationStatus.MALFORMEDVALUE);
                if (!currentlyShownRecord) {
                    if (record.getIndex() < this.index - 1) {
                        this.prevRecordButton.getStyleClass().add("error");
                    } else {
                        this.nextRecordButton.getStyleClass().add("error");
                    }
                }
            }

            if (currentlyShownRecord) {
                // styles for account
                if(this.getFromDropDownAccountOne() == null && this.getPositionValueFromAccountOne() != 0) {
                    if (this.fromDropDownAccountOne.getStyleClass().contains("error")) {
                        this.fromDropDownAccountOne.getStyleClass().add("error");
                    }
                } else {
                    this.fromDropDownAccountOne.getStyleClass().remove("error");
                }
                if(this.getFromDropDownAccountTwo() == null && this.getPositionValueFromAccountTwo() != 0) {
                    if (this.fromDropDownAccountTwo.getStyleClass().contains("error")) {
                        this.fromDropDownAccountTwo.getStyleClass().add("error");
                    }
                } else {
                    this.fromDropDownAccountTwo.getStyleClass().remove("error");
                }
                if(this.getFromDropDownAccountThree() == null && this.getPositionValueFromAccountThree() != 0) {
                    if (this.fromDropDownAccountThree.getStyleClass().contains("error")) {
                        this.fromDropDownAccountThree.getStyleClass().add("error");
                    }
                } else {
                    this.fromDropDownAccountThree.getStyleClass().remove("error");
                }
                if(this.getFromDropDownAccountFour() == null && this.getPositionValueFromAccountFour() != 0) {
                    if (this.fromDropDownAccountFour.getStyleClass().contains("error")) {
                        this.fromDropDownAccountFour.getStyleClass().add("error");
                    }
                } else {
                    this.fromDropDownAccountFour.getStyleClass().remove("error");
                }
                if(this.getToDropDownAccountOne() == null && this.getPositionValueToAccountOne() != 0) {
                    if (this.toDropDownAccountOne.getStyleClass().contains("error")) {
                        this.toDropDownAccountOne.getStyleClass().add("error");
                    }
                } else {
                    this.toDropDownAccountOne.getStyleClass().remove("error");
                }
                if(this.getToDropDownAccountTwo() == null && this.getPositionValueToAccountTwo() != 0) {
                    if (this.toDropDownAccountTwo.getStyleClass().contains("error")) {
                        this.toDropDownAccountTwo.getStyleClass().add("error");
                    }
                } else {
                    this.toDropDownAccountTwo.getStyleClass().remove("error");
                }
                if(this.getToDropDownAccountThree() == null && this.getPositionValueToAccountThree() != 0) {
                    if (this.toDropDownAccountThree.getStyleClass().contains("error")) {
                        this.toDropDownAccountThree.getStyleClass().add("error");
                    }
                } else {
                    this.toDropDownAccountThree.getStyleClass().remove("error");
                }
                if(this.getToDropDownAccountFour() == null && this.getPositionValueToAccountFour() != 0) {
                    if (this.toDropDownAccountFour.getStyleClass().contains("error")) {
                        this.toDropDownAccountFour.getStyleClass().add("error");
                    }
                } else {
                    this.toDropDownAccountFour.getStyleClass().remove("error");
                }

                // styles for positions
                if(this.getFromDropDownAccountOne() != null && this.getPositionValueFromAccountOne() == 0) {
                    if (this.positionValueFromAccountOne.getStyleClass().contains("error")) {
                        this.positionValueFromAccountOne.getStyleClass().add("error");
                    }
                } else {
                    this.positionValueFromAccountOne.getStyleClass().remove("error");
                }
                if(this.getFromDropDownAccountTwo() != null && this.getPositionValueFromAccountTwo() == 0) {
                    if (this.positionValueFromAccountTwo.getStyleClass().contains("error")) {
                        this.positionValueFromAccountTwo.getStyleClass().add("error");
                    }
                } else {
                    this.positionValueFromAccountTwo.getStyleClass().remove("error");
                }
                if(this.getFromDropDownAccountThree() != null && this.getPositionValueFromAccountThree() == 0) {
                    if (this.positionValueFromAccountThree.getStyleClass().contains("error")) {
                        this.positionValueFromAccountThree.getStyleClass().add("error");
                    }
                } else {
                    this.positionValueFromAccountThree.getStyleClass().remove("error");
                }
                if(this.getFromDropDownAccountFour() != null && this.getPositionValueFromAccountFour() == 0) {
                    if (this.positionValueFromAccountFour.getStyleClass().contains("error")) {
                        this.positionValueFromAccountFour.getStyleClass().add("error");
                    }
                } else {
                    this.positionValueFromAccountFour.getStyleClass().remove("error");
                }
                if(this.getToDropDownAccountOne() != null && this.getPositionValueToAccountOne() == 0) {
                    if (this.positionValueToAccountOne.getStyleClass().contains("error")) {
                        this.positionValueToAccountOne.getStyleClass().add("error");
                    }
                } else {
                    this.positionValueToAccountOne.getStyleClass().remove("error");
                }
                if(this.getToDropDownAccountTwo() != null && this.getPositionValueToAccountTwo() == 0) {
                    if (this.positionValueToAccountTwo.getStyleClass().contains("error")) {
                        this.positionValueToAccountTwo.getStyleClass().add("error");
                    }
                } else {
                    this.positionValueToAccountTwo.getStyleClass().remove("error");
                }
                if(this.getToDropDownAccountThree() != null && this.getPositionValueToAccountThree() == 0) {
                    if (this.positionValueToAccountThree.getStyleClass().contains("error")) {
                        this.positionValueToAccountThree.getStyleClass().add("error");
                    }
                } else {
                    this.positionValueToAccountThree.getStyleClass().remove("error");
                }
                if(this.getToDropDownAccountFour() != null && this.getPositionValueToAccountFour() == 0) {
                    if (this.positionValueToAccountFour.getStyleClass().contains("error")) {
                        this.positionValueToAccountFour.getStyleClass().add("error");
                    }
                } else {
                    this.positionValueToAccountFour.getStyleClass().remove("error");
                }
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
        //TODO: Bug if we are on position 2 of 2 positions
        this.prevRecord();
        this.getRecordsFound().remove(this.index);
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

    private ImageView getConfidenceImage() {
        return confidenceImage;
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
}