package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.dao.AccountingRecordDao;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO implementation of business object AccountingRecord
 */
public class AccountingRecordDaoImpl extends AbstractDao<AccountingRecord> implements AccountingRecordDao {
    public AccountingRecordDaoImpl() {
        super(AccountingRecord.class);
    }

    @Override
    protected void onSave(AccountingRecord entity) {

    }
}
