package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.dao.InvoiceDao;
import de.cneubauer.domain.dao.ScanDao;
import de.cneubauer.domain.dao.impl.InvoiceDaoImpl;
import de.cneubauer.domain.dao.impl.ScanDaoImpl;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.time.LocalDate;
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

    /**
     * Calls database and searches for Scans that match the specified values
     * @param   date    the date when the invoice has been specified
     * TODO: dateFrom and dateTo
     * @param   deb     the debitor to search for
     * @param   cred    the creditor to search for
     * @param   value   the value the total sum of the invoice has
     * @param dateTo
     * @return  a list of all scans that match the specified criteria
     */
    public List<Scan> getFromDatabase(LocalDate date, String deb, String cred, double value, LocalDate dateTo) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "calling database..");
        scanDao = new ScanDaoImpl();
        invoiceDao = new InvoiceDaoImpl();
        List<Invoice> invoiceResults;
        if (date != null) {
            if (dateTo != null) {
                invoiceResults = invoiceDao.getAllBetweenDates(date, dateTo);
            } else {
                invoiceResults = invoiceDao.getAllByDate(date);
            }
        } else {
            invoiceResults = invoiceDao.getAll();
        }
        LinkedList<Scan> result = new LinkedList<>();

        for (Invoice i : invoiceResults) {
            result.addAll(scanDao.getByInvoiceId(i.getId()));
        }

        for (Invoice invoiceResult : invoiceResults) {
            List<Scan> scans = (List<Scan>) scanDao.getByInvoiceId(invoiceResult.getId());
            for (Scan s : scans) {
                s.setInvoiceInformation(invoiceResult);
            }
        }

        if (value > 0) {
            for (Scan s : result) {
                if (! (s.getInvoiceInformation().getGrandTotal() > value - 1 && s.getInvoiceInformation().getGrandTotal() < value + 1)) {
                    result.remove(s);
                }
            }
        }

        if (deb != null && deb.length() > 0) {
            for (Scan s : result) {
                if (!s.getInvoiceInformation().getDebitor().toString().contains(deb)) {
                    result.remove(s);
                }
            }
        }

        if (cred != null && cred.length() > 0) {
            for (Scan s : result) {
                if (!s.getInvoiceInformation().getCreditor().toString().contains(cred)) {
                    result.remove(s);
                }
            }
        }

        return result;
    }
}
