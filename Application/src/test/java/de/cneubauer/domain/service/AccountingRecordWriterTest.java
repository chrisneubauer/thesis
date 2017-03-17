package de.cneubauer.domain.service;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountPosition;
import de.cneubauer.domain.bo.Position;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Position r = new Position();
        AccountPosition credit1 = new AccountPosition();
        AccountPosition credit2 = new AccountPosition();
        AccountPosition debit1 = new AccountPosition();
        AccountPosition debit2 = new AccountPosition();

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

        Set<AccountPosition> set = new HashSet<>();
        set.add(debit1);
        set.add(credit1);
        set.add(debit2);
        set.add(credit2);

        r.setPositionAccounts(set);

        Assert.assertNotNull(this.writer.convert(r));
    }

}