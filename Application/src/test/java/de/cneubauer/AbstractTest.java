package de.cneubauer;

import de.cneubauer.database.MySQLConnector;
import de.cneubauer.domain.bo.*;
import de.cneubauer.domain.dao.impl.*;
import de.cneubauer.util.config.ConfigHelper;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.query.Query;
import org.junit.AfterClass;
import org.junit.Before;

import java.sql.Connection;

/**
 * Created by Christoph Neubauer on 06.10.2016.
 * Configures log and necessary basic testing configuration
 */
public abstract class AbstractTest {
    protected static boolean databaseChanged;
    protected Connection connection;
    protected MySQLConnector connector;

    @Before
    public void setUp() throws Exception {
        PropertyConfigurator.configure("src\\test\\log4j.properties");
        LogManager.getRootLogger().setLevel(Level.DEBUG);
        ConfigHelper.getConfig();
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
        AbstractDao<CorporateForm> corporateFormDao = new CorporateFormDaoImpl();
        AbstractDao<Address> addressDao = new AddressDaoImpl();
        AbstractDao<Country> countryDao = new CountryDaoImpl();
        AbstractDao<Creditor> creditorDao = new CreditorDaoImpl();
        AbstractDao<DocumentCase> documentCaseDao = new DocumentCaseDaoImpl();
        AbstractDao<Position> positionDao = new PositionDaoImpl();
        AbstractDao<AccountPosition> accountPositionDao = new AccountPositionDaoImpl();

        String deleteAccountPositions = "DELETE FROM AccountPosition";
        accountPositionDao.getSession().beginTransaction();
        Query query = accountPositionDao.getSession().createQuery(deleteAccountPositions);
        query.executeUpdate();
        accountPositionDao.getSession().getTransaction().commit();

        String deletePositions = "DELETE FROM Position";
        positionDao.getSession().beginTransaction();
        query = positionDao.getSession().createQuery(deletePositions);
        query.executeUpdate();
        positionDao.getSession().getTransaction().commit();

        String deleteScans = "DELETE FROM Scan";
        scanDao.getSession().beginTransaction();
        query = scanDao.getSession().createQuery(deleteScans);
        query.executeUpdate();
        scanDao.getSession().getTransaction().commit();

        String deleteInvoices = "DELETE FROM Invoice";
        invoiceDao.getSession().beginTransaction();
        query = invoiceDao.getSession().createQuery(deleteInvoices);
        query.executeUpdate();
        invoiceDao.getSession().getTransaction().commit();

        String deleteDocCases = "DELETE FROM DocumentCase";
        documentCaseDao.getSession().beginTransaction();
        query = documentCaseDao.getSession().createQuery(deleteDocCases);
        query.executeUpdate();
        documentCaseDao.getSession().getTransaction().commit();

        String deleteCreditors = "DELETE FROM Creditor";
        creditorDao.getSession().beginTransaction();
        query = creditorDao.getSession().createQuery(deleteCreditors);
        query.executeUpdate();
        creditorDao.getSession().getTransaction().commit();

        String deletePersons = "DELETE FROM LegalPerson";
        personDao.getSession().beginTransaction();
        query = personDao.getSession().createQuery(deletePersons);
        query.executeUpdate();
        personDao.getSession().getTransaction().commit();

        String deleteCorporateForms = "DELETE FROM CorporateForm";
        corporateFormDao.getSession().beginTransaction();
        query = corporateFormDao.getSession().createQuery(deleteCorporateForms);
        query.executeUpdate();
        corporateFormDao.getSession().getTransaction().commit();


        String deleteAddresses = "DELETE FROM Address";
        addressDao.getSession().beginTransaction();
        query = addressDao.getSession().createQuery(deleteAddresses);
        query.executeUpdate();
        addressDao.getSession().getTransaction().commit();


        String deleteCountries = "DELETE FROM Country";
        countryDao.getSession().beginTransaction();
        query = countryDao.getSession().createQuery(deleteCountries);
        query.executeUpdate();
        countryDao.getSession().getTransaction().commit();
    }

}
