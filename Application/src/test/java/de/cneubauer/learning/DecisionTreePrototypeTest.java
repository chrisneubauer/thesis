package de.cneubauer.learning;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 21.11.2016.
 */
public class DecisionTreePrototypeTest {
    private DecisionTreePrototype prototype;

    @Before
    public void setUp() {
        this.prototype = new DecisionTreePrototype();
    }

    @Test
    public void testDecisionTree() throws Exception {
        List<DataSample> trainingData = new ArrayList<>(4);
        /*trainingData.add(0, new SimpleDataSample())

        List<Feature> features = getFeatures();

        tree.train(trainingData, features);

        // print tree after training
        tree.printTree();

        // read test data
        List<DataSample> testingData = readData(false);

        // classify all test data
        for (DataSample dataSample : testingData) {
            tree.classify(dataSample);
        }*/
    }
}