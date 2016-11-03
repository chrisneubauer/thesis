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
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceDaoImplTest extends AbstractTest {
    private MySQLConnector connector;
    private Connection con;
    private InvoiceDaoImpl dao;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        databaseChanged = true;
        this.connector = new MySQLConnector();
        this.con = connector.connect();
        this.dao = new InvoiceDaoImpl();
    }

    @After
    public void tearDown() throws Exception {
        if (con != null) {
            con.close();
        }
        this.connector = null;
        this.dao = null;
    }

    @Test
    public void testSave() throws Exception {
        Invoice i = new Invoice();
        i.setIssueDate(Timestamp.valueOf(LocalDateTime.now()));
        i.setDeliveryDate(Timestamp.valueOf(LocalDateTime.now()));

        LegalPerson creditor = new LegalPerson();
        creditor.setName("Kreditor");
        LegalPerson debitor = new LegalPerson();
        debitor.setName("Debitor");

        i.setCreditor(creditor);
        i.setDebitor(debitor);
        i.setLineTotal(100);
        i.setChargeTotal(0);
        i.setAllowanceTotal(0);
        i.setTaxBasisTotal(100);
        i.setTaxTotal(19);
        i.setGrandTotal(119);
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

    @Test
    public void testGetByDate() throws Exception {
        Invoice test = new Invoice();
        Invoice wrong = new Invoice();
        LocalDate testDate = LocalDateTime.now().toLocalDate();
        LocalDate wrongTestDate = LocalDateTime.now().minusDays(2).toLocalDate();
        test.setIssueDate(Timestamp.valueOf(LocalDateTime.from(testDate)));
        wrong.setIssueDate(Timestamp.valueOf(LocalDateTime.from(wrongTestDate)));
        this.dao.save(test);
        this.dao.save(test);
        this.dao.save(wrong);

        List<Invoice> results = this.dao.getAllByDate(testDate);

        Assert.notNull(results);
        Assert.isTrue(results.size() > 0);
        Assert.isTrue(results.contains(test));
        Assert.isTrue(!results.contains(wrong));
    }
}