package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.util.config.ConfigHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 02.02.2017.
 * This class represents the machine learning model
 * It contains the invoice position as a string
 * And two Set<Account> that represent debit and credit
 */
public class Model {
    private String position;
    private Set<Account> debit;
    private Set<Account> credit;

    public Model() {
        this.debit = new HashSet<>();
        this.credit = new HashSet<>();
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
     * @param a  the account that should be added to the list of debit accounts
     */
    void addDebitAccount(Account a) {
        this.debit.add(a);
    }

    /**
     * @param a  the account that should be added to the list of credit accounts
     */
    void addCreditAccount(Account a) {
        this.credit.add(a);
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

    /**
     * @return  a set of all debit accounts for this model
     */
    Set<Account> getDebit() {
        return debit;
    }

    /**
     * @param debit  a set of debit accounts that should belong to this model
     */
    public void setDebit(Set<Account> debit) {
        this.debit = debit;
    }

    /**
     * @return  a set of all credit accounts for this model
     */
    Set<Account> getCredit() {
        return credit;
    }

    /**
     * @param credit  a set of credit accounts that should belong to this model
     */
    public void setCredit(Set<Account> credit) {
        this.credit = credit;
    }
}
