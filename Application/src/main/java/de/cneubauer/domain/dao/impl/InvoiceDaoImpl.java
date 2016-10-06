package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.dao.InvoiceDao;

import java.util.List;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Implementation of InvoiceDAO
 */
public class InvoiceDaoImpl extends AbstractDao implements InvoiceDao {
    public InvoiceDaoImpl() {
        super();
    }

    @Override
    public Invoice getById(int id) {
        return null;
    }

    @Override
    public List<Invoice> getAll() {
        return null;
    }

    @Override
    public void save(Invoice i) {

    }
}
