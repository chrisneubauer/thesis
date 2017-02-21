package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by Christoph Neubauer on 02.02.2017.
 * Test for the ModelWriter class
 */
public class ModelWriterTest {
    private ModelWriter writer;
    private ModelReader reader;

    @Before
    public void setUp() throws Exception {
        this.writer = new ModelWriter();
        this.reader = new ModelReader();
    }

    @After
    public void tearDown() throws Exception {
        this.writer = null;
        this.reader = null;
    }

    @Test
    public void writeToFile() throws Exception {
        Model m = new Model();
        m.setPosition("abc");
        Account creditAcc = new Account();
        creditAcc.setAccountNo("1000");

        Account debitAcc = new Account();
        debitAcc.setAccountNo("2000");

        Account debitAcc2 = new Account();
        debitAcc2.setAccountNo("3000");

        m.addToCreditAccounts(creditAcc, 1.0);
        m.addToDebitAccounts(debitAcc, 0.8);
        m.addToDebitAccounts(debitAcc2, 0.2);
        this.writer.writeToFile(m);

        List<Model> models = this.reader.getModels();

        Assert.isTrue(models.size() > 0);

        for (Model model : models) {
            if (model.getPosition().equals(m.getPosition())) {
                Assert.isTrue(model.getDebit().size() == m.getDebit().size());
                Assert.isTrue(model.getCredit().size() == m.getCredit().size());
                break;
            }
        }
    }

}