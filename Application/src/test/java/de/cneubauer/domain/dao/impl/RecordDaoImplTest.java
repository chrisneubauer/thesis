package de.cneubauer.domain.dao.impl;

import de.cneubauer.AbstractTest;
import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountRecord;
import de.cneubauer.domain.bo.AccountType;
import de.cneubauer.domain.bo.Record;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.AccountRecordDao;
import de.cneubauer.domain.dao.AccountTypeDao;
import de.cneubauer.domain.dao.RecordDao;
import de.cneubauer.util.enumeration.AccType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 */
public class RecordDaoImplTest extends AbstractTest {
    private AccountTypeDao accountTypeDao;
    private AccountDao accountDao;
    private RecordDao dao;
    private AccountRecordDao accountRecordDao;

    @Before
    public void setUp() throws Exception {
        this.accountTypeDao = new AccountTypeDaoImpl();
        this.accountDao = new AccountDaoImpl();
        this.dao = new RecordDaoImpl();
        this.accountRecordDao = new AccountRecordDaoImpl();
    }

    @After
    public void tearDown() throws Exception {
        this.dao = null;
        this.accountDao = null;
        this.accountTypeDao = null;
    }

    @Test
    public void testAccountingRecordCreation() {
        AccountType asset = this.accountTypeDao.getAssetAccount();
        Account darlehen = this.accountDao.getByAccountNo("0550");
        Account bank = this.accountDao.getByAccountNo("0630");

        Assert.isTrue(Objects.equals(darlehen.getType().getName(), asset.getName()));
        Assert.isTrue(Objects.equals(bank.getType().getId(), AccType.LIABILITY));

        Record record = new Record();
        /*record.setCredit(bank);
        record.setDebit(darlehen);
        record.setBruttoValue(100);
        record.setDocumentNo("0001");
        record.setEntryDate(Timestamp.valueOf(LocalDateTime.now()));
        record.setVat_rate(0.0);*/

        this.dao.save(record);

        Record record2 = this.dao.getById(record.getId());

        Assert.isTrue(record.getId() == record2.getId());
        //Assert.isTrue(record.getBruttoValue() == record2.getBruttoValue());
       // Assert.isTrue(Objects.equals(record2.getDebit().getName(), darlehen.getName()));
    }

    @Test
    public void testAccountsFilteredByType() {
        AccountType expense = this.accountTypeDao.getExpenseAccount();
        List<Account> allAcounts = this.accountDao.getAll();
        List<Account> onlyExpense = this.accountDao.getAllByType(expense);
        List<Account> onlyExpenseById = this.accountDao.getAllByType(expense.getId());

        Assert.notNull(allAcounts);
        Assert.notNull(onlyExpense);
        Assert.notNull(onlyExpenseById);
        Assert.notEmpty(allAcounts);
        Assert.notEmpty(onlyExpense);
        Assert.notEmpty(onlyExpenseById);
        Assert.isTrue(allAcounts.size() > onlyExpense.size());
        Assert.isTrue(onlyExpense.size() == onlyExpenseById.size());
    }

    @Test
    public void testMultipleAccountsInOneRecord() {
        Record r = new Record();
        r.setEntryText("Kartoffeln");

        Account debit1 = this.accountDao.getById(4);
        Account debit2 = this.accountDao.getById(19);
        Account credit1 = this.accountDao.getById(25);

        AccountRecord accountRecord = new AccountRecord();
        accountRecord.setAccount(debit1);
        accountRecord.setRecord(r);
        accountRecord.setBruttoValue(400);
        accountRecord.setIsDebit(true);

        AccountRecord accountRecord2 = new AccountRecord();
        accountRecord2.setAccount(debit2);
        accountRecord2.setRecord(r);
        accountRecord2.setBruttoValue(600);
        accountRecord2.setIsDebit(true);

        AccountRecord accountRecord3 = new AccountRecord();
        accountRecord3.setAccount(credit1);
        accountRecord3.setRecord(r);
        accountRecord3.setBruttoValue(1000);
        accountRecord3.setIsDebit(false);

        Set<AccountRecord> accRecs = new HashSet<>(3);
        accRecs.add(accountRecord);
        accRecs.add(accountRecord2);
        accRecs.add(accountRecord3);

        r.setRecordAccounts(accRecs);

        this.dao.save(r);

        Record fromDb = this.dao.getById(r.getId());
        Assert.notNull(fromDb);

        double debitSum = 0;
        double creditSum = 0;

        for (AccountRecord record : fromDb.getRecordAccounts()) {
            if (record.getIsDebit()) {
                debitSum += record.getBruttoValue();
            } else {
                creditSum += record.getBruttoValue();
            }
        }
        Assert.isTrue(debitSum == creditSum);
    }
}