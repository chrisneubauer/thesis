package de.cneubauer.ml.nlp;

import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by Christoph on 28.03.2017.
 */
public class OpenNLPSpeechTaggerTest {
    @Test
    public void tag() throws Exception {
        OpenNLPSpeechTagger tagger = new OpenNLPSpeechTagger();
        WordTokenizer tokenizer = new WordTokenizer();
        String[] tokens = tokenizer.tokenize("Das ist das Haus vom Nikolaus");
        String[] tags = tagger.tag(tokens);
        System.out.println(Arrays.toString(tags));
        Assert.notNull(tags);
    }

}