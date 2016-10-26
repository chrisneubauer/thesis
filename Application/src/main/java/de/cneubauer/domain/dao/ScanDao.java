package de.cneubauer.domain.dao;

import de.cneubauer.domain.bo.Scan;

import java.util.Collection;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Data-Access-Object for Scan
 * Defines specific methods for Scan
 */
public interface ScanDao extends IDao<Scan> {
    Collection<Scan> getByInvoiceId(int id);
}
