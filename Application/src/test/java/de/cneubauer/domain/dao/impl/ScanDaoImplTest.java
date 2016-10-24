package de.cneubauer.domain.dao.impl;

import de.cneubauer.AbstractTest;
import de.cneubauer.database.MySQLConnector;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.dao.InvoiceDao;
import de.cneubauer.domain.dao.LegalPersonDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.List;

public class ScanDaoImplTest extends AbstractTest {
    private MySQLConnector connector;
    private Connection con;
    private ScanDaoImpl dao;
    private InvoiceDao invoiceDao;
    private LegalPersonDao legalPersonDao;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        databaseChanged = true;
        this.connector = new MySQLConnector();
        this.con = connector.connect();
        this.dao = new ScanDaoImpl();
        this.invoiceDao = new InvoiceDaoImpl();
        this.legalPersonDao = new LegalPersonDaoImpl();
    }

    @After
    public void tearDown() throws Exception {
        if (con != null) {
            con.close();
        }
        this.connector = null;
        this.dao = null;
        this.invoiceDao = null;
        this.legalPersonDao = null;
    }

    @Test
    public void testSave() throws Exception {
        Scan s = new Scan();

        File pdf = new File(".\\src\\test\\resources\\invoice\\2015-11-26_Reifen Ebay.pdf");

        Invoice i = new Invoice();
        i.setGrandTotal(299.99);
        i.setHasSkonto(false);

        LegalPerson cred = new LegalPerson("TestCompany AG");
        LegalPerson deb = new LegalPerson("Hannah Maier");

        legalPersonDao.save(cred);
        legalPersonDao.save(deb);

        i.setDebitor(deb);
        i.setCreditor(cred);

        invoiceDao.save(i);

        s.setInvoiceInformation(i);
        s.setFile(Files.readAllBytes(pdf.toPath()));

        this.dao.save(s);

        Assert.isTrue(s.getId() > 0);
        Scan persistentScan = this.dao.getById(s.getId());
        Assert.notNull(persistentScan);
        Assert.isTrue(persistentScan.getId() == s.getId());
        Assert.isTrue(persistentScan.getInvoiceInformation() != null);
        Assert.isTrue(persistentScan.getInvoiceInformation().getId() == i.getId());
        Assert.isTrue(persistentScan.getInvoiceInformation().getDebitor().getId() == deb.getId());
        Assert.isTrue(persistentScan.getInvoiceInformation().getCreditor().getId() == cred.getId());
    }

    @Test
    public void testGetById() throws Exception {
        Scan s = this.dao.getById(1);
        Assert.notNull(s);
        Assert.isTrue(s.getId() == 1);
        Assert.notNull(s.getInvoiceInformation());
        Assert.notNull(s.getFile());
    }

    @Test
    public void testGetAll() throws Exception {
        List<Scan> result;
        result = this.dao.getAll();

        Assert.notNull(result);
        Assert.isTrue(result.size() > 0);
        System.out.println("Size of table Scan: " + result.size());
    }
}