package de.cneubauer.ml;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * Created by Christoph on 20.03.2017.
 * Test for DictionaryHelper class
 */
public class DictionaryHelperTest {
    private DictionaryHelper dictionaryHelper;

    @Before
    public void setUp() throws Exception {
        this.dictionaryHelper = new DictionaryHelper();
    }

    @After
    public void tearDown() throws Exception {
        this.dictionaryHelper = null;
    }

    @Test
    public void replaceValuesFromDictionary() throws Exception {
        String result = this.dictionaryHelper.replaceValuesFromDictionary("Äpfel");
        Assert.notNull(result);
        Assert.isTrue(result.equals("Apfel"));
    }

}