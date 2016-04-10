package com.cloudpocket.utils;

import com.cloudpocket.model.dto.FileDto;
import com.cloudpocket.model.enums.FilesOrder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Util class for sorting files list
 */
public class FilesSorter {

    /**
     * Sorts files list.
     * If {@code order} is not set files will be sorted by name.
     *
     * @param files
     *         files list
     * @param order
     *         specify criteria for sorting
     *         If criterion is not set, then will be used NAME criterion.
     * @param isReverse
     *         if {@code true} then sorts in descending mode, ascending otherwise.
     * @return sorted files list
     */
    public static List<FileDto> sortFiles(List<FileDto> files, FilesOrder order, boolean isReverse) {
        switch (order) {
            case NAME:
                Collections.sort(files, comparatorByName);
                break;
            case SIZE:
                Collections.sort(files, comparatorBySize);
                break;
            case TYPE:
                Collections.sort(files, comparatorByType);
                break;
            case DATE:
                Collections.sort(files, comparatorByDate);
                break;
            default:
                Collections.sort(files, comparatorByName);
        }

        if (isReverse) {
            Collections.reverse(files);
        }

        return files;
    }

    private static final Comparator<FileDto> comparatorByName = (o1, o2) -> {
        if (o1.isDirectory() != o2.isDirectory()) {
            if (o1.isDirectory()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return o1.getFilename().compareTo(o2.getFilename());
        }
    };

    private static final Comparator<FileDto> comparatorBySize = (o1, o2) -> {
        if (o1.isDirectory() != o2.isDirectory()) {
            if (o1.isDirectory()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (o1.isDirectory()) {
                return o1.getFilename().compareTo(o2.getFilename());
            } else {
                return Long.compare(o2.getSize(), o1.getSize());
            }
        }
    };

    private static final Comparator<FileDto> comparatorByType = (o1, o2) -> {
        if (o1.isDirectory() != o2.isDirectory()) {
            if (o1.isDirectory()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (o1.isDirectory()) {
                return o1.getFilename().compareTo(o2.getFilename());
            } else {
                return o1.getExtension().compareTo(o2.getExtension());
            }
        }
    };

    private static final Comparator<FileDto> comparatorByDate = (o1, o2) -> {
        if (o1.isDirectory() != o2.isDirectory()) {
            if (o1.isDirectory()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return Long.compare(o2.getCreationDateLong(), o1.getCreationDateLong());
        }
    };

}
