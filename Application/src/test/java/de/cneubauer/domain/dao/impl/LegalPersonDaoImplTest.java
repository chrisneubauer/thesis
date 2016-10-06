package de.cneubauer.domain.dao.impl;

import de.cneubauer.AbstractTest;
import de.cneubauer.database.MySQLConnector;
import de.cneubauer.domain.bo.LegalPerson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.util.List;

public class LegalPersonDaoImplTest extends AbstractTest {
    private MySQLConnector connector;
    private Connection con;
    private LegalPersonDaoImpl dao;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        databaseChanged = true;
        this.connector = new MySQLConnector();
        this.con = connector.connect();
        this.dao = new LegalPersonDaoImpl();
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
        LegalPerson p = new LegalPerson();
        p.setCompanyName("fakeCompany");
        p.setCorporateForm("AG");
        p.setCity("Erlangen");
        p.setZipCode(91056);

        //Assertion not needed. Should fail by exception
        this.dao.save(p);
    }

    @Test
    public void testGetById() throws Exception {
        LegalPerson p = this.dao.getById(1);
        Assert.notNull(p);
        Assert.isTrue(p.getId() == 1);
    }

    @Test
    public void testGetAll() throws Exception {
        List<LegalPerson> result;
        result = this.dao.getAll();

        Assert.notNull(result);
        Assert.isTrue(result.size() > 0);
        System.out.println("Size of table LegalPerson: " + result.size());
    }
}