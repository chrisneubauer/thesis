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

    /**
     * Hook method to apply additional logic upon save
     * @param entity  the account type that should be saved
     */
    @Override
    protected void onSave(AccountType entity) {

    }

    /**
     * It is not possible to add more account types
     * Therefore, this method will throw an UnsupportedOperationException
     * @param entity  the entity to be saved in the database
     * @throws UnsupportedOperationException  account types can not be extended
     */
    @Override
    public void save(AccountType entity) {
        Logger.getLogger(this.getClass()).log(Level.ERROR, "AccountTypes are fix. No additional AccountTypes can be added!");
        throw new UnsupportedOperationException("AccountTypes can not be extended");
    }

    /**
     * @return  the account type "Aktivkonto"
     */
    @Override
    public AccountType getAssetAccount() {
        return super.getById(AccType.ASSET);
    }

    /**
     * @return  the account type "Passivkonto"
     */
    @Override
    public AccountType getLiabilityAccount() {
        return super.getById(AccType.LIABILITY);
    }

    /**
     * @return  the account type "Aufwandskonto"
     */
    @Override
    public AccountType getExpenseAccount() {
        return super.getById(AccType.EXPENSE);
    }

    /**
     * @return  the account type "Ertragskonto"
     */
    @Override
    public AccountType getRevenueAccount() {
        return super.getById(AccType.REVENUE);
    }

    /**
     * @return  the account type "Statistikkonto"
     */
    @Override
    public AccountType getStatisticalAccount() {
        return super.getById(AccType.STATISTICAL);
    }
}
