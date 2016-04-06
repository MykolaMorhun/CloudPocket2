package com.cloudpocket.utils;

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

}
