package de.cneubauer.domain.dao.impl;

import de.cneubauer.AbstractTest;
import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountPosition;
import de.cneubauer.domain.bo.Position;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.PositionDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 * Test for PositionDaoImpl class
 */
public class PositionDaoImplTest extends AbstractTest {
    private AccountDao accountDao;
    private PositionDao dao;

    @Before
    public void setUp() throws Exception {
        this.accountDao = new AccountDaoImpl();
        this.dao = new PositionDaoImpl();
    }

    @After
    public void tearDown() throws Exception {
        this.dao = null;
        this.accountDao = null;
    }

    @Test
    public void testAccountingRecordCreation() {
        Account darlehen = this.accountDao.getByAccountNo("0550");
        Account bank = this.accountDao.getByAccountNo("0630");

        Position record = new Position();

        AccountPosition accountRecord = new AccountPosition();
        accountRecord.setAccount(darlehen);
        accountRecord.setPosition(record);
        accountRecord.setBruttoValue(400);
        accountRecord.setIsDebit(true);

        AccountPosition accountRecord2 = new AccountPosition();
        accountRecord2.setAccount(bank);
        accountRecord2.setPosition(record);
        accountRecord2.setBruttoValue(400);
        accountRecord2.setIsDebit(false);

        Set<AccountPosition> positionSet = new HashSet<>();
        positionSet.add(accountRecord);
        positionSet.add(accountRecord2);
        record.setPositionAccounts(positionSet);

        this.dao.save(record);

        Position record2 = this.dao.getById(record.getId());

        Assert.isTrue(record.getId() == record2.getId());
    }

    @Test
    public void testMultipleAccountsInOneRecord() {
        Position r = new Position();
        r.setEntryText("Kartoffeln");

        Account debit1 = this.accountDao.getById(4);
        Account debit2 = this.accountDao.getById(19);
        Account credit1 = this.accountDao.getById(25);

        AccountPosition accountRecord = new AccountPosition();
        accountRecord.setAccount(debit1);
        accountRecord.setPosition(r);
        accountRecord.setBruttoValue(400);
        accountRecord.setIsDebit(true);

        AccountPosition accountRecord2 = new AccountPosition();
        accountRecord2.setAccount(debit2);
        accountRecord2.setPosition(r);
        accountRecord2.setBruttoValue(600);
        accountRecord2.setIsDebit(true);

        AccountPosition accountRecord3 = new AccountPosition();
        accountRecord3.setAccount(credit1);
        accountRecord3.setPosition(r);
        accountRecord3.setBruttoValue(1000);
        accountRecord3.setIsDebit(false);

        Set<AccountPosition> accRecs = new HashSet<>(3);
        accRecs.add(accountRecord);
        accRecs.add(accountRecord2);
        accRecs.add(accountRecord3);

        r.setPositionAccounts(accRecs);

        this.dao.save(r);

        Position fromDb = this.dao.getById(r.getId());
        Assert.notNull(fromDb);

        double debitSum = 0;
        double creditSum = 0;

        for (AccountPosition record : fromDb.getPositionAccounts()) {
            if (record.getIsDebit()) {
                debitSum += record.getBruttoValue();
            } else {
                creditSum += record.getBruttoValue();
            }
        }
        Assert.isTrue(debitSum == creditSum);
    }
}