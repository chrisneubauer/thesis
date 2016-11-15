package de.cneubauer.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Christoph Neubauer on 15.11.2016.
 */
public class SQLGeneratorTest {
    @Test
    public void createAccountSQL() throws Exception {
        SQLGenerator generator = new SQLGenerator();
        generator.createAccountSQL();
    }

}