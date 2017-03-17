package de.cneubauer.domain.helper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;


/**
 * Created by Christoph on 17.03.2017.
 * Test for DateHelper class
 */
public class DateHelperTest {
    private DateHelper helper;

    @Before
    public void setUp() throws Exception {
        this.helper = new DateHelper();
    }

    @After
    public void tearDown() throws Exception {
        this.helper = null;
    }

    @Test
    public void stringToDate() throws Exception {
        LocalDate date = this.helper.stringToDate("23.06.2007");
        Assert.assertTrue(date.getYear() == 2007);
        Assert.assertTrue(date.getMonthValue() == 6);
        Assert.assertTrue(date.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN).equals("Juni"));
        Assert.assertTrue(date.getDayOfMonth() == 23);
    }

}