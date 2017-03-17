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
    public void testStringToDate() throws Exception {
        LocalDate date = this.helper.stringToDate("23.06.2007");
        Assert.assertTrue(date.getYear() == 2007);
        Assert.assertTrue(date.getMonthValue() == 6);
        Assert.assertTrue(date.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN).equals("Juni"));
        Assert.assertTrue(date.getDayOfMonth() == 23);
    }


    @Test
    public void testJanuaryDate() throws Exception {
        LocalDate date = this.helper.stringToDate("31.01.2002");
        Assert.assertTrue(date.getYear() == 2002);
        Assert.assertTrue(date.getMonthValue() == 1);
        Assert.assertTrue(date.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN).equals("Januar"));
        Assert.assertTrue(date.getDayOfMonth() == 31);
    }


    @Test
    public void testDecemberDate() throws Exception {
        LocalDate date = this.helper.stringToDate("31.12.2008");
        Assert.assertTrue(date.getYear() == 2008);
        Assert.assertTrue(date.getMonthValue() == 12);
        Assert.assertTrue(date.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN).equals("Dezember"));
        Assert.assertTrue(date.getDayOfMonth() == 31);
    }

    @Test
    public void testFebruaryDate() throws Exception {
        LocalDate date = this.helper.stringToDate("28.02.2009");
        Assert.assertTrue(date.getYear() == 2009);
        Assert.assertTrue(date.getMonthValue() == 2);
        Assert.assertTrue(date.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN).equals("Februar"));
        Assert.assertTrue(date.getDayOfMonth() == 28);
    }
}