package com.cloudpocket.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Common utils.
 */
public class Utils {

    /**
     * Simple helper method for return defaults if a parameter is null.
     *
     * @param value
     *         value to check for {@code null}
     * @param alternative
     *         this value returns when first argument is null
     * @param <T>
     *         type of an value and alternative
     * @return the first argument if it non null and the second argument otherwise
     */
    public static <T> T firstIfNotNull(T value, T alternative) {
        if (value != null) {
            return value;
        }
        return alternative;
    }

    /**
     * Returns current timestamp formatted by given formatter.
     *
     * @param dateFormat
     *         formatter which should format current date/time
     *         or null for default format
     * @return current date in specified format
     */
    public static String getCurrentDateTime(SimpleDateFormat dateFormat) {
        if (dateFormat == null) {
            return new Date().toString();
        }
        return dateFormat.format(new Date());
    }

    private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
    /**
     * The same as {@link #getCurrentDateTime(SimpleDateFormat)}, but uses predefined date format.
     * @return current date and time in format: <i>year-month-day hour-minute-second</i>
     */
    public static String getCurrentDateTime() {
        return DEFAULT_DATE_FORMAT.format(new Date());
    }

}
