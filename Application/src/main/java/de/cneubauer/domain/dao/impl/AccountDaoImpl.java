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
     * @param   type    the account type by which the accounts should be filtered
     * @return  a list of accounts that belong to the given account type
     */
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

    /**
     * @param   typeId  the ID of the account tpe by which the accounts should be filtered
     * @return  a list of accounts that belong to the given account type
     */
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