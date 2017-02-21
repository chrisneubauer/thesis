package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.ml.classification.Classification;
import de.cneubauer.ml.classification.Classifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
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
        category.addAll(models.get(0).getCredit().keySet());
        category.addAll(models.get(0).getDebit().keySet());

        this.helper.learnMockData(models);

        Classification classification = this.helper.getClassifier().classify(Collections.singleton("Grundgebühr 1&1 Surf & Phone 16.000 Office"));
        System.out.println(classification.getProbability());
        //System.out.println("a probability is: " + this.helper.getFeatureProbability("Grundgebühr 1&1 Surf & Phone 16.000 Office", category));
    }
}