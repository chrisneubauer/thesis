package de.cneubauer.ml.nlp;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph on 28.03.2017.
 * Enables usage of the NLP algorithm with a single call
 * Internally loads models, calculates probability values and returns the most likely model
 */
public class NLPFacade {
    public NLPModel getMostLikelyModel(String position) {
        if (position != null) {
            NLPFileHelper fileHelper = new NLPFileHelper();
            List<NLPModel> models = fileHelper.getModels();
            WordTokenizer tokenizer = new WordTokenizer();
            String[] tokens = tokenizer.tokenize(position);
            models = this.filterRelevantModels(models, tokens);
            NLPClassificator classificator = new NLPClassificator(models);
            List<NLPModel> valuedModels = classificator.classify(tokens);
            return this.getHighestProbabilityModel(valuedModels);
        }
        return null;
    }

    private NLPModel getHighestProbabilityModel(List<NLPModel> valuedModels) {
        double max = 0;
        int idxOfHighest = -1;
        for (NLPModel m : valuedModels) {
            if (m.getProbability() > max) {
                max = m.getProbability();
                idxOfHighest = valuedModels.indexOf(m);
            }
        }
        if (idxOfHighest >= 0) {
            return valuedModels.get(idxOfHighest);
        } else {
            return null;
        }
    }

    private List<NLPModel> filterRelevantModels(List<NLPModel> models, String[] tokens) {
        List<NLPModel> result = new LinkedList<>();
        for (NLPModel m : models) {
            for (String token : tokens) {
                if (m.getValues().keySet().contains(token)) {
                    result.add(m);
                    break;
                }
            }
        }
        return result;
    }
}
