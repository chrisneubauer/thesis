package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.AccountPosition;
import de.cneubauer.domain.dao.AccountPositionDao;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 08.01.2017.
 * DAO implementation of business object AccountRecord
 */
public class AccountPositionDaoImpl extends AbstractDao<AccountPosition> implements AccountPositionDao {
    public AccountPositionDaoImpl() {
        super(AccountPosition.class);
    }

    /**
     * Hook method to apply additional logic upon save
     * @param entity  the account-record relation that should be saved
     */
    @Override
    protected void onSave(AccountPosition entity) {

    }

    /**
     * Saves all given AccountRecords
     * @param recordAccounts  the accounts that should be stored
     */
    public void saveAll(Set<AccountPosition> recordAccounts) {
        for (AccountPosition record : recordAccounts) {
            this.save(record);
        }
    }

    /**
     * @param id the id of the position that combines all accountPositions
     * @return a collection of AccountPosition relations that are related to the given position id
     */
    @Override
    public Collection<AccountPosition> getByPosition(int id) {
        String hql = "FROM AccountPosition p WHERE p.position.id = ?1";

        Session session = this.getSessionFactory().openSession();
        Query q = session.createQuery(hql);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Searching by position id " + id);
        q.setParameter(1, id);

        List resultList = q.list();
        Collection<AccountPosition> results = new ArrayList<>(resultList.size());

        for (Object o : resultList) {
            if (o instanceof AccountPosition) {
                results.add((AccountPosition) o);
            }
        }

        session.close();
        return results;
    }
}
