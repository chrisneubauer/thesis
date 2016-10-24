package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.dao.InvoiceDao;

import java.sql.Timestamp;
import java.time.LocalDateTime;

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
}
