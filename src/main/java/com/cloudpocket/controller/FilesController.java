package com.cloudpocket.controller;

import com.cloudpocket.model.FileDetails;
import com.cloudpocket.model.dto.FileDto;
import com.cloudpocket.services.FilesService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Api(basePath = "/api/files", value = "Files controller", description = "Operations with files")
@RestController
@RequestMapping("/api/files")
public class FilesController {

    @Autowired
    FilesService filesService;

    @ApiOperation(value = "List files",
                  notes = "Gets list of files with basic information from specified directory")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/list", method = RequestMethod.GET,
                    produces = APPLICATION_JSON_VALUE)
    public List<FileDto> getFilesList(@RequestParam(required =  true) String path,
                                      @RequestParam(required = false) String order,
                                      @RequestParam(required = false) Boolean isReverse,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      HttpServletResponse response) {
        try {
            return filesService.listFiles(userDetails.getUsername(), path, order, isReverse);
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
        return null;
    }

    @ApiOperation(value = "Copy files",
                  notes = "Copy specified files from one directory to another")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/copy", method = RequestMethod.POST,
                    produces = TEXT_PLAIN_VALUE)
    public Integer copyFiles(@RequestParam(required = true) String pathFrom,
                             @RequestParam(required = true) String pathTo,
                             @RequestParam(required = true) String[] files,
                             @RequestParam(required = false) Boolean isReplaceIfExist,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletResponse response) {
        try {
            return filesService.copyFiles(userDetails.getUsername(), pathFrom, pathTo, files, isReplaceIfExist);
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
        return 0;
    }

    @ApiOperation(value = "Move files",
                  notes = "Move specified files from one directory to another")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/move", method = RequestMethod.PUT,
                    produces = TEXT_PLAIN_VALUE)
    public Integer moveFiles(@RequestParam(required = true) String pathFrom,
                             @RequestParam(required = true) String pathTo,
                             @RequestParam(required = true) String[] files,
                             @RequestParam(required = false) Boolean isReplaceIfExist,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletResponse response) {
        try {
            return filesService.moveFiles(userDetails.getUsername(), pathFrom, pathTo, files, isReplaceIfExist);
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
        return 0;
    }

    @ApiOperation(value = "Rename file",
                  notes = "Rename specified file")
    @ResponseStatus(NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @RequestMapping(value = "/rename", method = RequestMethod.PUT)
    public void renameFile(@RequestParam(required = true) String path,
                           @RequestParam(required = true) String oldName,
                           @RequestParam(required = true) String newName,
                           @AuthenticationPrincipal UserDetails userDetails,
                           HttpServletResponse response) {
        try {
            filesService.rename(userDetails.getUsername(), path, oldName, newName);
            response.setStatus(NO_CONTENT.value());
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
    }

    @ApiOperation(value = "Delete files",
                  notes = "Deletes specified files and directories")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE,
                    produces = TEXT_PLAIN_VALUE)
    public Integer deleteFiles(@RequestParam(required = true) String path,
                               @RequestParam(required = true) String[] files,
                               @AuthenticationPrincipal UserDetails userDetails,
                               HttpServletResponse response) {
        try {
            return filesService.deleteFiles(userDetails.getUsername(), path, files);
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        }
        return 0;
    }

    @ApiOperation(value = "Compress files",
                  notes = "Creates archive from specified files")
    @ResponseStatus(NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @RequestMapping(value = "/compress", method = RequestMethod.POST)
    public void compressFiles(@RequestParam(required = true) String path,
                              @RequestParam(required = true) String[] files,
                              @RequestParam(required = false) String archiveName,
                              @RequestParam(required = false) String archiveType,
                              @AuthenticationPrincipal UserDetails userDetails,
                              HttpServletResponse response) {
        try {
            filesService.createArchive(userDetails.getUsername(), path, files, archiveName, archiveType);
            response.setStatus(NO_CONTENT.value());
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
    }

    @ApiOperation(value = "Extract files",
                  notes = "Extract files from specified archive")
    @ResponseStatus(NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @RequestMapping(value = "/uncompress", method = RequestMethod.POST)
    public void uncompressFiles(@RequestParam(required = true) String path,
                                @RequestParam(required = true) String archiveName,
                                @RequestParam(required = true) String archiveType,
                                @RequestParam(required = false) Boolean extractIntoSubdirectory,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpServletResponse response) {
        try {
            filesService.uncompressArchive(userDetails.getUsername(),
                                           path,
                                           archiveName,
                                           archiveType,
                                           extractIntoSubdirectory);
            response.setStatus(NO_CONTENT.value());
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
    }

    @ApiOperation(value = "Create directory",
                  notes = "Creates directory in specified location")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 409, message = "Folder already exist"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @RequestMapping(value = "/create/folder", method = RequestMethod.POST)
    public void createDirectory(@RequestParam(required = true) String path,
                                @RequestParam(required = true) String name,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpServletResponse response) {
        try {
            filesService.createDirectory(userDetails.getUsername(), path, name);
            response.setStatus(NO_CONTENT.value());
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (FileAlreadyExistsException e) {
            response.setStatus(CONFLICT.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
    }

    @ApiOperation(value = "Download file",
                  notes = "Gives to user download specified file")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/download/file", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFile(@RequestParam(required = true) String path,
                             @RequestParam(required = true) String file,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletResponse response) {
        try {
            filesService.downloadFile(userDetails.getUsername(), path, file, response);
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
    }

    @ApiOperation(value = "Download files",
                  notes = "Gives to user download archive with specified files")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/download/archive", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFilesInArchive(@RequestParam(required = true) String path,
                                       @RequestParam(required = true) String[] files,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       HttpServletResponse response) {
        try {
            filesService.downloadFilesInArchive(userDetails.getUsername(), path, files, response);
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
    }

    @ApiOperation(value = "Upload file",
                  notes = "Saves file received from user")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @RequestMapping(value = "/upload/file", method = RequestMethod.POST,
                    consumes = "multipart/form-data")
    public void uploadFile(@RequestParam(required = true) MultipartFile file,
                           @RequestParam(required = true) String path,
                           @RequestParam(required = false) String name,
                           @AuthenticationPrincipal UserDetails userDetails,
                           HttpServletResponse response) {
        try {
            filesService.uploadFile(userDetails.getUsername(), path, name, file);
            response.setStatus(NO_CONTENT.value());
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
    }

    @ApiOperation(value = "Upload file structure",
                  notes = "Saves file tree from archive")
    @ResponseStatus(NO_CONTENT)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad archive"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @RequestMapping(value = "/upload/structure", method = RequestMethod.POST,
                    consumes = MULTIPART_FORM_DATA_VALUE)
    public void uploadStructure(@RequestParam(required = true) MultipartFile file,
                                @RequestParam(required = true) String path,
                                @RequestParam(required = false) Boolean skipSubfolder,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpServletResponse response) {
        try {
            filesService.uploadFileStructure(userDetails.getUsername(), path, file, skipSubfolder);
            response.setStatus(NO_CONTENT.value());
        } catch (IllegalArgumentException e) {
            response.setStatus(BAD_REQUEST.value());
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
    }

    @ApiOperation(value = "Search",
                  notes = "Search for files and folders")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid parameter"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/search", method = RequestMethod.GET,
                    produces = APPLICATION_JSON_VALUE)
    public Map<String, FileDto> search(@RequestParam(required = true) String path,
                                       @RequestParam(required = true) String namePattern,
                                       @RequestParam(required = false) Boolean skipSubfolders,
                                       @RequestParam(required = false) Integer maxResults,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       HttpServletResponse response) {
        try {
            return filesService.search(userDetails.getUsername(), path, namePattern, skipSubfolders, maxResults);
        } catch (IllegalArgumentException e) {
            response.setStatus(BAD_REQUEST.value());
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
        return null;
    }

    @ApiOperation(value = "Retrieve file info",
                  notes = "Retrieves detailed information about specified file")
    @ResponseStatus(OK)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/info", method = RequestMethod.GET,
                    produces = APPLICATION_JSON_VALUE)
    public FileDetails getDetailedFileInformation(@RequestParam(required = true) String path,
                                                  @RequestParam(required = true) String name,
                                                  @AuthenticationPrincipal UserDetails userDetails,
                                                  HttpServletResponse response) {
        try {
            return filesService.getDetailedFileInfo(userDetails.getUsername(), path, name);
        } catch (FileNotFoundException e) {
            response.setStatus(NOT_FOUND.value());
        } catch (IOException e) {
            response.setStatus(INTERNAL_SERVER_ERROR.value());
        }
        return null;
    }

}
