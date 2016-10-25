package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.dao.ScanDao;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Implementation of ScanDao
 */
public class ScanDaoImpl extends AbstractDao<Scan> implements ScanDao {
    public ScanDaoImpl() {
        super(Scan.class);
    }

    // hook-method before saving
    @Override
    public void onSave(Scan entity) {
        if(entity.getCreatedDate() == null) {
            entity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        entity.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Override
    public Collection<? extends Scan> getByInvoiceId(int id) {
        String hql = "FROM Scan s WHERE s.InvoiceInformation = ?1";

        Query q = this.getSession().createQuery(hql);
        q.setParameter(1, id);

        List resultList = q.getResultList();
        Collection<Scan> results = new ArrayList<>(resultList.size());

        for (Object o : resultList) {
            if (o instanceof Scan) {
                results.add((Scan) o);
            }
        }
        return results;
    }
}
