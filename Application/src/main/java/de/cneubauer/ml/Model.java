package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountRecord;
import de.cneubauer.domain.bo.Record;
import de.cneubauer.util.config.ConfigHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 02.02.2017.
 * This class represents the machine learning model
 * It contains the invoice position as a string
 * And two Set<Account> that represent debit and credit
 */
public class Model {
    private String position;
    private Map<Account, Double> debit;
    private Map<Account, Double> credit;
    private float probability;

    public Model() {
        this.debit = new HashMap<>();
        this.credit = new HashMap<>();
    }

    /**
     * Looks up if a given string could be equal to the position of this model
     * @param positionToCompare  the string to be compared
     * @return  true if it is likely the same position, false otherwise
     */
    boolean positionEqualsWith(String positionToCompare) {
        // TODO: intelligent approach, using not only levenshtein distance but also wordnet
        int levDistance = StringUtils.getLevenshteinDistance(this.getPosition(), positionToCompare);
        int length = this.getPosition().length();
        double distance = (double) levDistance / (double) length;
        if (distance < 1 - ConfigHelper.getConfidenceRate()) {
            return true;
        } else {
            return false;
        }

        //return position.equals(positionToCompare);
    }

    /**
     * @return  the position of this model
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position  the position the model should have
     */
    public void setPosition(String position) {
        this.position = position;
    }

    public Map<Account, Double> getDebit() {
        return debit;
    }

    public Map<Account, Double> getCredit() {
        return credit;
    }

    public void addToDebitAccounts(Account debit, double percentualValue) {
        this.debit.put(debit, percentualValue);
    }

    public void addToCreditAccounts(Account credit, double percentualValue) {
        this.credit.put(credit, percentualValue);
    }

    public Set<AccountRecord> getAsAccountRecord(double value) {
        Set<AccountRecord> result = new HashSet<>();

        for (Map.Entry<Account, Double> debit : this.getDebit().entrySet()) {
            AccountRecord record = new AccountRecord();
            record.setIsDebit(true);
            record.setAccount(debit.getKey());
            record.setBruttoValue(debit.getValue() * value);
            result.add(record);
        }

        for (Map.Entry<Account, Double> credit : this.getCredit().entrySet()) {
            AccountRecord record = new AccountRecord();
            record.setIsDebit(false);
            record.setAccount(credit.getKey());
            record.setBruttoValue(credit.getValue() * value);
            result.add(record);
        }
        return result;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public float getProbability() {
        return probability;
    }
}
