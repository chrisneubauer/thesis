package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountType;
import de.cneubauer.domain.dao.AccountDao;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<Account> getAllByType(AccountType type) {
        String hql = "FROM Account a WHERE a.type.id = ?1";

        Query q = this.getSession().createQuery(hql);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Searching for Accounts of AccountType " + type.getName() + " with id " + type.getId());
        q.setParameter(1, type.getId());

        List accs = q.getResultList();
        List<Account> result = new ArrayList<>(accs.size());

        for (Object acc : accs) {
            result.add((Account) acc);
        }
        return result;
    }

    @Override
    public List<Account> getAllByType(int typeId) {
        String hql = "FROM Account a WHERE a.type.id = ?1";

        Query q = this.getSession().createQuery(hql);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Searching for Accounts of AccountType with id " + typeId);
        q.setParameter(1, typeId);

        List accs = q.getResultList();
        List<Account> result = new ArrayList<>(accs.size());

        for (Object acc : accs) {
            result.add((Account) acc);
        }
        return result;
    }

    @Override
    public Account getByName(String accountName) {
        String hql = "FROM Account a WHERE a.name = ?1";

        Query q = this.getSession().createQuery(hql);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Searching for Account with name " + accountName);
        q.setParameter(1, accountName);

        return (Account) q.getSingleResult();
    }
}