package de.cneubauer.domain.helper;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.time.*;

/**
 * Created by Christoph on 17.03.2017.
 * Convenience class for easy conversion between strings and dates
 */
public class DateHelper {

    /**
     * Converts a string containing date information to a Calendar object
     * @param date  the date that should be converted
     * @return  the Calendar object with the given date
     */
    private Calendar convertStringToCalendar(String date) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "Trying to convert date: " + date);
        Calendar cal = new Calendar.Builder().build();
        if (date.contains(".")) {
            String[] dateValues = date.split("\\.");
            if (dateValues.length == 3) {
                // we expect german calendar writing style, so days are in the first row, then months, then years
                // TODO: Do internationalization as a setting in the application
                if (dateValues[2].length() > 2) {
                    cal.set(Calendar.YEAR, Integer.parseInt(dateValues[dateValues.length - 1]));
                }
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateValues[0]));
                cal.set(Calendar.MONTH, Integer.parseInt(dateValues[1]));
            }
        }
        return cal;
    }

    public LocalDate stringToDate(String date) {
        Calendar cal = this.convertStringToCalendar(date);
        return LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }
}
