package com.cloudpocket.services;

import com.cloudpocket.model.dto.FileDto;
import com.cloudpocket.utils.FSUtils;
import com.cloudpocket.utils.FilesSorter;
import com.cloudpocket.utils.ZipUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cloudpocket.utils.StreamUtils.writeFileToOutputStream;
import static com.cloudpocket.utils.Utils.firstIfNotNull;

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

        return FilesSorter.sortFiles(files, order, firstIfNotNull(isReverse, false));
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
                         Boolean replaceIfExist) throws IOException {
        Path pathFrom = getAbsolutePath(login, fromDirectory);
        Path pathTo = getAbsolutePath(login, toDirectory);

        return FSUtils.copyFiles(files, pathFrom, pathTo, firstIfNotNull(replaceIfExist, false));
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
                         Boolean replaceIfExist) throws IOException {
        Path pathFrom = getAbsolutePath(login, fromDirectory);
        Path pathTo = getAbsolutePath(login, toDirectory);

        return FSUtils.moveFiles(files, pathFrom, pathTo, firstIfNotNull(replaceIfExist, false));
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
     *         type of the archive, e.g. 'zip', 'rar' etc.
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
                             String archiveType) throws IOException {
        Path absolutePath = getAbsolutePath(login, path);

        if (archiveName == null) {
            archiveName = new Date().toString();
        }
        if (archiveType == null) {
            archiveType = "zip";
        }

        switch (archiveType) {
            case "zip":
                return ZipUtils.zip(absolutePath, files, archiveName);
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
     *         type of the archive, e.g. 'zip', 'rar' etc.
     *         (for now only 'zip' is supported)
     * @param extractIntoSubdirectory
     *         if {@code true} archive will be extracted into a separate directory
     * @throws IOException
     *         if error occurs while uncompressing the archive
     * @throws IllegalArgumentException
     *         if given unsupported archive type
     */
    public void uncompressArchive(String login,
                                  String path,
                                  String archiveName,
                                  String archiveType,
                                  Boolean extractIntoSubdirectory) throws IOException {
        Path absolutePath = getAbsolutePath(login, path);

        switch (archiveType) {
            case "zip":
                ZipUtils.unzip(absolutePath, archiveName, firstIfNotNull(extractIntoSubdirectory, true));
                break;
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
     * @param login
     *         user's login
     * @param path
     *         absolute path to directory in which file is located
     * @param file
     *         file to download
     * @param response
     *         http servlet response
     * @throws IOException
     * @throws FileNotFoundException
     *         if file to download doesn't exist
     *         or directory which must contains the file doesn't exist
     */
    public void downloadFile(String login, String path, String file, HttpServletResponse response) throws IOException {
        Path absolutePath = getAbsolutePath(login, path);

        Path pathToFile = absolutePath.resolve(file);
        if (Files.exists(pathToFile)) {
            response.setContentType(Files.probeContentType(pathToFile));
            response.setHeader("Content-disposition", "attachment; filename=\"" + pathToFile.getFileName() + "\"");
            response.setContentLengthLong(Files.size(pathToFile));
            writeFileToOutputStream(pathToFile, response.getOutputStream());
        } else {
            throw new FileNotFoundException();
        }
    }

    /**
     * Sends files in zip archive to user.
     *
     * @param login
     *         user's login
     * @param path
     *         absolute path to directory in which files are located
     * @param files
     *         list of file to download
     * @param response
     *         http servlet response
     * @return number of items in the archive root
     * @throws IOException
     */
    public void downloadFilesInArchive(String login,
                                      String path,
                                      String[] files,
                                      HttpServletResponse response) throws IOException {
        Path absolutePath = getAbsolutePath(login, path);
        String archiveName = String.valueOf(new Date().getTime());
        ZipUtils.zip(absolutePath, files, archiveName);
        Path pathToArchive = absolutePath.resolve(archiveName);
        response.setContentType("application/zip");
        response.setHeader("Content-disposition", "attachment; filename=\"" + archiveName + ".zip\"");
        response.setContentLengthLong(Files.size(pathToArchive));
        writeFileToOutputStream(pathToArchive, response.getOutputStream());
        FSUtils.delete(pathToArchive);
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
