package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.Account;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO for business object Account
 */
public interface AccountDao extends IDao<Account> {
    /**
     * @param   accNo   the account number that should be searched for
     * @return          the account object that has been searched for
     */
    Account getByAccountNo(String accNo);

    /**
     * @param   accountName  the account name the searched account has
     * @return               the account matching the account name
     */
    Account getByName(String accountName);
}
