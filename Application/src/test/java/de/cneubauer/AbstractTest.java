package de.cneubauer;

import de.cneubauer.database.MySQLConnector;
import de.cneubauer.domain.bo.Invoice;
import de.cneubauer.domain.bo.LegalPerson;
import de.cneubauer.domain.bo.Scan;
import de.cneubauer.domain.dao.impl.AbstractDao;
import de.cneubauer.domain.dao.impl.InvoiceDaoImpl;
import de.cneubauer.domain.dao.impl.LegalPersonDaoImpl;
import de.cneubauer.domain.dao.impl.ScanDaoImpl;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.hibernate.query.Query;
import org.junit.AfterClass;
import org.junit.Before;

import java.sql.Connection;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Configures log and necessary basic testing configuration
 */
public class AbstractTest {
    protected static boolean databaseChanged;
    protected Connection connection;
    protected MySQLConnector connector;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel(Level.ERROR);
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        if(databaseChanged) {
            clearDatabase();
        }
    }

    private static void clearDatabase() {
        //TODO: more general approach?
        //This way will result in a lot of code if more tables will exist
        AbstractDao<Scan> scanDao = new ScanDaoImpl();
        AbstractDao<Invoice> invoiceDao = new InvoiceDaoImpl();
        AbstractDao<LegalPerson> personDao = new LegalPersonDaoImpl();

        String deleteScans = "DELETE FROM Scan";
        scanDao.getSession().beginTransaction();
        Query query = scanDao.getSession().createQuery(deleteScans);
        query.executeUpdate();
        scanDao.getSession().getTransaction().commit();

        String deleteInvoices = "DELETE FROM Invoice";
        invoiceDao.getSession().beginTransaction();
        query = invoiceDao.getSession().createQuery(deleteInvoices);
        query.executeUpdate();
        invoiceDao.getSession().getTransaction().commit();

        String deletePersons = "DELETE FROM LegalPerson";
        personDao.getSession().beginTransaction();
        query = personDao.getSession().createQuery(deletePersons);
        query.executeUpdate();
        personDao.getSession().getTransaction().commit();
    }

}
