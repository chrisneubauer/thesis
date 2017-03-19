package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.AccountPosition;

import java.util.Collection;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO for business object AccountRecord
 */
public interface AccountPositionDao extends IDao<AccountPosition> {
    /**
     * @param recordAccounts  the accounts that should be stored
     */
    void saveAll(Set<AccountPosition> recordAccounts);

    Collection<AccountPosition> getByPosition(int id);
}
