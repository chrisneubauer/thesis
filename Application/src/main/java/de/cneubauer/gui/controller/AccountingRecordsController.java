package de.cneubauer.gui.controller;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountType;
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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

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
    }
}
