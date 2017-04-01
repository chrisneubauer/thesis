package de.cneubauer.ml;

import de.cneubauer.domain.bo.Account;
import de.cneubauer.domain.bo.AccountPosition;
import de.cneubauer.domain.bo.Position;
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
@Deprecated
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
        //String replacedString = feature;
        DictionaryHelper dictionaryHelper = new DictionaryHelper();
        String replacedString = dictionaryHelper.replaceValuesFromDictionary(feature);
        NaiveBayesHelper helper = new NaiveBayesHelper();
        ModelReader reader = new ModelReader();
        try {
            helper.trainClassifier(reader.getModels());

            // replace string if it is equal with an existing value
            for (Model m : reader.getModels()) {
                if (m.positionEqualsWith(feature)) {
                    replacedString = m.getPosition();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Classification<String, List<Account>> classification = helper.getClassifier().classify(Collections.singleton(replacedString));
        Logger.getLogger(this.getClass()).log(Level.INFO, "Probability of classification: " + classification.getProbability()*100 + "%");
        if (classification.getProbability() > ConfigHelper.getConfidenceRate()) {
            try {
                Model m = reader.getModelByStringAndAccounts(String.valueOf(classification.getFeatureset().toArray()[0]), classification.getCategory());
                m.setProbability(classification.getProbability());
                return m;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public Model createModel(Position r) {
        Model m = new Model();
        m.setPosition(r.getEntryText());

        double total = 0;

        for (AccountPosition ar : r.getPositionAccounts()) {
            if (ar.getIsDebit()) {
                total += ar.getBruttoValue();
            }
        }

        for (AccountPosition ar : r.getPositionAccounts()) {
            if (ar.getIsDebit()) {
                m.addToDebitAccounts(ar.getAccount(), (ar.getBruttoValue() / total));
            } else {
                m.addToCreditAccounts(ar.getAccount(), (ar.getBruttoValue() / total));
            }
        }
        return m;
    }
}
