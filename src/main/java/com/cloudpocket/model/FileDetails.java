package com.cloudpocket.model;

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
    private Long created;
    private Long modified;
    private Long accessed;
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

    public Long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public Long getAccessed() {
        return accessed;
    }

    public void setAccessed(long accessed) {
        this.accessed = accessed;
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
