package de.cneubauer.ml.nlp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import static org.junit.Assert.*;

/**
 * Created by Christoph on 27.03.2017.
 */
public class WordTokenizerTest {
    private WordTokenizer tokenizer;

    @Before
    public void setUp() throws Exception {
        this.tokenizer = new WordTokenizer();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void tokenize() throws Exception {
        String position = "Grundgebuehr Surf und Fon Surf Flat";
        String[] result = this.tokenizer.tokenize(position);

        Assert.notNull(result);
        Assert.isTrue(result.length == 4);
    }

}