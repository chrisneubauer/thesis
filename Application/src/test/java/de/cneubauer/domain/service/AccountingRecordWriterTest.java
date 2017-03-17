package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountRecord;
import de.cneubauer.domain.bo.Record;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Christoph on 17.03.2017.
 * Test for AccountingRecordWriter
 */
public class AccountingRecordWriterTest {
    private AccountingRecordWriter writer;
    private List<Account> accountList;

    @Before
    public void setUp() throws Exception {
        this.writer = new AccountingRecordWriter();
        AccountDao dao = new AccountDaoImpl();
        this.accountList = dao.getAll();
    }

    @Test
    public void convert() throws Exception {
        Record r = new Record();
        AccountRecord credit1 = new AccountRecord();
        AccountRecord credit2 = new AccountRecord();
        AccountRecord debit1 = new AccountRecord();
        AccountRecord debit2 = new AccountRecord();

        debit1.setIsDebit(true);
        debit2.setIsDebit(true);

        credit1.setBruttoValue(31.11);
        credit1.setAccount(accountList.get(20));

        credit2.setBruttoValue(19.11);
        credit2.setAccount(accountList.get(40));

        debit1.setBruttoValue(20);
        debit1.setAccount(accountList.get(60));

        debit2.setBruttoValue(30.22);
        debit2.setAccount(accountList.get(80));

        Set<AccountRecord> set = new HashSet<>();
        set.add(debit1);
        set.add(credit1);
        set.add(debit2);
        set.add(credit2);

        r.setRecordAccounts(set);

        Assert.assertNotNull(this.writer.convert(r));
    }

}