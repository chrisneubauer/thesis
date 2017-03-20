package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.dao.AccountDao;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.query.Query;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO implementation of business object Account
 */
public class AccountDaoImpl extends AbstractDao<Account> implements AccountDao {
    public AccountDaoImpl() {
        super(Account.class);
    }

    /**
     * Hook method to apply additional logic upon save
     * @param entity  the account that should be saved
     */
    @Override
    protected void onSave(Account entity) {

    }

    /**
     * @param   accNo   the account number that should be searched for
     * @return  the account that has been found for the given account number. Null if nothing has been found
     */
    @Override
    public Account getByAccountNo(String accNo) {
        String hql = "FROM Account a WHERE a.accountNo = ?1";

        Query q = this.getSession().createQuery(hql);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Searching for Account " + accNo);
        q.setParameter(1, accNo);

        return (Account) q.getSingleResult();
    }

    /**
     * @param   accountName  the account name the searched account has
     * @return  the account with the given name. Null if there is no such account
     */
    @Override
    public Account getByName(String accountName) {
        String hql = "FROM Account a WHERE a.name = ?1";

        Query q = this.getSession().createQuery(hql);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Searching for Account with name " + accountName);
        q.setParameter(1, accountName);

        return (Account) q.getSingleResult();
    }
}