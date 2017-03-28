package de.cneubauer.ml.nlp;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Christoph on 28.03.2017.
 * Uses opennlp maximum entropy model to do part of speech tagging
 */
public class OpenNLPSpeechTagger {

    public String[] tag(String[] tokens) {
        Tokenizer tokenizer = null;

        InputStream modelIn = null;
        try {
            // Loading tokenizer model
            modelIn = getClass().getResourceAsStream("/opennlp/de-pos-maxent.bin");
            POSModel posModel = new POSModel(modelIn);
            modelIn.close();

            POSTagger posTagger = new POSTaggerME(posModel);
            return posTagger.tag(tokens);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (final IOException ignored) {}
            }
        }
        return null;
    }
}
