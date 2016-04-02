package com.cloudpocket.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Util methods for working with file system
 */
public class FSUtils {

    /**
     * Deletes non empty directory on the file system.
     *
     * @param pathToFolder
     *         path to folder for deletion
     * @throws IOException
     *         if error occurs while deleting file or folder
     */
    public static void deleteDirectoryWithContent(Path pathToFolder) throws IOException {
        recursiveDelete(pathToFolder.toFile());
    }

    /**
     * Deletes given item from file system (either folder or file).
     *
     * @param itemForDelete
     *         file or folder for deletion
     * @throws IOException
     *         if delete fails
     */
    private static void recursiveDelete(File itemForDelete) throws IOException {
        if (itemForDelete == null) {
            throw new IllegalArgumentException();
        }

        if (itemForDelete.isDirectory()) {
            File[] files = itemForDelete.listFiles();
            if (files != null) {
                for (File file : files) {
                    recursiveDelete(file);
                }
            }
        }
        if (!itemForDelete.delete()) {
            throw new IOException("Failed to delete file: " + itemForDelete);
        }
    }

}
