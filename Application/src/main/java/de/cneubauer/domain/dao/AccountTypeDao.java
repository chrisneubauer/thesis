package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.AccountType;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO for business object AccountType
 */
@Deprecated
public interface AccountTypeDao extends IDao<AccountType> {
    /**
     * @return  the asset account type ("Aktivkonto")
     */
    public AccountType getAssetAccount();

    /**
     * @return  the asset account type ("Pasivkonto")
    */
    public AccountType getLiabilityAccount();

    /**
     * @return  the asset account type ("Aufwandskonto")
     */
    public AccountType getExpenseAccount();

    /**
     * @return  the asset account type ("Ertragskonto")
     */
    public AccountType getRevenueAccount();

    /**
     * @return  the asset account type ("Statistikkonto")
     */
    public AccountType getStatisticalAccount();
}
