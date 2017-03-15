package de.cneubauer.domain.dao.impl;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.dao.AccountDao;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Christoph on 15.03.2017.
 */
public class AccountDaoImplTest {
    private AccountDao dao;

    @Before
    public void setUp() throws Exception {
        this.dao = new AccountDaoImpl();
    }

    @After
    public void tearDown() throws Exception {
        this.dao = null;
    }

    @Test
    public void testMultipleInstances() {
        List<Account> accountList = this.dao.getAll();

        for (int i = 0; i < 30; i++) {
            AccountDao dao2 = new AccountDaoImpl();
            AccountDao dao3 = new AccountDaoImpl();
            AccountDao dao4 = new AccountDaoImpl();
            AccountDao dao5 = new AccountDaoImpl();
            AccountDao dao6 = new AccountDaoImpl();
            AccountDao dao7 = new AccountDaoImpl();
            AccountDao dao8 = new AccountDaoImpl();
            AccountDao dao9 = new AccountDaoImpl();
            AccountDao dao10 = new AccountDaoImpl();
            AccountDao dao11 = new AccountDaoImpl();
            AccountDao dao12 = new AccountDaoImpl();
            AccountDao dao13 = new AccountDaoImpl();
            AccountDao dao14 = new AccountDaoImpl();
            AccountDao dao15 = new AccountDaoImpl();
            AccountDao dao16 = new AccountDaoImpl();
            AccountDao dao17 = new AccountDaoImpl();
            AccountDao dao18 = new AccountDaoImpl();
            AccountDao dao19 = new AccountDaoImpl();
            AccountDao dao20 = new AccountDaoImpl();
            AccountDao dao21 = new AccountDaoImpl();
            AccountDao dao22 = new AccountDaoImpl();
            AccountDao dao23 = new AccountDaoImpl();
            AccountDao dao24 = new AccountDaoImpl();
            AccountDao dao25 = new AccountDaoImpl();
            AccountDao dao26 = new AccountDaoImpl();
            AccountDao dao27 = new AccountDaoImpl();
            AccountDao dao28 = new AccountDaoImpl();
            AccountDao dao29 = new AccountDaoImpl();
            AccountDao dao30 = new AccountDaoImpl();
            AccountDao dao31 = new AccountDaoImpl();

            List<Account> accountList2 = dao2.getAll();
            List<Account> accountList3 = dao3.getAll();
            List<Account> accountList4 = dao4.getAll();
            List<Account> accountList5 = dao5.getAll();
            List<Account> accountList6 = dao6.getAll();
            List<Account> accountList7 = dao7.getAll();
            List<Account> accountList8 = dao8.getAll();
            List<Account> accountList9 = dao9.getAll();
            List<Account> accountList10 = dao10.getAll();
            List<Account> accountList11 = dao11.getAll();
            List<Account> accountList12 = dao12.getAll();
            List<Account> accountList13 = dao13.getAll();
            List<Account> accountList14 = dao14.getAll();
            List<Account> accountList15 = dao15.getAll();
            List<Account> accountList16 = dao16.getAll();
            List<Account> accountList17 = dao17.getAll();
            List<Account> accountList18 = dao18.getAll();
            List<Account> accountList19 = dao19.getAll();
            List<Account> accountList20 = dao20.getAll();
            List<Account> accountList21 = dao21.getAll();
            List<Account> accountList22 = dao22.getAll();
            List<Account> accountList23 = dao23.getAll();
            List<Account> accountList24 = dao24.getAll();
            List<Account> accountList25 = dao25.getAll();
            List<Account> accountList26 = dao26.getAll();
            List<Account> accountList27 = dao27.getAll();
            List<Account> accountList28 = dao28.getAll();
            List<Account> accountList29 = dao29.getAll();
            List<Account> accountList30 = dao30.getAll();
            List<Account> accountList31 = dao31.getAll();

            Assert.assertTrue(accountList.size() == accountList2.size());
            Assert.assertTrue(accountList.size() == accountList3.size());
            Assert.assertTrue(accountList.size() == accountList4.size());
            Assert.assertTrue(accountList.size() == accountList5.size());
            Assert.assertTrue(accountList.size() == accountList6.size());
            Assert.assertTrue(accountList.size() == accountList7.size());
            Assert.assertTrue(accountList.size() == accountList8.size());
            Assert.assertTrue(accountList.size() == accountList9.size());
            Assert.assertTrue(accountList.size() == accountList10.size());
            Assert.assertTrue(accountList.size() == accountList11.size());
            Assert.assertTrue(accountList.size() == accountList12.size());
            Assert.assertTrue(accountList.size() == accountList13.size());
            Assert.assertTrue(accountList.size() == accountList14.size());
            Assert.assertTrue(accountList.size() == accountList15.size());
            Assert.assertTrue(accountList.size() == accountList16.size());
            Assert.assertTrue(accountList.size() == accountList17.size());

            dao.stopAccess();
            dao2.stopAccess();
            dao3.stopAccess();
            dao4.stopAccess();
            dao5.stopAccess();
            dao6.stopAccess();
            dao7.stopAccess();
            dao8.stopAccess();
            dao9.stopAccess();
            dao10.stopAccess();
            dao11.stopAccess();
            dao12.stopAccess();
            dao13.stopAccess();
            dao14.stopAccess();
            dao15.stopAccess();
            dao16.stopAccess();
            dao17.stopAccess();
            dao18.stopAccess();
            dao19.stopAccess();
            dao20.stopAccess();
            dao21.stopAccess();
            dao22.stopAccess();
            dao23.stopAccess();
            dao24.stopAccess();
            dao25.stopAccess();
            dao26.stopAccess();
            dao27.stopAccess();
            dao28.stopAccess();
            dao29.stopAccess();
            dao30.stopAccess();
            dao31.stopAccess();
        }
    }

}