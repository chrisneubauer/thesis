package de.cneubauer.ml;

import de.cneubauer.AbstractTest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 03.02.2017.
 * Test for the ModelReader class
 */
public class ModelReaderTest extends AbstractTest {
    private ModelReader reader;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        databaseChanged = false;
        this.reader = new ModelReader();
    }

    @After
    public void tearDown() throws Exception {
        this.reader = null;
    }

    @Test
    public void getModels() throws Exception {
        long time = System.currentTimeMillis();
        List<Model> models = this.reader.getModels();
        long time2 = System.currentTimeMillis();

        Assert.isTrue(models.size() > 0);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Time taken to read " + models.size() + " models: " + (time2 - time) + " ms.");
    }

}