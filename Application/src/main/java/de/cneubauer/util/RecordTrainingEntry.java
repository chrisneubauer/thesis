package de.cneubauer.util;

import de.cneubauer.domain.bo.Account;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Christoph Neubauer on 07.01.2017.
 */
public class RecordTrainingEntry {
    public String position;
    public Map<String, Double> debitAccounts;
    public Map<String, Double> creditAccounts;

    public RecordTrainingEntry(String position) {
        this.position = position;
        this.debitAccounts = new HashMap<>(0);
        this.creditAccounts = new HashMap<>(0);
    }

    public RecordTrainingEntry() {
        this.debitAccounts = new HashMap<>(0);
        this.creditAccounts = new HashMap<>(0);
    }

    public boolean isValid() {
        double debitSum = 0;
        double creditSum = 0;
        for (Map.Entry<String, Double> entry : debitAccounts.entrySet()) {
            debitSum += entry.getValue();
        }
        for (Map.Entry<String, Double> entry : creditAccounts.entrySet()) {
            creditSum += entry.getValue();
        }
        return debitSum == creditSum;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Map<String, Double> getDebitAccounts() {
        return debitAccounts;
    }

    public void setDebitAccount(String a, double value) {
        this.debitAccounts.put(a, value);
    }

    public Map<String, Double> getCreditAccounts() {
        return creditAccounts;
    }

    public void setCreditAccount(String a, double value) {
        this.creditAccounts.put(a, value);
    }
}
