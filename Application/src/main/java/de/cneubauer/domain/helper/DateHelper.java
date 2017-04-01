package de.cneubauer.domain.helper;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.time.LocalDate;

/**
 * Created by Christoph on 17.03.2017.
 * Convenience class for easy conversion between strings and dates
 */
public class DateHelper {
    /**
     * Converts a string containing date information to an int[] containing:
     * <ul>
     *     <li>[0]: the day</li>
     *     <li>[1]: the month</li>
     *     <li>[2]: the year</li>
     * </ul>
     * @param date the date that should be converted
     * @return the int[] containing the found values, null if there is no "." in the string
     */
    private int[] extractDateInformation(String date) {
        Logger.getLogger(this.getClass()).log(Level.INFO, "Trying to convert date: " + date);
        int[] result = new int[3];
        // first german locale
        if (date.contains(".")) {
            String[] dateValues = date.split("\\.");
            if (dateValues.length == 3) {
                // we expect german calendar writing style, so days are in the first row, then months, then years
                result[0] = Integer.parseInt(dateValues[0]);
                result[1] = Integer.parseInt(dateValues[1]);
                if (dateValues[2].length() > 2) {
                    result[2] = Integer.parseInt(dateValues[dateValues.length - 1]);
                }
            }
        } else if (date.contains("-")){
            // english locale
            String[] dateValues = date.split("\\-");
            if (dateValues.length == 3) {
                // we expect english calendar writing style, so months are in the first row, then days, then years
                result[1] = Integer.parseInt(dateValues[0]);
                result[0] = Integer.parseInt(dateValues[1]);
                if (dateValues[2].length() > 2) {
                    result[2] = Integer.parseInt(dateValues[dateValues.length - 1]);
                }
            };
        } else {
            return null;
        }
        return result;
    }

    /**
     * Converts a string to LocalDate
     * @param date the string that should be converted
     * @return the LocalDate of the given string, or EpochDay(0) if conversion not possible
     */
    public LocalDate stringToDate(String date) {
        int[] values = this.extractDateInformation(date);
        if (values != null) {
            return LocalDate.of(values[2], values[1], values[0]);
        } else {
            return LocalDate.ofEpochDay(0);
        }
    }
}
