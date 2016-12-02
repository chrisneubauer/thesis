package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountType;

import java.util.List;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO for business object Account
 */
public interface AccountDao extends IDao<Account> {
    /*
     * @param   accNo   the account number that should be searched for
     * @return          the account object that has been searched for
     */
    Account getByAccountNo(String accNo);

    /*
     * @param   type    the account type by which the accounts should be filtered
     * @return          a list of all accounts that belong to the specified account type
     */
    List<Account> getAllByType(AccountType type);

    /*
     * @param   typeId  the ID of the account tpe by which the accounts should be filtered
     * @return          a list of all accounts that belong to the specified account type
     */
    List<Account> getAllByType(int typeId);
}
