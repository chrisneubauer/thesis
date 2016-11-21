package de.cneubauer.gui.model;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountType;
import de.cneubauer.domain.bo.AccountingRecord;
import javafx.collections.ObservableList;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 * Model for the AccountingRecordsController
 */
public class AccountingRecordModel {
    private AccountingRecord record;
    private boolean revised;
    private AccountType toPossibleType;
    private Account toPossibleAccount;
    private AccountType fromPossibleType;
    private Account fromPossibleAccount;
    private int confidence;
    private int index;

    public AccountingRecordModel(int index) {
        this.index = index;
    }

    public AccountingRecord getRecord() {
        return record;
    }

    public void setRecord(AccountingRecord record) {
        this.record = record;
    }

    public boolean isRevised() {
        return revised;
    }

    public void setRevised(boolean revised) {
        this.revised = revised;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public AccountType getToPossibleType() {
        return toPossibleType;
    }

    public void setToPossibleType(AccountType toPossibleType) {
        this.toPossibleType = toPossibleType;
    }

    public Account getToPossibleAccount() {
        return toPossibleAccount;
    }

    public void setToPossibleAccount(Account toPossibleAccount) {
        this.toPossibleAccount = toPossibleAccount;
    }

    public AccountType getFromPossibleType() {
        return fromPossibleType;
    }

    public void setFromPossibleType(AccountType fromPossibleType) {
        this.fromPossibleType = fromPossibleType;
    }

    public Account getFromPossibleAccount() {
        return fromPossibleAccount;
    }

    public void setFromPossibleAccount(Account fromPossibleAccount) {
        this.fromPossibleAccount = fromPossibleAccount;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
