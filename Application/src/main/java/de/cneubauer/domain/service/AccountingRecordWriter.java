package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.AccountRecord;
import de.cneubauer.domain.bo.Record;

import java.util.Set;

/**
 * Created by Christoph on 17.03.2017.
 * This class converts accounting records into human-readable text lines
 */
public class AccountingRecordWriter {
    public String convert(Record r) {
        StringBuilder result = new StringBuilder();
        result.append("Soll").append(this.getTabs()).append("Haben");
        result.append(System.lineSeparator());

        Set<AccountRecord> recordSet = r.getRecordAccounts();
        for (AccountRecord ar : recordSet) {
            if(ar.getIsDebit()) {
                result.append(ar.getAccount().getName()).append(" ").append(ar.getBruttoValue()).append("€").append(System.lineSeparator());
            }
        }

        for (AccountRecord ar : recordSet) {
            if(!ar.getIsDebit()) {
                result.append(this.getTabs()).append("an ").append(ar.getAccount().getName()).append(" ").append(ar.getBruttoValue()).append("€").append(System.lineSeparator());
            }
        }
        return result.toString();
    }

    private String getTabs() {
        return "\t\t\t\t\t\t\t\t";
    }
}
