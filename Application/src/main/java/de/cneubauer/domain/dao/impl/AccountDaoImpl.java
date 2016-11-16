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

    @Override
    protected void onSave(Account entity) {

    }

    @Override
    public Account getByAccountNo(String accNo) {
        String hql = "FROM Account a WHERE a.accountNo = ?1";

        Query q = this.getSession().createQuery(hql);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Searching for Account " + accNo);
        q.setParameter(1, accNo);

        return (Account) q.getSingleResult();
    }
}
