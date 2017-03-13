package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.dao.ScanDao;
import de.cneubauer.domain.dao.impl.ScanDaoImpl;
import de.cneubauer.transformation.ZugFerdTransformator;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Created by Christoph Neubauer on 21.10.2016.
 * This service handles requests from the ResultsController
 * It takes the given invoice information and hands it to the ZugFerdTransformator
 * Output is the given PDF including a fully functional zugferd xml appended
 */
@Deprecated
public class ZugFerdExtendService {
    private ZugFerdTransformator transformator;
    private ScanDao dao;

    public ZugFerdExtendService() {
        this.transformator = new ZugFerdTransformator();
        this.dao = new ScanDaoImpl();
    }

    @Deprecated
    public byte[] appendInvoiceToPDF(byte[] originalPdf, Invoice i) throws IOException {
        io.konik.zugferd.Invoice konikInvoice = transformator.createFullConformalBasicInvoice(i);
        return transformator.appendInvoiceToPdf(originalPdf, konikInvoice);
    }

    /**
     * Links the invoice to the file and saves it as a scan in the database
     * @param file  the file that should be saved
     * @param i  the invoice that should be linked and saved
     */
    @Deprecated
    public void save(byte[] file, Invoice i) {
        Scan scanToSave = new Scan();
        scanToSave.setCreatedDate(Date.valueOf(LocalDate.now()));
        scanToSave.setModifiedDate(Date.valueOf(LocalDate.now()));
        scanToSave.setFile(file);
        scanToSave.setInvoiceInformation(i);
        dao.save(scanToSave);
    }
}
