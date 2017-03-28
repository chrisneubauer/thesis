package de.cneubauer.ml.nlp;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * Created by Christoph on 27.03.2017.
 * OpenNLP implementation class
 * Splits words into tokens
 */
public class WordTokenizer {

    public String[] tokenize(String position) {
        position = this.preprocess(position);
        return position.split(" ");
    }

    private String preprocess(String position) {
        String cleaned = position;
        cleaned = cleaned.replace(" und ", " ");
        cleaned = cleaned.replace(" oder ", " ");
        return cleaned;
    }

}
