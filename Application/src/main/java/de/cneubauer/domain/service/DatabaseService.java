package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.dao.AccountingRecordDao;
import de.cneubauer.domain.dao.InvoiceDao;
import de.cneubauer.domain.dao.impl.AccountingRecordDaoImpl;
import de.cneubauer.domain.dao.impl.InvoiceDaoImpl;
import de.cneubauer.gui.model.ProcessResult;

import java.util.List;

/**
 * Created by Christoph Neubauer on 02.12.2016.
 */
public class DatabaseService {
    public void saveProcessResult(ProcessResult result) {
        Invoice i = result.getExtractionModel().getInvoiceInformation();
        List<AccountingRecord> records = result.getExtractionModel().getAccountingRecords();

        InvoiceDao invoiceDao = new InvoiceDaoImpl();
        invoiceDao.save(i);

        AccountingRecordDao accountDao = new AccountingRecordDaoImpl();
        for (AccountingRecord r : records) {
            accountDao.save(r);
        }
    }
}
