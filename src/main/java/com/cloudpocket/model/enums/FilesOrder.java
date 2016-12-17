package com.cloudpocket.model.enums;

/**
 * Describes criteria of sorting files inside directory.
 */
public enum FilesOrder {
    NAME,
    TYPE,
    SIZE,
    CREATION_DATE;

    public static FilesOrder thisOrDefault(String sortOrder) {
        FilesOrder order;
        try {
            order = valueOf(sortOrder.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            order = NAME;
        }
        return order;
    }

}
