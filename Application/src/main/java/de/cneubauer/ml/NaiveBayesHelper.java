package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.ml.classification.Classifier;
import de.cneubauer.ml.classification.bayes.BayesClassifier;

import java.util.*;

/**
 * Created by Christoph Neubauer on 03.02.2017.
 * This class is used as an interface for the naive bayes library
 */
class NaiveBayesHelper {
    private Classifier<String, List<Account>> bayes;

    /**
     * @return  the naive Bayes classifier
     */
    Classifier<String, List<Account>> getClassifier() {
        if (this.bayes == null) {
            this.bayes = new BayesClassifier<>();
        }
        return this.bayes;
    }

    /**
     * Trains the naive Bayes classifier
     * @param models  the training models
     */
    void learnMockData(List<Model> models) {
        Classifier<String, List<Account>> classifier = this.getClassifier();
        for (Model m : models) {
            List<Account> category = new LinkedList<>();
            category.addAll(m.getCredit());
            category.addAll(m.getDebit());

            classifier.learn(category, Collections.singletonList(m.getPosition()));
        }
    }

    /**
     * @param position  the position to be used as a feature
     * @param accounts  the accounts that will be treated as the category
     * @return  a probability value if the given position belongs to the given account structure
     */
    float getFeatureProbability(String position, List<Account> accounts) {
        //System.out.println("Categories total: " + this.getClassifier().getCategoriesTotal());
        //System.out.println("Category count = how often is this account structure present: " + this.getClassifier().categoryCount(accounts));
        //System.out.println("Strings: " + this.getClassifier().getFeatures().toString());
        //System.out.println("Feature count of abc = how often abc is in training set with given account structure: " + this.getClassifier().featureCount(position, accounts));

        return this.getClassifier().featureProbability(position, accounts);
    }
}
