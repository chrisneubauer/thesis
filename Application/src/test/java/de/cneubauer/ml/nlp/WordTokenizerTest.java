package de.cneubauer.ml.nlp;

import de.cneubauer.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * Created by Christoph on 27.03.2017.
 * Test for WordTokenizer
 */
public class WordTokenizerTest extends AbstractTest {
    private WordTokenizer tokenizer;

    @Before
    public void setUp() throws Exception {
        databaseChanged = false;
        this.tokenizer = new WordTokenizer();
    }

    @After
    public void tearDown() throws Exception {
        this.tokenizer = null;
    }

    @Test
    public void tokenize() throws Exception {
        String position = "Grundgebuehr Surf und Fon Surf Flat";
        String[] result = this.tokenizer.tokenize(position);

        Assert.notNull(result);
        Assert.isTrue(result.length == 5);
    }

}