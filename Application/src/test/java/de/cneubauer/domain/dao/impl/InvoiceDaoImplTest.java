package de.cneubauer.domain.dao.impl;

import de.cneubauer.AbstractTest;
import de.cneubauer.database.MySQLConnector;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.LegalPerson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.util.List;

public class InvoiceDaoImplTest extends AbstractTest {
    private MySQLConnector connector;
    private Connection con;
    private InvoiceDaoImpl dao;
    private LegalPersonDaoImpl personDao;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        databaseChanged = true;
        this.connector = new MySQLConnector();
        this.con = connector.connect();
        this.dao = new InvoiceDaoImpl();
        this.personDao = new LegalPersonDaoImpl();
    }

    @After
    public void tearDown() throws Exception {
        if (con != null) {
            con.close();
        }
        this.connector = null;
        this.dao = null;
        this.personDao = null;
    }

    @Test
    public void testSave() throws Exception {
        Invoice i = new Invoice();

        LegalPerson creditor = new LegalPerson();
        creditor.setName("Kreditor");
        LegalPerson debitor = new LegalPerson();
        debitor.setName("Debitor");

        i.setCreditor(creditor);
        i.setDebitor(debitor);
        i.setMoneyVale(199.99);
        i.setHasSkonto(false);

        //Assertion not needed. Should fail by exception
        this.dao.save(i);
    }

    @Test
    public void testGetById() throws Exception {
        Invoice i = this.dao.getById(1);
        Assert.notNull(i);
        Assert.isTrue(i.getId() == 1);
        Assert.notNull(i.getCreditor());
        Assert.notNull(i.getDebitor());
    }

    @Test
    public void testGetAll() throws Exception {
        List<Invoice> result;
        result = this.dao.getAll();

        Assert.notNull(result);
        Assert.isTrue(result.size() > 0);
        System.out.println("Size of table Invoice: " + result.size());
    }
}