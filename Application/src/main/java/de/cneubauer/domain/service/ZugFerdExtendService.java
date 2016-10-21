package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.dao.ScanDao;
import de.cneubauer.domain.dao.impl.ScanDaoImpl;
import de.cneubauer.transformation.ZugFerdTransformator;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by Christoph Neubauer on 21.10.2016.
 * This service handles requests from the ResultsController
 * It takes the given invoice information and hands it to the ZugFerdTransformator
 * Output is the given PDF including a fully functional zugferd xml appended
 */
public class ZugFerdExtendService {
    private ZugFerdTransformator transformator;
    private ScanDao dao;

    public ZugFerdExtendService() {
        this.transformator = new ZugFerdTransformator();
        this.dao = new ScanDaoImpl();
    }

    public byte[] appendInvoiceToPDF(byte[] originalPdf, Invoice i) throws IOException {
        io.konik.zugferd.Invoice konikInvoice = transformator.createFullConformalBasicInvoice(i);
        return transformator.appendInvoiceToPdf(originalPdf, konikInvoice);
    }

    public void save(byte[] pdf, Invoice i) {
        Scan scanToSave = new Scan();
        scanToSave.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        scanToSave.setModifiedDate(Timestamp.valueOf(LocalDateTime.now()));
        scanToSave.setFile(pdf);
        scanToSave.setInvoiceInformation(i);
        dao.save(scanToSave);
    }
}
