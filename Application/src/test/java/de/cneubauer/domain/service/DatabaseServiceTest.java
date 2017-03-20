package de.cneubauer.domain.service;

import de.cneubauer.AbstractTest;
import de.cneubauer.database.MySQLConnector;
import de.cneubauer.domain.bo.*;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import de.cneubauer.gui.model.ExtractionModel;
import de.cneubauer.gui.model.ProcessResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 07.12.2016.
 * Test for DatabaseService class
 */
public class DatabaseServiceTest extends AbstractTest {
    private MySQLConnector connector;
    private AccountDao accountDao;
    private DatabaseService service;

    @Before
    public void setUp() throws Exception {
      super.setUp();
        this.service = new DatabaseService();
        databaseChanged = true;
        this.connector = new MySQLConnector();
        this.accountDao = new AccountDaoImpl();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void saveProcessResult() throws Exception {
        ExtractionModel model = new ExtractionModel();
        List<Position> recordList = new ArrayList<>(1);
        recordList.add(this.createRecord());

        model.setRecords(recordList);
        model.setInvoiceInformation(this.createInvoice());

        ProcessResult mock = new ProcessResult();
        mock.setExtractionModel(model);

        this.service.saveProcessResult(mock);
    }

    private Invoice createInvoice() {
        Invoice i = new Invoice();
        i.setIssueDate(Date.valueOf(LocalDate.now()));
        i.setDeliveryDate(Date.valueOf(LocalDate.now()));

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

        return i;
    }

    private Position createRecord() {
        //AccountType asset = this.accountTypeDao.getAssetAccount();
        Account darlehen = this.accountDao.getByAccountNo("0550");
        Account bank = this.accountDao.getByAccountNo("0630");

        //Assert.isTrue(Objects.equals(darlehen.getType().getName(), asset.getName()));
        //Assert.isTrue(Objects.equals(bank.getType().getId(), AccType.LIABILITY));

        Position record = new Position();
        /*record.setCredit(bank);
        record.setDebit(darlehen);
        record.setBruttoValue(100);
        record.setDocumentNo("0001");
        record.setEntryDate(Timestamp.valueOf(LocalDateTime.now()));
        record.setVat_rate(0.0);*/

        return record;
    }
}