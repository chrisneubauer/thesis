package de.cneubauer.domain.service.validation;

import de.cneubauer.domain.bo.AccountingRecord;
import java.util.List;

/**
 * Created by Christoph Neubauer on 02.12.2016.
 * Validates AccountingRecords with minimum information
 */
public class AccountingRecordValidator {
    private List<AccountingRecord> recordList;
    public AccountingRecordValidator(List<AccountingRecord> records) {
        this.recordList = records;
    }

    public boolean isValid() {
        return this.validateAccountingRecords();
    }

    private boolean validateAccountingRecords() {
        boolean valid = true;

        for (AccountingRecord r : this.recordList) {
            valid = r.getBruttoValue() > 0;
            valid = valid && r.getDebit() != null;
            valid = valid && r.getCredit() != null;
            valid = valid && r.getEntryText() != null;
        }

        return valid;
    }
}
