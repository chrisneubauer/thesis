package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.Invoice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Data-Access-Object for Invoice
 * Defines specific methods for Invoice
 */
public interface InvoiceDao extends IDao<Invoice> {

    List<Invoice> getAllByDate(LocalDateTime date);
}
