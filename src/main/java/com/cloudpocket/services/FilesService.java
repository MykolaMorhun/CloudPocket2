package com.cloudpocket.services;

import com.cloudpocket.exceptions.ForbiddenException;
import com.cloudpocket.model.FileDetails;
import com.cloudpocket.model.enums.ArchiveType;
import com.cloudpocket.model.dto.FileDto;
import com.cloudpocket.model.enums.FilesOrder;
import com.cloudpocket.utils.FSUtils;
import com.cloudpocket.utils.FilesSorter;
import com.cloudpocket.utils.StreamUtils;
import com.cloudpocket.utils.ZipUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilesService {

    private static final int SEARCH_MAX_RESULTS = 250;

    @Value("${cloudpocket.storage}")
    private String PATH_TO_STORAGE;

    /**
     * Creates home dir for new user.
     * Adds welcome file into it.
     *
     * @param login
     *         new user's login
     * @throws IOException
     *         when a problem occurs with files operations
     */
    public void addNewAccount(String login) throws IOException {
        Path userHomeDir = Paths.get(PATH_TO_STORAGE, login);
        if (Files.exists(userHomeDir)) {
            FSUtils.delete(userHomeDir);
        }
        Files.createDirectories(userHomeDir);

        copyWelcomeFilesIntoNewAccount(login);
    }

    /**
     * Creates welcome files in user's home directory.
     *
     * @param login
     *         new user's login
     */
    private void copyWelcomeFilesIntoNewAccount(String login) {
        Path userHomeDir = Paths.get(PATH_TO_STORAGE, login);
        try {
            FSUtils.copyFileFromJar("welcome/welcome.txt", userHomeDir.resolve("welcome.txt"));
        } catch (IOException ignore) {
            // we shouldn't break registration if coping of welcome files fails
        }
    }

    /**
     * Deletes user's files from server file system.
     *
     * @param login
     *        user's login
     */
    public void deleteUserData(String login) {
        try {
            FSUtils.delete(Paths.get(PATH_TO_STORAGE, login));
        } catch (IOException e) {
            // TODO log errors
        }
    }

    /**
     * Returns files list in given user's directory.
     *
     * @param login
     *         user's login
     * @param path
     *         path to directory
     * @param sortOrder
     *         sort order for returning file list
     * @param isReverse
     *         if {@code true} then sorts in descending mode, ascending otherwise.
     * @return list of files in given directory
     * @throws FileNotFoundException
     *         if given directory doesn't exist
     * @throws IOException
     *         if error occurs while reading data from the file system
     */
    public List<FileDto> listFiles(String login,
                                   String path,
                                   FilesOrder sortOrder,
                                   boolean isReverse) throws IOException {
        Path absolutePath = getAbsolutePath(login, path);

        List<FileDto> files = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(absolutePath)) {
            for (Path item : directoryStream) {
                files.add(FSUtils.getFileCommonInfoFromPath(item));
            }
        }

        return FilesSorter.sortFiles(files, sortOrder, isReverse);
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
     * @throws FileNotFoundException
     *         if file in specified location doesn't exist
     * @throws IOException
     *         if an error occurs while renaming file
     */
    public void rename(String login,
                       String path,
                       String oldName,
                       String newName) throws IOException {
        Path oldPath = getAbsolutePath(login, path).resolve(oldName);
        if (Files.exists(oldPath)) {
            Files.move(oldPath, oldPath.resolveSibling(newName));
        } else {
            throw new FileNotFoundException();
        }
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
    public int deleteFiles(String login, String path, String[] files) throws FileNotFoundException {
        Path absolutePath = getAbsolutePath(login, path);
        return FSUtils.deleteFiles(absolutePath, files);
    }

    /**
     * Creates an archive.
     *
     * @param login
     *         user's login
     * @param path
     *         path to directory with target item (either files and directories)
     * @param files
     *         name of files and directories
     * @param archiveName
     *         name of the archive
     * @param archiveType
     *         type of the archive, e.g. 'ZIP', 'RAR' etc.
     * @return number of added items (subdirectories and files in them, do not counts)
     * @throws IOException
     *         if an error occurred while creating the archive
     * @throws IllegalArgumentException
     *         if given unsupported archive type
     */
    public int createArchive(String login,
                             String path,
                             String[] files,
                             String archiveName,
                             ArchiveType archiveType) throws IOException {
        Path absolutePath = getAbsolutePath(login, path);

        switch (archiveType) {
            case ZIP:
                return ZipUtils.zip(absolutePath, files, archiveName);
            case RAR:
                throw new UnsupportedOperationException();
            default:
                throw new IllegalArgumentException("Unsupported archive type");
        }
    }

    /**
     * Uncompress given archive.
     *
     * @param login
     *         user's login
     * @param path
     *         absolute path to directory, where the archive is located
     * @param archiveName
     *         name of the archive to uncompress
     * @param archiveType
     *         type of the archive, e.g. 'ZIP', 'RAR' etc.
     *         (for now only 'zip' is supported)
     * @param extractIntoSubdirectory
     *         if {@code true} archive will be extracted into a separate directory
     * @throws IOException
     *         if error occurs while uncompressing the archive
     * @throws IllegalArgumentException
     *         if given unsupported archive type or archive is damaged
     */
    public void uncompressArchive(String login,
                                  String path,
                                  String archiveName,
                                  ArchiveType archiveType,
                                  boolean extractIntoSubdirectory) throws IOException {
        Path absolutePath = getAbsolutePath(login, path);

        switch (archiveType) {
            case ZIP:
                ZipUtils.unzip(absolutePath, archiveName, extractIntoSubdirectory);
                break;
            case RAR:
                throw new UnsupportedOperationException();
            default:
                throw new IllegalArgumentException("Unsupported archive type");
        }
    }

    /**
     * Creates directory.
     *
     * @param login
     *         user's login
     * @param path
     *         absolute path to directory in that new directory must be created
     * @param directoryName
     *         name of new directory
     * @throws FileNotFoundException
     *         if parent directory doesn't exist
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
     * Sends file to user.
     * I.e. user download this file.
     *
     * @param pathToFile
     *         absolute path to file for download
     * @param outputStream
     *         stream to write file to
     * @throws FileNotFoundException
     *         if file to download doesn't exist
     *         or directory which must contains the file doesn't exist
     * @throws IOException
     *         if an error occurs while sending file
     */
    public void downloadFile(Path pathToFile, OutputStream outputStream) throws IOException {
        StreamUtils.writeFileToOutputStream(pathToFile, outputStream);
    }

    /**
     * Saves file which was send to server by user.
     *
     * @param login
     *         user's login
     * @param path
     *         absolute path to directory in which file must be saved
     * @param name
     *         name of the file
     * @param file
     *         file content
     * @return absolute path on server's file system to received file
     * @throws IOException
     *         if an error occurs while receiving and saving file
     */
    public Path uploadFile(String login, String path, String name, InputStream file) throws IOException {
        Path absolutePath = getAbsolutePath(login, path).resolve(name);
        StreamUtils.writeInputStreamToFile(absolutePath, file);
        return absolutePath;
    }

    /**
     * Saves data from archive.
     *
     * @param login
     *         user's login
     * @param path
     *         absolute path to directory in which file structure must be saved
     * @param file
     *         archive with file structure
     * @param skipSubfolder
     *         if {@code false} then save file structure directly in the specified directory
     * @throws IllegalArgumentException
     *         if given file isn't valid zip archive
     * @throws IOException
     *         if an error occurs while receiving and saving archive
     */
    public void uploadFileStructure(String login,
                                    String path,
                                    String name,
                                    InputStream file,
                                    boolean skipSubfolder) throws IOException {
            Path absolutePathToArchive = uploadFile(login, path, name, file);
            ZipUtils.unzip(absolutePathToArchive.getParent(), name, !skipSubfolder);
            Files.delete(absolutePathToArchive);
    }

    /**
     * Searches for files and directories.
     * Returns map in which:
     *  key is path to found item,
     *  value is information about this item
     *
     * @param login
     *         user's login
     * @param path
     *         absolute path to directory to search in
     * @param namePattern
     *         Name pattern for match files and directories.
     *         * and ? are possible wildcards.
     *         Just part of a file name is correct
     * @param isCaseSensitive
     *         if {@code true} then search will be case sensitive
     * @param isExactMatch
     *         if {@code true} then only files with full match in name will be returned,
     *         if {@code false} then files which names contain search pattern will be returned
     * @param recursive
     *         if {@code false} then search for files and directories
     *         exactly inside given directory, no recursive search
     * @param maxResults
     *         maximal number of results.
     *         If it exceed then search stops and returns result.
     *         Returns no more than 50 results by default
     * @return search results
     * @throws FileNotFoundException
     *         if directory to search in doesn't exist
     * @throws IOException
     *         if errors occurs while reading file system
     * @throws IllegalArgumentException
     *         if {@code maxResults} exceeds {@code SEARCH_MAX_RESULTS}
     */
    public Map<String, FileDto> search(String login,
                                       String path,
                                       String namePattern,
                                       boolean isCaseSensitive,
                                       boolean isExactMatch,
                                       boolean recursive,
                                       int maxResults) throws IOException {
        if (maxResults < 1 || maxResults > SEARCH_MAX_RESULTS) {
            throw new IllegalArgumentException("Max search result parameter should be positive and less then " +
                                               SEARCH_MAX_RESULTS);
        }
        Path absolutePath = getAbsolutePath(login, path);
        Map <Path, FileDto> searchResult;
        if (recursive) {
            searchResult = FSUtils.searchRecursively(absolutePath,
                    namePattern,
                    isCaseSensitive,
                    isExactMatch,
                    maxResults);
        } else {
            searchResult = FSUtils.searchInsideDirectoryOnly(absolutePath,
                    namePattern,
                    isCaseSensitive,
                    isExactMatch,
                    maxResults);
        }

        Map <String, FileDto> results = new HashMap<>();
        for (Map.Entry<Path, FileDto> entry : searchResult.entrySet()) {
            results.put(getPathInsideUserHomeDirectory(entry.getKey(), login), entry.getValue());
        }
        return results;
    }

    /**
     * Returns information about file system entry.
     *
     * @param login
     *         user's login
     * @param path
     *         path to directory with given file
     * @param name
     *         name of a file
     * @return basic information about file system entry
     * @throws FileNotFoundException
     *         if specified file doesn't exist
     * @throws IOException
     */
    public FileDto getFileInfo(String login, String path, String name) throws IOException {
        Path absolutePath = getAbsolutePath(login, path);
        Path pathToFile = absolutePath.resolve(name);
        if (Files.exists(pathToFile)) {
            return FSUtils.getFileCommonInfoFromPath(pathToFile);
        }
        throw new FileNotFoundException("File '" + name + "' doesn't exist.");
    }

    /**
     * Returns detailed information about file system entry.
     *
     * @param login
     *         user's login
     * @param path
     *         path to directory with given file
     * @param name
     *         name of a file
     * @return detailed information about file system entry
     * @throws FileNotFoundException
     *         if specified file doesn't exist
     * @throws IOException
     */
    public FileDetails getDetailedFileInfo(String login, String path, String name) throws IOException {
        Path absolutePath = getAbsolutePath(login, path);
        Path pathToFile = absolutePath.resolve(name);
        if (Files.exists(pathToFile)) {
            FileDetails fileDetails = FSUtils.getDetailedInfoFromPath(pathToFile);
            fileDetails.setOwner(login);
            fileDetails.setPath(getPathInsideUserHomeDirectory(Paths.get(fileDetails.getPath()), login));
            return fileDetails;
        }
        throw new FileNotFoundException("File '" + name + "' doesn't exist");
    }

    /**
     * Returns absolute path to user's file.
     *
     * @param login
     *         user's login
     * @param path
     *         absolute path inside user's home directory
     * @param filename
     *         name of file
     * @return absolute path on server's file system to specified file
     * @throws FileNotFoundException
     *         if specified files doesn't exist
     * @throws ForbiddenException
     *         if path exits from user's home directory
     */
    public Path getAbsolutePathToFile(String login, String path, String filename) throws FileNotFoundException {
        return pathInsideUserHomeDirectory(login, path + '/' + filename);
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
     * @throws ForbiddenException
     *         if path exits from user's home directory. E.g. "userhome/../../"
     */
    private Path getAbsolutePath(String login, String path) throws FileNotFoundException {
        Path absolutePath = pathInsideUserHomeDirectory(login, path);
        if (! Files.exists(absolutePath)) {
            throw new FileNotFoundException("Wrong path");
        }
        return absolutePath;
    }

    /**
     * Returns path to given file or folder relatively to user's home directory.
     * This method is the inverse of {@link #getAbsolutePath(String, String)}
     *
     * @param absolutePath
     *         absolute path on server's file system to a file
     *         inside user's home directory
     * @param login
     *         user's login
     * @return absolute path inside user's home directory
     */
    private String getPathInsideUserHomeDirectory(Path absolutePath, String login) {
        return "/" + Paths.get(PATH_TO_STORAGE, login).relativize(absolutePath).toString();
    }

    /**
     * Checks that absolute path points to a item inside user's home directory.
     * If it false, then {@link ForbiddenException} will be thrown.
     *
     * @param login
     *         user's login
     * @param path
     *         absolute path inside user home directory
     * @return absolute path on server's file system to given in {@code path} parameter item
     * @throws ForbiddenException
     *         if path exits from user's home directory. E.g. "path/userhome/../"
     */
    private Path pathInsideUserHomeDirectory(String login, String path) {
        Path userHomeDir = Paths.get(PATH_TO_STORAGE, login);
        Path absolutePath = Paths.get(PATH_TO_STORAGE + '/' + login + path).normalize();
        if (! absolutePath.startsWith(userHomeDir)) {
            throw new ForbiddenException();
        }
        return absolutePath;
    }

}
