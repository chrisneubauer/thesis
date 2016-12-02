package de.cneubauer.domain.dao.impl;

import de.cneubauer.AbstractTest;
import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountType;
import de.cneubauer.domain.bo.AccountingRecord;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.AccountTypeDao;
import de.cneubauer.domain.dao.AccountingRecordDao;
import de.cneubauer.util.enumeration.AccType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 */
public class AccountingRecordDaoImplTest extends AbstractTest {
    private AccountTypeDao accountTypeDao;
    private AccountDao accountDao;
    private AccountingRecordDao dao;
    @Before
    public void setUp() throws Exception {
        this.accountTypeDao = new AccountTypeDaoImpl();
        this.accountDao = new AccountDaoImpl();
        this.dao = new AccountingRecordDaoImpl();
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

        AccountingRecord record = new AccountingRecord();
        record.setCredit(bank);
        record.setDebit(darlehen);
        record.setBruttoValue(100);
        record.setDocumentNo("0001");
        record.setEntryDate(Timestamp.valueOf(LocalDateTime.now()));
        record.setVat_rate(0.0);

        this.dao.save(record);

        AccountingRecord record2 = this.dao.getById(record.getId());

        Assert.isTrue(record.getId() == record2.getId());
        Assert.isTrue(record.getBruttoValue() == record2.getBruttoValue());
        Assert.isTrue(Objects.equals(record2.getDebit().getName(), darlehen.getName()));
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
}