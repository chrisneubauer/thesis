package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Record;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.dao.RecordDao;
import de.cneubauer.domain.dao.InvoiceDao;
import de.cneubauer.domain.dao.impl.RecordDaoImpl;
import de.cneubauer.domain.dao.impl.InvoiceDaoImpl;
import de.cneubauer.gui.model.ProcessResult;

import java.util.List;

/**
 * Created by Christoph Neubauer on 02.12.2016.
 * Service for saving revised documents to the database
 */
public class DatabaseService {
    public void saveProcessResult(ProcessResult result) {
        Invoice i = result.getExtractionModel().getInvoiceInformation();
        List<Record> records = result.getExtractionModel().getRecords();

        InvoiceDao invoiceDao = new InvoiceDaoImpl();
        invoiceDao.save(i);

        RecordDao accountDao = new RecordDaoImpl();
        for (Record r : records) {
            accountDao.save(r);
        }
    }
}
