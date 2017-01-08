package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.AccountRecord;
import de.cneubauer.domain.dao.AccountRecordDao;

import java.util.List;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 08.01.2017.
 */
public class AccountRecordDaoImpl extends AbstractDao<AccountRecord> implements AccountRecordDao {
    public AccountRecordDaoImpl() {
        super(AccountRecord.class);
    }

    @Override
    protected void onSave(AccountRecord entity) {

    }

    public void saveAll(Set<AccountRecord> recordAccounts) {
        for (AccountRecord record : recordAccounts) {
            this.save(record);
        }
    }
}
