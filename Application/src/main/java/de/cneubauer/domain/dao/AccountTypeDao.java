package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.AccountType;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO for business object AccountType
 */
public interface AccountTypeDao extends IDao<AccountType> {
    // Aktivkonto
    public AccountType getAssetAccount();

    // Passivkonto
    public AccountType getLiabilityAccount();

    // Aufwandskonto
    public AccountType getExpenseAccount();

    // Ertragskonto
    public AccountType getRevenueAccount();

    // Statistikkonto
    public AccountType getStatisticalAccount();
}