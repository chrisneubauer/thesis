package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.AccountType;
import de.cneubauer.domain.dao.AccountTypeDao;
import de.cneubauer.util.enumeration.AccType;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO implementation of business object AccountType
 */
public class AccountTypeDaoImpl extends AbstractDao<AccountType> implements AccountTypeDao {
    public AccountTypeDaoImpl() {
        super(AccountType.class);
    }

    @Override
    protected void onSave(AccountType entity) {

    }

    @Override
    public void save(AccountType entity) {
        Logger.getLogger(this.getClass()).log(Level.ERROR, "AccountTypes are fix. No additional AccountTypes can be added!");
        throw new UnsupportedOperationException("AccountTypes can not be extended");
    }

    @Override
    public AccountType getAssetAccount() {
        return super.getById(AccType.ASSET);
    }

    @Override
    public AccountType getLiabilityAccount() {
        return super.getById(AccType.LIABILITY);
    }

    @Override
    public AccountType getExpenseAccount() {
        return super.getById(AccType.EXPENSE);
    }

    @Override
    public AccountType getRevenueAccount() {
        return super.getById(AccType.REVENUE);
    }

    @Override
    public AccountType getStatisticalAccount() {
        return super.getById(AccType.STATISTICAL);
    }
}
