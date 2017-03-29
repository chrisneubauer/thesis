package de.cneubauer.ml;

import de.cneubauer.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by Christoph Neubauer on 03.02.2017.
 * Test for LearningService class
 */
@Deprecated
public class LearningServiceTest extends AbstractTest {

    private LearningService service;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        databaseChanged = false;
        this.service = new LearningService();
    }

    @After
    public void tearDown() throws Exception {
        this.service = null;
    }

    @Test
    public void isModelExisting() throws Exception {
        ModelReader reader = new ModelReader();
        List<Model> models = reader.getModels();

        if (models.size() > 0) {
            for(Model m : models) {
                Assert.isTrue(this.service.isModelExisting(m));
            }
        }
    }

    @Test
    public void getMostLikelyModel() throws Exception {
        ModelReader reader = new ModelReader();
        List<Model> models = reader.getModels();

        if (models.size() > 0) {
            Model m = models.get(0);
            Model result = this.service.getMostLikelyModel(m.getPosition());
            Assert.isTrue(result != null);
        }

    }

}