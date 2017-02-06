package de.cneubauer.domain.service;

import com.google.common.io.Files;
import de.cneubauer.domain.bo.Record;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.dao.RecordDao;
import de.cneubauer.domain.dao.InvoiceDao;
import de.cneubauer.domain.dao.ScanDao;
import de.cneubauer.domain.dao.impl.RecordDaoImpl;
import de.cneubauer.domain.dao.impl.InvoiceDaoImpl;
import de.cneubauer.domain.dao.impl.ScanDaoImpl;
import de.cneubauer.gui.model.ProcessResult;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by Christoph Neubauer on 02.12.2016.
 * Service for saving revised documents to the database
 */
public class DatabaseService {
    /**
     * Saves the process result in the database completely
     * @param result  the process result that should be saved
     */
    public void saveProcessResult(ProcessResult result) {
        Invoice i = result.getExtractionModel().getInvoiceInformation();
        List<Record> records = result.getExtractionModel().getRecords();

        InvoiceDao invoiceDao = new InvoiceDaoImpl();
        invoiceDao.save(i);

        Scan scan = new Scan();
        try {
            scan.setFile(Files.toByteArray(result.getFile()));
            scan.setCreatedDate(Date.valueOf(LocalDate.now()));
            scan.setInvoiceInformation(i);
            ScanDao scanDao = new ScanDaoImpl();
            scanDao.save(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RecordDao accountDao = new RecordDaoImpl();
        for (Record r : records) {
            accountDao.save(r);
        }
    }
}
