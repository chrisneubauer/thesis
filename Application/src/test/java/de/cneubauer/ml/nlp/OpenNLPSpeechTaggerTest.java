package de.cneubauer.ml.nlp;

import de.cneubauer.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * Created by Christoph on 28.03.2017.
 * Test for OpenNLPSpeechTagger
 */
public class OpenNLPSpeechTaggerTest extends AbstractTest {
    private OpenNLPSpeechTagger tagger;
    private WordTokenizer tokenizer;

    @Before
    public void setUp() throws Exception {
        databaseChanged = false;
        this.tagger = new OpenNLPSpeechTagger();
        this.tokenizer = new WordTokenizer();
    }

    @After
    public void tearDown() throws Exception {
        this.tagger = null;
    }

    @Test
    public void tag() throws Exception {
        String[] tokens = tokenizer.tokenize("Das ist das Haus vom Nikolaus");
        String[] tags = tagger.tag(tokens);
        System.out.println(Arrays.toString(tags));
        Assert.notNull(tags);
    }
}