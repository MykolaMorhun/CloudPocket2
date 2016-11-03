package com.cloudpocket.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Helper methods for work with streams.
 */
public class StreamUtils {

    /**
     * Writes given file to the output stream.
     *
     * @param pathToFile
     *         absolute path to file
     * @param outputStream
     *         output stream to write to
     * @throws IOException
     *         if input/output error occurs
     */
    public static void writeFileToOutputStream(Path pathToFile, OutputStream outputStream) throws IOException {
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(pathToFile.toFile()));
             BufferedOutputStream os = new BufferedOutputStream(outputStream)) {
            copyStream(is, os);
        }
    }

    /**
     * Writes given input stream to specified file.
     *
     * @param pathToFile
     *         absolute path to file to write to
     * @param inputStream
     *         stream from which file will be created
     * @throws IOException
     *         if input/output error occurs
     */
    public static void writeInputStreamToFile(Path pathToFile, InputStream inputStream) throws IOException {
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(pathToFile.toFile()));
             BufferedInputStream is = new BufferedInputStream(inputStream)) {
            copyStream(is, os);
        }
    }

    private static void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        int b;
        while ((b = inputStream.read()) != -1) {
            outputStream.write(b);
        }
        outputStream.flush();
    }

}
