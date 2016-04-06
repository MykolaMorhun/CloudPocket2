package com.cloudpocket.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
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
            int b;
            while ((b = is.read()) != -1) {
                os.write(b);
            }
            outputStream.flush();
        }
    }

}
