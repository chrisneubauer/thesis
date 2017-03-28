package de.cneubauer.ml.nlp;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Christoph on 28.03.2017.
 * Classifies tokens and returns the model list containing the probabilities
 */
public class NLPClassificator {
    private List<NLPModel> models;

    public NLPClassificator(List<NLPModel> modelList) {
        this.models = modelList;
    }

    public List<NLPModel> classify(String[] tokens) {
        OpenNLPSpeechTagger tagger = new OpenNLPSpeechTagger();
        String[] tags = tagger.tag(tokens);
        Map<String, Double> valuedKeys = new HashMap<>();
        for (int i = 0; i < tokens.length; i++)
        {
            String token = tokens[i];
            double value = this.calculateImportance(tags[i]);
            valuedKeys.put(token, value);
        }

        for (NLPModel model : models) {
            this.calculateProbability(model, valuedKeys);
        }
        return models;
    }

    private void calculateProbability(NLPModel model, Map<String, Double> valuedKeys) {
        // valued keys are the total amount of keys for the position
        // 1.0 is reached when all the keys are exactly matched
        // importance of a key is defined by the double value
        // example: keys [1][2][3]
        // model contains [2][3]
        // basic likelihood: 0.66%
        // but importance is: 1=0.8; 2=2.0; 3=1.0
        // hence calculation is:
        // 0*[1]*0.8 + 1*[2]*2.0 + 1*[3]*1.0
        // 0 + 2.0 + 1.0 = 3.0 / maximum (defined as each key * value)
        // maximum = 0.8 + 2.0 + 1.0 = 3.8
        // 3.0 / 3.8 = 0.79 probability
        double maximum = 0;
        double value = 0;

        for (Double entry : valuedKeys.values()) {
            maximum += entry;
        }

        for (String key : model.getValues().keySet()) {
            if (valuedKeys.containsKey(key)) {
                value += valuedKeys.get(key);
            }
        }
        Logger.getLogger(this.getClass()).log(Level.INFO, "Classifying model " + model.getValues().toString() + " with probability of " + value / maximum );
        model.setProbability(value / maximum);
    }

    /**
     * Defines importance values for tags
     * @param tag can be one of the following:
     *            <ul>
     *              <li>NN : common noun</li>
     *              <li>NE : proper noun</li>
     *              <li>VVFIN : full finite verb</li>
     *              <li>VVINF : full infinitive</li>
     *              <li>FM : foreign word</li>
     *              <li>ART : Article</li>
     *            </ul>
     * @return double value that shows the importance of a tag
     */
    private double calculateImportance(String tag) {
        switch (tag) {
            case "NN":
            case "NE":
                return 2.0;
            case "VVFIN":
            case "VVINF":
            case "FM":
                return 1.0;
            case "ART":
                return 0.5;
            default:
                return 0.8;
        }
    }
}
