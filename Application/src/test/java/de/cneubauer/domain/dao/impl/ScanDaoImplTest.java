package de.cneubauer.domain.dao.impl;

import de.cneubauer.AbstractTest;
import de.cneubauer.database.MySQLConnector;
import de.cneubauer.domain.bo.*;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.InvoiceDao;
import de.cneubauer.domain.dao.LegalPersonDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class ScanDaoImplTest extends AbstractTest {
    private MySQLConnector connector;
    private Connection con;
    private ScanDaoImpl dao;
    private InvoiceDao invoiceDao;
    private LegalPersonDao legalPersonDao;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        databaseChanged = true;
        this.connector = new MySQLConnector();
        this.con = connector.connect();
        this.dao = new ScanDaoImpl();
        this.invoiceDao = new InvoiceDaoImpl();
        this.legalPersonDao = new LegalPersonDaoImpl();
    }

    @After
    public void tearDown() throws Exception {
        if (con != null) {
            con.close();
        }
        this.connector = null;
        this.dao = null;
        this.invoiceDao = null;
        this.legalPersonDao = null;
    }

    @Test
    public void testSave() throws Exception {
        Scan s = new Scan();

        File pdf = new File(".\\src\\test\\resources\\data\\generation\\template1_generated0.pdf");

        Invoice i = new Invoice();
        i.setGrandTotal(299.99);
        i.setHasSkonto(false);

        LegalPerson cred = new LegalPerson("TestCompany AG");
        LegalPerson deb = new LegalPerson("Hannah Maier");

        legalPersonDao.save(cred);
        legalPersonDao.save(deb);

        i.setDebitor(deb);
        i.setCreditor(cred);

        Position pos = new Position();
        pos.setEntryText("fakePos");

        AccountPosition debit = new AccountPosition();
        AccountPosition credit = new AccountPosition();

        AccountDao accountDao = new AccountDaoImpl();
        List<Account> accounts = accountDao.getAll();
        debit.setAccount(accounts.get(5));
        debit.setBruttoValue(20);
        debit.setIsDebit(true);
        debit.setPosition(pos);
        credit.setAccount(accounts.get(8));
        credit.setBruttoValue(20);
        credit.setPosition(pos);

        Set<AccountPosition> accountPositionSet = new HashSet<>();
        accountPositionSet.add(debit);
        accountPositionSet.add(credit);

        pos.setPositionAccounts(accountPositionSet);
        pos.setScan(s);

        s.setInvoiceInformation(i);
        s.setFile(Files.readAllBytes(pdf.toPath()));
        s.setPositions(Collections.singleton(pos));

        this.dao.save(s);

        Assert.isTrue(s.getId() > 0);
        Scan persistentScan = this.dao.getById(s.getId());
        Assert.notNull(persistentScan);
        Assert.isTrue(persistentScan.getId() == s.getId());
        Assert.isTrue(persistentScan.getInvoiceInformation() != null);
        Assert.isTrue(persistentScan.getPositions() != null);
        Assert.isTrue(persistentScan.getPositions().size() > 0);
        Assert.isTrue(persistentScan.getInvoiceInformation().getId() == i.getId());
        Assert.isTrue(persistentScan.getInvoiceInformation().getDebitor().getId() == deb.getId());
        Assert.isTrue(persistentScan.getInvoiceInformation().getCreditor().getId() == cred.getId());
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

    @Test
    public void testGetByInvoiceId() {
        Invoice correctInvoice = new Invoice();
        Invoice wrongInvoice = new Invoice();
        correctInvoice.setIssueDate(Date.valueOf(LocalDate.now().minusDays(1)));
        wrongInvoice.setIssueDate(Date.valueOf(LocalDate.now()));

        Scan scan1 = new Scan();
        scan1.setInvoiceInformation(correctInvoice);
        Scan scan2 = new Scan();
        scan2.setInvoiceInformation(correctInvoice);
        Scan scan3 = new Scan();
        scan3.setInvoiceInformation(wrongInvoice);

        this.invoiceDao.save(correctInvoice);
        this.invoiceDao.save(wrongInvoice);

        this.dao.save(scan1);
        this.dao.save(scan2);
        this.dao.save(scan3);

        System.out.println("Using id: " + correctInvoice.getId());
        Collection<Scan> results = this.dao.getByInvoiceId(correctInvoice.getId());

        Assert.notNull(results);
        Assert.isTrue(results.size() > 0);
        Assert.isTrue(results.stream().anyMatch(dbObject -> dbObject.getId() == scan1.getId()));
        Assert.isTrue(results.stream().anyMatch(dbObject -> dbObject.getId() == scan2.getId()));
        Assert.isTrue(results.stream().noneMatch(dbObject -> dbObject.getId() == scan3.getId()));
        Scan resultObject = (Scan) results.toArray()[0];
        Assert.isTrue(resultObject.getInvoiceInformation().getId() == correctInvoice.getId());
    }
}