package com.cloudpocket.services;

import com.cloudpocket.model.dto.FileDto;
import com.cloudpocket.utils.FSUtils;
import com.cloudpocket.utils.FilesSorter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilesService {

    @Value("${cloudpocket.storage}")
    public String PATH_TO_STORAGE;

    /**
     * Returns files list in given user's directory.
     *
     * @param login
     *         user's login
     * @param path
     *         path to directory
     * @param order
     *         sort order
     *         Valid values are: 'name', 'size', 'type', 'date'
     *         If value is wrong or not set, then used order by 'name'.
     * @param isReverse
     *         if {@code true} then sorts in descending mode, ascending otherwise.
     * @return list of files in given directory
     * @throws FileNotFoundException
     *         if given directory doesn't exist
     * @throws IOException
     *         if error occurs while reading data from the file system
     */
    public List<FileDto> listFiles(String login, String path, String order, Boolean isReverse) throws IOException {
        Path absolutePath = getAbsolutePath(login, path);

        List<FileDto> files = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(absolutePath)) {
            for (Path item : directoryStream) {
                files.add(FSUtils.getFileCommonInfoFromPath(item));
            }
        }

        return FilesSorter.sortFiles(files, order, isReverse);
    }

    /**
     * Copy files from one directory to another.
     * Keeps files attributes.
     *
     * @param login
     *         user's login
     * @param fromDirectory
     *         path to directory copy from
     * @param toDirectory
     *         path to directory copy to
     * @param files
     *         list of files names to copy
     * @param replaceIfExist
     *         if {@code true} then files with same name in target directory will be replaced
     * @return number of copied files
     * @throws IOException
     *         if an error occurs while copying files
     */
    public int copyFiles(String login,
                         String fromDirectory,
                         String toDirectory,
                         String[] files,
                         boolean replaceIfExist) throws IOException {
        Path pathFrom = getAbsolutePath(login, fromDirectory);
        Path pathTo = getAbsolutePath(login, toDirectory);

        return FSUtils.copyFiles(files, pathFrom, pathTo, replaceIfExist);
    }

    /**
     * Moves given files.
     *
     * @param login
     *         user's login
     * @param fromDirectory
     *         path to directory copy from
     * @param toDirectory
     *         path to directory copy to
     * @param files
     *         list of files names to copy
     * @param replaceIfExist
     *         if {@code true} then file with the same name in target directory will be replaced
     * @return number of moved files
     * @throws IOException
     *         if an error occurs while moving files
     */
    public int moveFiles(String login,
                         String fromDirectory,
                         String toDirectory,
                         String[] files,
                         boolean replaceIfExist) throws IOException {
        Path pathFrom = getAbsolutePath(login, fromDirectory);
        Path pathTo = getAbsolutePath(login, toDirectory);

        return FSUtils.moveFiles(files, pathFrom, pathTo, replaceIfExist);
    }

    /**
     * Renames given file.
     *
     * @param login
     *         user's login
     * @param path
     *         path to directory, which contains file to rename
     * @param oldName
     *         actual name of file for renaming
     * @param newName
     *         new file name
     * @throws IOException
     *         if an error occurs while renaming file
     */
    public void rename(String login,
                       String path,
                       String oldName,
                       String newName) throws IOException {
        Path oldPath = getAbsolutePath(login, path).resolve(oldName);
        Files.move(oldPath, oldPath.resolveSibling(newName));
    }

    /**
     * Deletes given files and directories.
     *
     * @param login
     *         user's login
     * @param path
     *         path to directory, which contains file to delete
     * @param files
     *         files names for deletion
     * @return number of deleted files
     * @throws FileNotFoundException
     *         if directory which must contains files doesn't exist
     */
    public Integer deleteFiles(String login, String path, String[] files) throws FileNotFoundException {
        Path absolutePath = getAbsolutePath(login, path);
        return FSUtils.deleteFiles(absolutePath, files);
    }

    /**
     * Creates directory.
     *
     * @param login
     *         user's login
     * @param path
     *         absolute path to directory in which new directory must be created
     * @param directoryName
     *         name of new directory
     * @throws FileNotFoundException
     *         if directory in which new directory must be created doesn't exist
     * @throws FileAlreadyExistsException
     *         if directory with given name already exist
     * @throws IOException
     *         if error occurs with new directory creation
     */
    public void createDirectory(String login, String path, String directoryName) throws IOException {
        Path absolutePath = getAbsolutePath(login, path);
        FSUtils.createDirectory(absolutePath, directoryName);
    }

    /**
     * Returns absolute path on server's file system to given user's directory.
     *
     * @param login
     *         user's login
     * @param path
     *         absolute path inside user's home directory
     * @return absolute path on server's file system
     * @throws FileNotFoundException
     *         if path does not exist
     */
    private Path getAbsolutePath(String login, String path) throws FileNotFoundException {
        Path absolutePath = Paths.get(PATH_TO_STORAGE, login, path);
        if (! Files.exists(absolutePath)) {
            throw new FileNotFoundException("Wrong path");
        }
        return absolutePath;
    }

}
