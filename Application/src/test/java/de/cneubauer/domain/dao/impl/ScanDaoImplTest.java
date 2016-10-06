package de.cneubauer.domain.dao.impl;

import de.cneubauer.database.MySQLConnector;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.Scan;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.List;

public class ScanDaoImplTest {
    private MySQLConnector connector;
    private Connection con;
    private ScanDaoImpl dao;
    private InvoiceDaoImpl invoiceDao;

    @Before
    public void setUp() throws Exception {
        this.connector = new MySQLConnector();
        this.con = connector.connect();
        this.dao = new ScanDaoImpl();
        this.invoiceDao = new InvoiceDaoImpl();
    }

    @After
    public void tearDown() throws Exception {
        if (con != null) {
            con.close();
        }
        this.connector = null;
        this.dao = null;
        this.invoiceDao = null;
    }

    @Test
    public void testSave() throws Exception {
        Scan s = new Scan();

        File pdf = new File(".\\src\\test\\resources\\invoice\\2015-11-26_Reifen Ebay.pdf");

        Invoice i = this.invoiceDao.getById(1);

        s.setInvoiceInformation(i);
        s.setFile(Files.readAllBytes(pdf.toPath()));

        //Assertion not needed. Should fail by exception
        this.dao.save(s);
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