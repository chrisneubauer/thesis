package de.cneubauer.domain.dao;

import de.cneubauer.database.MySQLConnector;
import de.cneubauer.domain.bo.LegalPerson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.sql.Connection;

import static org.junit.Assert.*;

public class LegalPersonDaoTest {
    private MySQLConnector connector;
    private Connection con;
    private LegalPersonDao dao;

    @Before
    public void setUp() throws Exception {
        this.connector = new MySQLConnector();
        this.con = connector.connect();
        this.dao = new LegalPersonDao();
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
        Assert.notNull(this.dao.getById(1));
    }
}