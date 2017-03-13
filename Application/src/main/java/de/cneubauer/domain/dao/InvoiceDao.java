package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.Invoice;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Data-Access-Object for Invoice
 * Defines specific methods for Invoice
 */
public interface InvoiceDao extends IDao<Invoice> {
    /**
     * @param date  the issue date that invoices should be filtered for
     * @return  a list of invoices that are issued on the given date
     */
    List<Invoice> getAllByDate(LocalDate date);

    List<Invoice> getAllBetweenDates(LocalDate date, LocalDate dateTo);
}
