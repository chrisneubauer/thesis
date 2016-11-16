package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.dao.AccountDao;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO implementation of business object Account
 */
public class AccountDaoImpl extends AbstractDao<Account> implements AccountDao {
    public AccountDaoImpl() {
        super(Account.class);
    }

    @Override
    protected void onSave(Account entity) {

    }
}
