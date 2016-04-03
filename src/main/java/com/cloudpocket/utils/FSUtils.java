package com.cloudpocket.utils;

import com.cloudpocket.model.dto.FileDto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Util methods for working with file system
 */
public class FSUtils {

    /**
     * Creates a directory.
     *
     * @param path
     *        absolute path where a directory will be created
     * @param folderName
     *        name of a new directory
     * @throws FileAlreadyExistsException
     *         if folder with given name already exist
     * @throws IOException
     *         if error occurs while creating a directory
     */
    public static void createDirectory(Path path, String folderName) throws IOException {
        Path newDirectoryPath = path.resolve(folderName);
        if (!Files.exists(newDirectoryPath)) {
            Files.createDirectory(newDirectoryPath);
        }
        throw new FileAlreadyExistsException(path + "/" + folderName);
    }

    /**
     * Deletes directory (may be non empty) or file from the file system.
     * If directory contains some files, they will be deleted.
     *
     * @param path
     *         path to folder or file for deletion
     * @throws IOException
     *         if error occurs while deleting file or folder
     */
    public static void delete(Path path) throws IOException {
        if (path != null) {
            if (Files.exists(path)) {
                recursiveDelete(path.toFile());
            } else {
                throw new FileNotFoundException(path.toString());
            }
        }
    }

    /**
     * Deletes given item from file system (either folder or file).
     *
     * @param itemForDelete
     *         file or folder for deletion
     * @throws IOException
     *         if deleteFiles fails
     * @throws IllegalArgumentException
     *         if argument is null
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
            throw new IOException("Failed to deleteFiles file: " + itemForDelete);
        }
    }

    /**
     * Copy files from one directory to another.
     * Keeps files attributes.
     *
     * @param files
     *         list of files to copy
     * @param pathFrom
     *         absolute path to directory copy from
     * @param pathTo
     *         absolute path to directory copy to
     * @param replaceIfExist
     *         if {@code true} then files with same name in target directory will be replaced
     * @return number of copied files
     * @throws IOException
     *         if error occurs while copying files
     */
    public static int copyFiles(String[] files, Path pathFrom, Path pathTo, boolean replaceIfExist) throws IOException {
        int counter = 0;
        for (String file : files) {
            try {
                if (replaceIfExist) {
                    Files.copy(pathFrom.resolve(file), pathTo.resolve(file), REPLACE_EXISTING, COPY_ATTRIBUTES);
                } else {
                    Files.copy(pathFrom.resolve(file), pathTo.resolve(file), COPY_ATTRIBUTES);
                }
                counter++;
            } catch (FileAlreadyExistsException ignore) {
                // if we shouldn't replace files, just skip copying of this file
            } catch (FileNotFoundException ignore) {
                // if file not found, just skip it
            }
        }
        return counter;
    }

    /**
     * Moves files from one directory to another.
     * Keeps files attributes.
     *
     * @param files
     *         list of files to move
     * @param pathFrom
     *         absolute path to source directory
     * @param pathTo
     *         absolute path to target directory
     * @param replaceIfExist
     *         if {@code true} then files with same name in target directory will be replaced
     * @return number of moved files
     * @throws IOException
     *         if error occurs while moving files
     */
    public static int moveFiles(String[] files, Path pathFrom, Path pathTo, boolean replaceIfExist) throws IOException {
        int counter = 0;
        for (String file : files) {
            try {
                if (replaceIfExist) {
                    Files.move(pathFrom.resolve(file), pathTo.resolve(file), REPLACE_EXISTING);
                } else {
                    Files.move(pathFrom.resolve(file), pathTo.resolve(file));
                }
                counter++;
            } catch (FileAlreadyExistsException ignore) {
                // if we shouldn't replace files, just skip moving of this file
            } catch (FileNotFoundException ignore) {
            // if file not found, just skip it
            }
        }
        return counter;
    }

    /**
     * Deletes given files from given directory.
     *
     * @param path
     *         absolute path from file will be deleted
     * @param files
     *         list of file to delete
     * @return number of deleted files (nested files doesn't counting)
     */
    public static int deleteFiles(Path path, String[] files) {
        int counter = 0;
        for (String file : files) {
            try {
                FSUtils.delete(path.resolve(file));
                counter++;
            } catch (IOException ignore) {
                // if we fail to delete a file, we shouldn't break deletion
            }
        }
        return counter;
    }

    /**
     * Retrieves basic information about given file.
     *
     * @param item
     *        file system's item (file or directory)
     * @return information about given file
     */
    public static FileDto getFileCommonInfoFromPath(Path item) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(item, BasicFileAttributes.class);

        String name = item.getFileName().toString();
        boolean isDirectory = attrs.isDirectory();
        long size = Files.size(item);
        long creationTime = attrs.creationTime().toMillis();

        return new FileDto(name, isDirectory, size, creationTime);
    }

}
