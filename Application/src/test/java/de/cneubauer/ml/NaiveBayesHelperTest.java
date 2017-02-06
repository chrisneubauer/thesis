package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph Neubauer on 03.02.2017.
 */
public class NaiveBayesHelperTest {
    private NaiveBayesHelper helper;

    @Before
    public void setUp() throws Exception {
        this.helper = new NaiveBayesHelper();
    }

    @After
    public void tearDown() throws Exception {
        this.helper = null;
    }

    @Test
    public void learnMockData() throws Exception {
        ModelReader reader = new ModelReader();
        List<Model> models = reader.getModels();

        List<Account> category = new LinkedList<>();
        category.addAll(models.get(0).getCredit());
        category.addAll(models.get(0).getDebit());

        this.helper.learnMockData(models);
        System.out.println("a probability is: " + this.helper.getFeatureProbability("a", category));
    }

}