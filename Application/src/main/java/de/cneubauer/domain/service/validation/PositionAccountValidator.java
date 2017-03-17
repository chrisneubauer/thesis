package de.cneubauer.domain.service.validation;

import de.cneubauer.domain.bo.AccountPosition;
import de.cneubauer.domain.bo.Position;

import java.util.List;

/**
 * Created by Christoph Neubauer on 02.12.2016.
 * Validates AccountingRecords with minimum information
 */
public class PositionAccountValidator {
    private List<Position> recordList;

    /**
     * Constructor of the AccountingRecordValidator class
     * @param records  the list of records that should be validated
     */
    public PositionAccountValidator(List<Position> records) {
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

        for (Position r : this.recordList) {
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
    private boolean validateEntries(Position r) {
        double debitSum = 0;
        double creditSum = 0;
        boolean valid = true;

        for (AccountPosition record : r.getPositionAccounts()) {
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
