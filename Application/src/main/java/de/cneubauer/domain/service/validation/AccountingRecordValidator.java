package de.cneubauer.domain.service.validation;

import de.cneubauer.domain.bo.AccountRecord;
import de.cneubauer.domain.bo.Record;
import java.util.List;

/**
 * Created by Christoph Neubauer on 02.12.2016.
 * Validates AccountingRecords with minimum information
 */
public class AccountingRecordValidator {
    private List<Record> recordList;
    public AccountingRecordValidator(List<Record> records) {
        this.recordList = records;
    }

    public boolean isValid() {
        return this.validateAccountingRecords();
    }

    private boolean validateAccountingRecords() {
        boolean valid = true;

        for (Record r : this.recordList) {
            valid = this.validateEntries(r);
            valid = valid && r.getEntryText() != null;
        }

        return valid;
    }

    private boolean validateEntries(Record r) {
        double debitSum = 0;
        double creditSum = 0;
        boolean valid = true;

        for (AccountRecord record : r.getRecordAccounts()) {
            valid = valid && record.getBruttoValue() != 0;
            valid = valid && record.getAccount() != null;
            if (record.getIsDebit()) {
                debitSum += record.getBruttoValue();
            } else {
                creditSum += record.getBruttoValue();
            }
        }

        return valid && debitSum == creditSum;
    }
}
