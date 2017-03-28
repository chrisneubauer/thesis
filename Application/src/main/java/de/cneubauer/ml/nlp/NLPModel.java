package de.cneubauer.ml.nlp;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountPosition;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Christoph on 27.03.2017.
 * NLPModel class
 */
public class NLPModel {
    private Map<String, Integer> values;
    private Map<Account, Double> debit;
    private Map<Account, Double> credit;
    private double probability;

    NLPModel() {
        this.debit = new HashMap<>();
        this.credit = new HashMap<>();
        this.values = new HashMap<>();
    }

    public void add(String key) {
        if (this.values.containsKey(key)) {
            int count = this.values.get(key);
            this.values.put(key, count+1);
        } else {
            this.values.put(key, 1);
        }
    }

    void addToDebitAccounts(Account debit, double percentualValue) {
        this.debit.put(debit, percentualValue);
    }

    void addToCreditAccounts(Account credit, double percentualValue) {
        this.credit.put(credit, percentualValue);
    }

    public void setValues(String values) {
        List<String> list = new ArrayList<>(3);
        Pattern p = Pattern.compile("\\{(.*?)\\}");
        Matcher m = p.matcher(values);
        int idx = 0;
        while (m.find()) {
            list.add(idx, m.group(1));
            idx++;
        }
        for (String s : list) {
            this.add(s);
        }
    }

    public Map<String, Integer> getValues() {
        return values;
    }


    Map<Account, Double> getDebit() {
        return debit;
    }

    public Map<Account, Double> getCredit() {
        return credit;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }

    public Set<AccountPosition> getAsAccountRecord(double value) {
        Set<AccountPosition> result = new HashSet<>();

        for (Map.Entry<Account, Double> debit : this.getDebit().entrySet()) {
            AccountPosition record = new AccountPosition();
            record.setIsDebit(true);
            record.setAccount(debit.getKey());
            record.setBruttoValue(debit.getValue() * value);
            result.add(record);
        }

        for (Map.Entry<Account, Double> credit : this.getCredit().entrySet()) {
            AccountPosition record = new AccountPosition();
            record.setIsDebit(false);
            record.setAccount(credit.getKey());
            record.setBruttoValue(credit.getValue() * value);
            result.add(record);
        }
        return result;
    }
}
