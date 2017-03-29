package de.cneubauer.ml.nlp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Christoph on 28.03.2017.
 * Test for NLPFacade
 */
public class NLPFacadeTest {
    private NLPFacade facade;

    @Before
    public void setUp() throws Exception {
        this.facade = new NLPFacade();
    }

    @Test
    public void getMostLikelyModel() throws Exception {
        NLPModel model = this.facade.getMostLikelyModel("Surf&Fon Flat Büro");
        Assert.assertTrue(model != null);
        model = this.facade.getMostLikelyModel("Kartoffel");
        Assert.assertTrue(model == null);
    }

}