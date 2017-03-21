package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Position;
import de.cneubauer.domain.dao.PositionDao;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * DAO implementation of business object Record
 */
public class PositionDaoImpl extends AbstractDao<Position> implements PositionDao {
    public PositionDaoImpl() {
        super(Position.class);
    }

    /**
     * Hook method to apply additional logic upon save
     * @param entity  the record that should be saved
     */
    @Override
    protected void onSave(Position entity) {
    }

    /**
     * @param id the id of the scan object
     * @return a collection of positions that are linked to a specific scan object
     */
    @Override
    public Collection<Position> getAllByScanId(int id) {
        String hql = "FROM Position p WHERE p.scan.id = ?1";

        Session session = this.getSessionFactory().openSession();
        Query q = session.createQuery(hql);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Searching by scan id " + id);
        q.setParameter(1, id);

        List resultList = q.list();
        Collection<Position> results = new ArrayList<>(resultList.size());

        for (Object o : resultList) {
            if (o instanceof Position) {
                results.add((Position) o);
            }
        }

        session.close();
        return results;
    }
}
