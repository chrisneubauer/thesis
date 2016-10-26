package de.cneubauer.domain.dao.impl;

import de.cneubauer.AbstractTest;
import de.cneubauer.database.MySQLConnector;
import de.cneubauer.domain.bo.*;
import de.cneubauer.domain.dao.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * Created by Christoph Neubauer on 26.10.2016.
 * Tests a complete circle of persisting objects
 */
public class FullApplicationPersistenceTest extends AbstractTest {
    private MySQLConnector connector;
    private Connection con;
    private AddressDao addressDao;
    private CountryDao countryDao;
    private CorporateFormDao corporateFormDao;
    private LegalPersonDao legalPersonDao;
    private InvoiceDao invoiceDao;
    private ScanDao scanDao;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        databaseChanged = true;
        this.connector = new MySQLConnector();
        this.con = connector.connect();

        this.addressDao = new AddressDaoImpl();
        this.countryDao = new CountryDaoImpl();
        this.corporateFormDao = new CorporateFormDaoImpl();
        this.legalPersonDao = new LegalPersonDaoImpl();
        this.invoiceDao = new InvoiceDaoImpl();
        this.scanDao = new ScanDaoImpl();
    }

    @After
    public void tearDown() throws Exception {
        if (con != null) {
            con.close();
        }
        this.connector = null;
        this.addressDao = null;
        this.countryDao = null;
        this.corporateFormDao = null;
        this.legalPersonDao = null;
        this.invoiceDao = null;
        this.scanDao = null;

    }

    @Test
    public void testFullApplicationPersistence() {
        Country germany = new Country();
        Country france = new Country();
        germany.setName("Deutschland");
        germany.setAbbreviation("DE");

        france.setName("Frankreich");
        france.setAbbreviation("FR");

        this.countryDao.save(germany);
        this.countryDao.save(france);

        Address address1 = new Address();
        Address address2 = new Address();

        address1.setCountry(germany);
        address1.setZipCode(91056);
        address1.setCity("Erlangen");
        address1.setStreet("Mustermannstr. 17");

        address2.setCountry(france);
        address2.setZipCode(4271);
        address2.setCity("Paris");
        address2.setStreet("Baguettestr. 9");

        this.addressDao.save(address1);
        this.addressDao.save(address2);

        CorporateForm ag = new CorporateForm();
        ag.setName("Aktiengesellschaft");
        ag.setShortName("AG");
        HashSet<Country> countriesForAG = new HashSet<>();
        countriesForAG.add(germany);
        countriesForAG.add(france);
        ag.setUsedInCountries(countriesForAG);

        this.corporateFormDao.save(ag);

        LegalPerson company1 = new LegalPerson();
        LegalPerson company2 = new LegalPerson();
        LegalPerson consumer = new LegalPerson();

        company1.setCompanyName("Mustermann");
        company1.setIsCompany(true);
        company1.setCorporateForm(ag);
        company1.setAddress(address1);

        company2.setCompanyName("Baguette");
        company2.setIsCompany(true);
        company2.setCorporateForm(ag);
        company2.setAddress(address2);

        consumer.setIsCompany(false);
        consumer.setFirstName("Max");
        consumer.setName("Kunde");
        consumer.setAddress(address1);

        this.legalPersonDao.save(company1);
        this.legalPersonDao.save(company2);
        this.legalPersonDao.save(consumer);

        Invoice invoice1 = new Invoice();
        Invoice invoice2 = new Invoice();

        invoice1.setIssueDate(Timestamp.valueOf(LocalDateTime.now().minusDays(3)));
        invoice1.setDeliveryDate(Timestamp.valueOf(LocalDateTime.now()));
        invoice1.setChargeTotal(2999);
        invoice1.setDebitor(consumer);
        invoice1.setCreditor(company1);
        invoice1.setInvoiceNumber("4839745");

        invoice2.setIssueDate(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        invoice2.setDeliveryDate(Timestamp.valueOf(LocalDateTime.now()));
        invoice2.setChargeTotal(11);
        invoice2.setDebitor(consumer);
        invoice2.setCreditor(company2);
        invoice2.setInvoiceNumber("321111");

        this.invoiceDao.save(invoice1);
        this.invoiceDao.save(invoice2);

        Scan scan = new Scan();
        scan.setInvoiceInformation(invoice1);

        this.scanDao.save(scan);

        Country persistentGermany = this.countryDao.getById(germany.getId());
        Country persistentFrance = this.countryDao.getById(france.getId());
        Address persistentAddress1 = this.addressDao.getById(address1.getId());
        Address persistentAddress2 = this.addressDao.getById(address2.getId());
        CorporateForm persistentAg = this.corporateFormDao.getById(ag.getId());
        LegalPerson persistentCompany1 = this.legalPersonDao.getById(company1.getId());
        LegalPerson persistentCompany2 = this.legalPersonDao.getById(company2.getId());
        LegalPerson persistentConsumer = this.legalPersonDao.getById(consumer.getId());
        Invoice persistentInvoice1 = this.invoiceDao.getById(invoice1.getId());
        Invoice persistentInvoice2 = this.invoiceDao.getById(invoice2.getId());
        Scan persistentScan = this.scanDao.getById(scan.getId());

        Assert.notNull(persistentGermany);
        Assert.notNull(persistentFrance);
        Assert.notNull(persistentAddress1);
        Assert.notNull(persistentAddress2);
        Assert.notNull(persistentAg);
        Assert.notNull(persistentCompany1);
        Assert.notNull(persistentCompany2);
        Assert.notNull(persistentConsumer);
        Assert.notNull(persistentInvoice1);
        Assert.notNull(persistentInvoice2);
        Assert.notNull(persistentScan);

        Assert.isTrue(germany.getId() == persistentGermany.getId());
        Assert.isTrue(france.getId() == persistentFrance.getId());
        Assert.isTrue(address1.getId() == persistentAddress1.getId());
        Assert.isTrue(address2.getId() == persistentAddress2.getId());
        Assert.isTrue(ag.getId() == persistentAg.getId());
        Assert.isTrue(company1.getId() == persistentCompany1.getId());
        Assert.isTrue(company2.getId() == persistentCompany2.getId());
        Assert.isTrue(consumer.getId() == persistentConsumer.getId());
        Assert.isTrue(invoice1.getId() == persistentInvoice1.getId());
        Assert.isTrue(invoice2.getId() == persistentInvoice2.getId());
        Assert.isTrue(scan.getId() == persistentScan.getId());

        Assert.isTrue(persistentAg.getUsedInCountries().size() == 2);
        Assert.isTrue(persistentAg.getUsedInCountries().contains(persistentGermany));

        Assert.isTrue(persistentInvoice2.getDebitor().getAddress().getCountry() == germany);
    }
}
