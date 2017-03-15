package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.ml.classification.Classifier;
import de.cneubauer.ml.classification.bayes.BayesClassifier;

import java.util.*;

/**
 * Created by Christoph Neubauer on 03.02.2017.
 * This class is used as an interface for the naive bayes library
 */
public class NaiveBayesHelper {
    private Classifier<String, List<Account>> bayes;

    /**
     * @return  the naive Bayes classifier
     */
    public Classifier<String, List<Account>> getClassifier() {
        if (this.bayes == null) {
            this.bayes = new BayesClassifier<>();
        }
        return this.bayes;
    }

    /**
     * Trains the naive Bayes classifier
     * @param models  the training models
     */
    public void trainClassifier(List<Model> models) {
        Classifier<String, List<Account>> classifier = this.getClassifier();
        for (Model m : models) {
            List<Account> category = new LinkedList<>();
            category.addAll(m.getCredit().keySet());
            category.addAll(m.getDebit().keySet());

            classifier.learn(category, Collections.singletonList(m.getPosition()));
        }
    }
}
