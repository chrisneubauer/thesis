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

    /**
     * Constructor of the AccountingRecordValidator class
     * @param records  the list of records that should be validated
     */
    public AccountingRecordValidator(List<Record> records) {
        this.recordList = records;
    }

    /**
     * Executes validation for every record
     * @return  true if every record is valid, false if one or more are invalid
     */
    public boolean isValid() {
        return this.validateAccountingRecords();
    }

    /**
     * @return  true if every record is valid, false is one or more are invalid
     */
    private boolean validateAccountingRecords() {
        boolean valid = true;

        for (Record r : this.recordList) {
            valid = this.validateEntries(r);
            valid = valid && r.getEntryText() != null;
        }

        return valid;
    }

    /**
     * Validates all entries of one record
     * All credit sums have to equal debit sums and account should not be null
     * @param r  the record containing the entries
     * @return  true if all entries are valid, false if otherwise
     */
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
