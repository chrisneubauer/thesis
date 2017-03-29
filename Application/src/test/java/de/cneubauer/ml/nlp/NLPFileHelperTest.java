package de.cneubauer.ml.nlp;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.dao.AccountDao;
import de.cneubauer.domain.dao.impl.AccountDaoImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by Christoph on 28.03.2017.
 * Test for NLPFileHelper
 */
public class NLPFileHelperTest {
    private NLPFileHelper fileHelper;

    @Before
    public void setUp() throws Exception {
        fileHelper = new NLPFileHelper();
    }

    @After
    public void tearDown() throws Exception {
        fileHelper = null;
    }

    @Test
    public void getModels() throws Exception {
        fileHelper.writeToFile(this.createMockModel());
        List<NLPModel> models = fileHelper.getModels();

        Assert.assertNotNull(models);
    }

    @Test
    public void writeToFile() throws Exception {
        List<NLPModel> original = this.fileHelper.getModels();
        fileHelper.writeToFile(this.createMockModel());
        List<NLPModel> plusOneModel = this.fileHelper.getModels();
        Assert.assertNotNull(original);
        Assert.assertNotNull(plusOneModel);
        Assert.assertTrue(original.size() < plusOneModel.size());
        Assert.assertTrue(plusOneModel.size() == original.size() + 1);
    }

    private NLPModel createMockModel() {
        WordTokenizer tokenizer = new WordTokenizer();
        String[] tokens = tokenizer.tokenize("Grundgebühr Surf&Fon Flat 16.000 Office Surf&Fon");

        NLPModel mockModel = new NLPModel();
        for (String token : tokens) {
            mockModel.add(token);
        }

        AccountDao dao = new AccountDaoImpl();
        List<Account> accounts = dao.getAll();

        mockModel.addToCreditAccounts(accounts.get(4), 100);
        mockModel.addToDebitAccounts(accounts.get(20), 100);
        return mockModel;
    }
}