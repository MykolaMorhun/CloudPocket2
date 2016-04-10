package com.cloudpocket.model.enums;

/**
 * Describes criteria of sorting files inside directory.
 */
public enum FilesOrder {
    NAME,
    TYPE,
    SIZE,
    DATE;

    public static FilesOrder thisOrDefault(String sortOrder) {
        FilesOrder order;
        try {
            order = valueOf(sortOrder);
        } catch (IllegalArgumentException | NullPointerException e) {
            order = NAME;
        }
        return order;
    }

}
