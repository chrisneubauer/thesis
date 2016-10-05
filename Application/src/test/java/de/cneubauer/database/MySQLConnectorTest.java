package de.cneubauer.database;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;

import java.sql.Connection;


public class MySQLConnectorTest {
    private Connection connection;
    private MySQLConnector connector;

    @Before
    public void setUp() throws Exception {
        connector = new MySQLConnector();
        connection = null;
    }

    @After
    public void tearDown() throws Exception {
        connector = null;
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testConnect() throws Exception {
        Assert.assertTrue(connection == null);
        connection = connector.connect();
        Assert.assertTrue(connection != null);
    }
}