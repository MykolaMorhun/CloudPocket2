package com.cloudpocket.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FilesService {

    @Value("${cloudpocket.storage}")
    public String PATH_TO_STORAGE;

    public String createFolder(String login, String path, String folderName) throws IOException {
        Path absolutePath = Paths.get(PATH_TO_STORAGE + login + path);
        Path newDirectoryPath = absolutePath.resolve(folderName);
        if (!Files.exists(newDirectoryPath)) {
            Files.createDirectory(newDirectoryPath);
            return folderName;
        }
        throw new FileAlreadyExistsException(path + "/" + folderName);
    }

}
