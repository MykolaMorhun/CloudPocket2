package com.cloudpocket.model.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Object for transfer data about file.
 */
public class FileDto {
    /** full name of file, e.g. 'file.ext' */
    private String filename;
    /**
     * extension of file item, e.g. 'png'
     * in case of directory or file without extension is empty string
     */
    private String extension;
    /** {@code true} if directory */
    private boolean directory;
    /** size in bytes */
    private long size;
    /** creation date in format: 'dd.MM.yyyy HH:mm:ss' as string */
    private String creationDate;
    /** creation date in long format */
    private long creationDateLong;

    private static final SimpleDateFormat FILE_CREATION_DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public FileDto(String name, boolean directory, long size, long creationDate) {
        this.filename = name;
        if (!directory) {
            int lastDotIndex = filename.lastIndexOf(".");
            if (lastDotIndex == -1) {
                extension = "";
            } else {
                extension = filename.substring(lastDotIndex + 1).toLowerCase();
            }
        } else {
            extension = "";
        }
        this.directory = directory;
        this.size = size;
        this.creationDateLong = creationDate;
        this.creationDate = FILE_CREATION_DATE_TIME_FORMAT.format(new Date(creationDate));
    }

    public String getFilename() {
        return filename;
    }

    public String getExtension() {
        return extension;
    }

    public boolean isDirectory() {
        return directory;
    }

    public long getSize() {
        return size;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public long getCreationDateLong() {
        return creationDateLong;
    }

}
