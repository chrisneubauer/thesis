package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.dao.InvoiceDao;
import de.cneubauer.domain.dao.ScanDao;
import de.cneubauer.domain.dao.impl.InvoiceDaoImpl;
import de.cneubauer.domain.dao.impl.ScanDaoImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 25.10.2016.
 * Database results are managed from this class
 * Has access to daos
 */
public class DatabaseResultsService {
    private ScanDao scanDao;
    private InvoiceDao invoiceDao;

    public List<Scan> getFromDatabase(LocalDateTime date, String deb, String cred, double value) {
        scanDao = new ScanDaoImpl();
        invoiceDao = new InvoiceDaoImpl();

        List<Invoice> invoiceResults = invoiceDao.getAllByDate(date);
        LinkedList<Scan> result = new LinkedList<>();

        for (Invoice i : invoiceResults) {
            result.addAll(scanDao.getByInvoiceId(i.getId()));
        }

        return result;
    }
}
