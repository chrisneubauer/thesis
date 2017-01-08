package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.AccountRecord;

import java.util.Set;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO for business object AccountRecord
 */
public interface AccountRecordDao extends IDao<AccountRecord> {
    void saveAll(Set<AccountRecord> recordAccounts);
}