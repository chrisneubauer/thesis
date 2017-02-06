package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.ml.classification.Classification;
import de.cneubauer.util.config.ConfigHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Christoph Neubauer on 02.02.2017.
 * This class is used for initiating the search
 */
public class LearningService {

    /**
     * @param position  the position to be checked
     * @return  true if the position is in the learning file, false if otherwise
     */
    public boolean exists(String position) {
        Model fakeModel = new Model();
        fakeModel.setPosition(position);
        return this.isModelExisting(fakeModel);
    }

    /**
     * @param model  the model that should be checked if it is saved in the learning file
     * @return  true if the model already exists, false if otherwise
     */
    public boolean isModelExisting(Model model) {
        ModelReader reader = new ModelReader();
        //TODO: url of the reader
        try {
            for (Model m : reader.getModels()) {
                if (m.positionEqualsWith(model.getPosition())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * uses naive bayes to search for the most likely Model
     * returns null if there is none or the confidence is too low
     * @param feature  the position that should be checked
     * @return  the model that is most likely to be used, null if the confidence is not reached
     */
    public Model getMostLikelyModel(String feature) {
        NaiveBayesHelper helper = new NaiveBayesHelper();
        ModelReader reader = new ModelReader();
        try {
            helper.learnMockData(reader.getModels());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Classification<String, List<Account>> classification = helper.getClassifier().classify(Collections.singleton(feature));
        Logger.getLogger(this.getClass()).log(Level.INFO, "Probability of classification: " + classification.getProbability()*100 + "%");
        if (classification.getProbability() > ConfigHelper.getConfidenceRate()) {
            try {
                return reader.getModelByStringAndAccounts(String.valueOf(classification.getFeatureset().toArray()[0]), classification.getCategory());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
