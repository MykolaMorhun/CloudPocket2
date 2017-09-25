package com.cloudpocket.model;

import static com.cloudpocket.utils.Utils.stringDateTime;

/**
 * Contains detailed information about a file or directory.
 */
public class FileDetails {

    private String name;
    private String path;
    private Long size;
    private Boolean directory;
    private Boolean executable;
    private Boolean hidden;
    private Long createdLong;
    private String created;
    private Long modifiedLong;
    private String modified;
    private Long accessedLong;
    private String accessed;
    private String owner;
    private String contentType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public Boolean isExecutable() {
        return executable;
    }

    public void setExecutable(boolean executable) {
        this.executable = executable;
    }

    public Boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Long getCreatedLong() {
        return createdLong;
    }

    public void setCreated(long created) {
        this.createdLong = created;
        this.created = stringDateTime(created);
    }

    public String getCreated() {
        return created;
    }

    public Long getModifiedLong() {
        return modifiedLong;
    }

    public void setModified(long modified) {
        this.modifiedLong = modified;
        this.modified = stringDateTime(modified);
    }

    public String getModified() {
        return modified;
    }

    public Long getAccessedLong() {
        return accessedLong;
    }

    public void setAccessed(long accessed) {
        this.accessedLong = accessed;
        this.accessed = stringDateTime(accessed);
    }

    public String getAccessed() {
        return accessed;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
