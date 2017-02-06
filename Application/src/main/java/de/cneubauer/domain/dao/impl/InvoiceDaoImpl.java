package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.dao.InvoiceDao;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.query.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Implementation of InvoiceDAO
 */
public class InvoiceDaoImpl extends AbstractDao<Invoice> implements InvoiceDao {
    public InvoiceDaoImpl() {
        super(Invoice.class);
    }

    /**
     * Hook method to apply additional logic upon save
     * @param entity  the invoice that should be saved
     */
    @Override
    public void onSave(Invoice entity) {
        if(entity.getCreatedDate() == null) {
            entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        }
        entity.setModifiedDate(Date.valueOf(LocalDate.now()));
    }

    /**
     *
     * @param date  the issue date that invoices should be filtered for
     * @return  a list of invoices that have the given issue date
     */
    @Override
    public List<Invoice> getAllByDate(LocalDate date) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "getting all by date: " + date.toString());

        String hql = "FROM Invoice I WHERE I.issueDate = ?1";

        Query q = this.getSession().createQuery(hql);
        q.setParameter(1, Date.valueOf(date));

        List results = q.getResultList();
        List<Invoice> result = new ArrayList<>(results.size());

        for (Object o : results) {
            if (o instanceof Invoice) {
                result.add((Invoice) o);
            }
        }

        Logger.getLogger(this.getClass()).log(Level.INFO, "Results found: " + result.size());
        return result;
    }
}
