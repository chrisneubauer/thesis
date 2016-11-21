package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountType;

import java.util.List;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO for business object Account
 */
public interface AccountDao extends IDao<Account> {
    Account getByAccountNo(String accNo);
    List<Account> getAllByType(AccountType type);
}
