package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;

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

    boolean positionEqualsWith(String positionToCompare) {
        return position.equals(positionToCompare);
        // TODO: intelligent approach, using not only levenshtein distance but also wordnet
    }

    void addDebitAccount(Account a) {
        this.debit.add(a);
    }

    void addCreditAccount(Account a) {
        this.credit.add(a);
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    Set<Account> getDebit() {
        return debit;
    }

    public void setDebit(Set<Account> debit) {
        this.debit = debit;
    }

    Set<Account> getCredit() {
        return credit;
    }

    public void setCredit(Set<Account> credit) {
        this.credit = credit;
    }
}
