package de.cneubauer.ml.nlp;

import de.cneubauer.domain.bo.AccountPosition;
import de.cneubauer.domain.bo.Position;
import de.cneubauer.ml.DictionaryHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph on 28.03.2017.
 * Enables usage of the NLP algorithm with a single call
 * Internally loads models, calculates probability values and returns the most likely model
 */
public class NLPFacade {
    private NLPFileHelper fileHelper;

    public NLPModel getMostLikelyModel(String position) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "Searching in models for position " + position);
        if (position != null) {
            if (this.fileHelper == null) {
                this.fileHelper = new NLPFileHelper();
            }
            List<NLPModel> models = fileHelper.getModels();

            DictionaryHelper dictionaryHelper = new DictionaryHelper();
            String replacedString = dictionaryHelper.replaceValuesFromDictionary(position);

            WordTokenizer tokenizer = new WordTokenizer();
            String[] tokens = tokenizer.tokenize(replacedString);
            models = this.filterRelevantModels(models, tokens);
            NLPClassificator classificator = new NLPClassificator(models);
            List<NLPModel> valuedModels = classificator.classify(tokens);
            return this.getHighestProbabilityModel(valuedModels);
        }
        return null;
    }

    public void writeModel(Position pos) {
        if (this.fileHelper == null) {
            this.fileHelper = new NLPFileHelper();
        }
        NLPModel model = new NLPModel();

        WordTokenizer tokenizer = new WordTokenizer();
        String[] tokens = tokenizer.tokenize(pos.getEntryText());

        for (String key : tokens) {
            model.add(key);
        }


        double total = 0;

        for (AccountPosition ar : pos.getPositionAccounts()) {
            if (ar.getIsDebit()) {
                total += ar.getBruttoValue();
            }
        }

        for (AccountPosition ar : pos.getPositionAccounts()) {
            if (ar.getIsDebit()) {
                model.addToDebitAccounts(ar.getAccount(), (ar.getBruttoValue() / total));
            } else {
                model.addToCreditAccounts(ar.getAccount(), (ar.getBruttoValue() / total));
            }
        }

        this.fileHelper.writeToFile(model);
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
