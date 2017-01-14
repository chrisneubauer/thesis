package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.dao.ScanDao;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.query.Query;

import java.sql.Date;
import java.time.LocalDate;
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
            entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        }
        entity.setModifiedDate(Date.valueOf(LocalDate.now()));
    }

    @Override
    public Collection<Scan> getByInvoiceId(int id) {
        String hql = "FROM Scan s WHERE s.invoiceInformation.id = ?1";

        Query q = this.getSession().createQuery(hql);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Searching by invoice id " + id);
        q.setParameter(1, id);

        List resultList = q.list();
        Collection<Scan> results = new ArrayList<>(resultList.size());

        for (Object o : resultList) {
            if (o instanceof Scan) {
                results.add((Scan) o);
            }
        }
        return results;
    }
}
