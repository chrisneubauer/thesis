package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.AccountRecord;
import de.cneubauer.domain.dao.AccountRecordDao;

import java.util.Set;

/**
 * Created by Christoph Neubauer on 08.01.2017.
 * DAO implementation of business object AccountRecord
 */
public class AccountRecordDaoImpl extends AbstractDao<AccountRecord> implements AccountRecordDao {
    AccountRecordDaoImpl() {
        super(AccountRecord.class);
    }

    /**
     * Hook method to apply additional logic upon save
     * @param entity  the account-record relation that should be saved
     */
    @Override
    protected void onSave(AccountRecord entity) {

    }

    /**
     * Saves all given AccountRecords
     * @param recordAccounts  the accounts that should be stored
     */
    public void saveAll(Set<AccountRecord> recordAccounts) {
        for (AccountRecord record : recordAccounts) {
            this.save(record);
        }
    }
}
