package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.dao.InvoiceDao;
import org.hibernate.query.Query;

import javax.persistence.TemporalType;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // hook-method before saving
    @Override
    public void onSave(Invoice entity) {
        if(entity.getCreatedDate() == null) {
            entity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        entity.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Override
    public List<Invoice> getAllByDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-DD");

        LocalDateTime before = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0);
        before = before.minusDays(1);
        LocalDateTime after = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0);
        after = after.plusDays(1);
        String hql = "FROM Invoice I WHERE I.issueDate >?1 AND I.issueDate <?2";

        Query q = this.getSession().createQuery(hql);
        q.setParameter(1, Timestamp.valueOf(before), TemporalType.TIMESTAMP);
        q.setParameter(2, Timestamp.valueOf(after), TemporalType.TIMESTAMP);

        List results = q.list();
        List<Invoice> result = new ArrayList<>(results.size());

        for (Object o : results) {
            if (o instanceof Invoice) {
                result.add((Invoice) o);
            }
        }

        return result;
    }
}
