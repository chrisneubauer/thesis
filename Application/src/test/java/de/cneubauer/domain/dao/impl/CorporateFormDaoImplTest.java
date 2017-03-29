package de.cneubauer.domain.dao.impl;

import de.cneubauer.AbstractTest;
import de.cneubauer.database.MySQLConnector;
import de.cneubauer.domain.bo.CorporateForm;
import de.cneubauer.domain.dao.CorporateFormDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.Assert;

import java.sql.Connection;

/**
 * Created by Christoph Neubauer on 13.12.2016.
 * Test for CorporateFormDao
 */
@Deprecated
public class CorporateFormDaoImplTest extends AbstractTest {
    private MySQLConnector connector;
    private Connection con;
    private CorporateFormDao dao;
    @Before
    public void setUp() throws Exception {
        super.setUp();

        databaseChanged = true;
        this.connector = new MySQLConnector();
        this.con = connector.connect();
        this.dao = new CorporateFormDaoImpl();
    }

    @After
    public void tearDown() throws Exception {
        this.con.close();
        this.con = null;
        this.connector = null;
        this.dao = null;
    }

    @Test
    @Ignore
    public void testGetById() {
        CorporateForm gmbH = this.dao.getById(2);
        Assert.notNull(gmbH);
        Assert.isTrue(gmbH.getShortName().equals("GmbH"));
    }

}