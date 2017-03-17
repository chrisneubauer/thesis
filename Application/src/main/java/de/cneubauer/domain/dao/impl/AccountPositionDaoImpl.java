package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.AccountPosition;
import de.cneubauer.domain.dao.AccountPositionDao;

import java.util.Set;

/**
 * Created by Christoph Neubauer on 08.01.2017.
 * DAO implementation of business object AccountRecord
 */
public class AccountPositionDaoImpl extends AbstractDao<AccountPosition> implements AccountPositionDao {
    public AccountPositionDaoImpl() {
        super(AccountPosition.class);
    }

    /**
     * Hook method to apply additional logic upon save
     * @param entity  the account-record relation that should be saved
     */
    @Override
    protected void onSave(AccountPosition entity) {

    }

    /**
     * Saves all given AccountRecords
     * @param recordAccounts  the accounts that should be stored
     */
    public void saveAll(Set<AccountPosition> recordAccounts) {
        for (AccountPosition record : recordAccounts) {
            this.save(record);
        }
    }
}
