package com.cloudpocket.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Util class for work with zip archives.
 */
public class ZipUtils {

    /**
     * Compress given files to zip archive.
     *
     * @param path
     *         path where files to zip is located
     * @param files
     *         names of files to zip
     * @param zipName
     *         archive name
     * @return number of item, which was added to the archive (only top level item counting)
     * @throws IOException
     *         if an fatal error occurs while creating archive
     */
    public static int zip(Path path, String[] files, String zipName) throws IOException {
        int counter = 0;
        try {
            ZipFile zipFile = new ZipFile(path + "/" + zipName);

            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            File file;
            for (String fileName : files) {
                try {
                    file = path.resolve(fileName).toFile();
                    if (file.isDirectory()) {
                        zipFile.addFolder(file, parameters);
                    } else {
                        zipFile.addFile(file, parameters);
                    }
                    counter++;
                } catch (ZipException ignore) {
                    // ignore when we fail to add some file to the archive
                }
            }
        } catch (ZipException e) {
            throw new IOException(e);
        }
        return counter;
    }


    /**
     * Uncompress given zip archive.
     *
     * @param path
     *         absolute path to directory, which contains target zip archive
     * @param archiveName
     *         name of archive to unzip
     * @param subfolder
     *         if {@code true} uncompress archive in the directory with the same name as archive,
     *          but without extension (e.g. demo.zip -> demo). If archive have no extension or
     *          extension different from ".zip" then folder take name with ".extracted" suffix
     *          (e.g. demo -> demo.extracted)
     * @throws IOException
     *         if error occurs while uncompressing files
     */
    public static void unzip(Path path, String archiveName, boolean subfolder) throws IOException {
        try {
            ZipFile zipFile = new ZipFile(path + "/" + archiveName);
            if (subfolder) {
                String directoryName;
                if (archiveName.endsWith(".zip")) {
                    directoryName = archiveName.substring(0, archiveName.length() - 4);
                } else {
                    directoryName = archiveName + ".extracted";
                }
                zipFile.extractAll(path.resolve(directoryName).toString());
            } else {
                zipFile.extractAll(path.toString());
            }
        } catch (ZipException e) {
            throw new IOException(e);
        }
    }

}
