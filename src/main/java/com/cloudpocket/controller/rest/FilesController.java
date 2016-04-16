package com.cloudpocket.controller.rest;

import com.cloudpocket.model.FileDetails;
import com.cloudpocket.model.dto.FileDto;
import com.cloudpocket.model.enums.ArchiveType;
import com.cloudpocket.services.FilesService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Api(basePath = "/api/files", value = "Files controller", description = "Operations with files")
@RestController
@RequestMapping("/api/files")
public class FilesController {

    @Autowired
    FilesService filesService;

    @ApiOperation(value = "List files",
                  notes = "Gets list of files with basic information from specified directory")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/list", method = RequestMethod.GET,
                    produces = APPLICATION_JSON_VALUE)
    public List<FileDto> getFilesList(@RequestParam(required =  true) String path,
                                      @RequestParam(required = false) String order,
                                      @RequestParam(required = false) Boolean isReverse,
                                      @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        return filesService.listFiles(userDetails.getUsername(), path, order, isReverse);
    }

    @ApiOperation(value = "Copy files",
                  notes = "Copy specified files from one directory to another")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/copy", method = RequestMethod.POST,
                    produces = APPLICATION_JSON_VALUE)
    public String copyFiles(@RequestParam(required = true) String pathFrom,
                            @RequestParam(required = true) String pathTo,
                            @RequestParam(required = true) String[] files,
                            @RequestParam(required = false) Boolean isReplaceIfExist,
                            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        int copiedFiles = filesService.copyFiles(userDetails.getUsername(), pathFrom, pathTo, files, isReplaceIfExist);
        JSONObject response = new JSONObject();
        response.put("copied files", copiedFiles);
        return response.toString();
    }

    @ApiOperation(value = "Move files",
                  notes = "Move specified files from one directory to another")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/move", method = RequestMethod.PUT,
                    produces = APPLICATION_JSON_VALUE)
    public String moveFiles(@RequestParam(required = true) String pathFrom,
                             @RequestParam(required = true) String pathTo,
                             @RequestParam(required = true) String[] files,
                             @RequestParam(required = false) Boolean isReplaceIfExist,
                             @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        int movedFiles  = filesService.moveFiles(userDetails.getUsername(), pathFrom, pathTo, files, isReplaceIfExist);
        JSONObject response = new JSONObject();
        response.put("moved files", movedFiles);
        return response.toString();
    }

    @ApiOperation(value = "Rename file",
                  notes = "Rename specified file")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/rename", method = RequestMethod.PUT)
    public void renameFile(@RequestParam(required = true) String path,
                          @RequestParam(required = true) String oldName,
                          @RequestParam(required = true) String newName,
                          @AuthenticationPrincipal UserDetails userDetails,
                          HttpServletResponse response) throws IOException {
        filesService.rename(userDetails.getUsername(), path, oldName, newName);
    }

    @ApiOperation(value = "Delete files",
                  notes = "Deletes specified files and directories")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE,
                    produces = APPLICATION_JSON_VALUE)
    public String deleteFiles(@RequestParam(required = true) String path,
                              @RequestParam(required = true) String[] files,
                              @AuthenticationPrincipal UserDetails userDetails) throws FileNotFoundException {
        int deletedFiles =  filesService.deleteFiles(userDetails.getUsername(), path, files);
        JSONObject response = new JSONObject();
        response.put("deleted files", deletedFiles);
        return response.toString();
    }

    @ApiOperation(value = "Compress files",
                  notes = "Creates archive from specified files")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/compress", method = RequestMethod.POST)
    public void compressFiles(@RequestParam(required = true) String path,
                              @RequestParam(required = true) String[] files,
                              @RequestParam(required = false) String archiveName,
                              @RequestParam(required = false) ArchiveType archiveType,
                              @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        filesService.createArchive(userDetails.getUsername(), path, files, archiveName, archiveType);
    }

    @ApiOperation(value = "Extract files",
                  notes = "Extract files from specified archive")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/uncompress", method = RequestMethod.POST)
    public void uncompressFiles(@RequestParam(required = true) String path,
                                @RequestParam(required = true) String archiveName,
                                @RequestParam(required = true) ArchiveType archiveType,
                                @RequestParam(required = false) Boolean extractIntoSubdirectory,
                                @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        filesService.uncompressArchive(userDetails.getUsername(),
                                       path,
                                       archiveName,
                                       archiveType,
                                       extractIntoSubdirectory);
    }

    @ApiOperation(value = "Create directory",
                  notes = "Creates directory in specified location")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 409, message = "Folder already exist"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/create/folder", method = RequestMethod.POST)
    public void createDirectory(@RequestParam(required = true) String path,
                                @RequestParam(required = true) String name,
                                @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        filesService.createDirectory(userDetails.getUsername(), path, name);
    }

    @ApiOperation(value = "Download file",
                  notes = "Gives to user download specified file")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/download/file", method = RequestMethod.GET,
                    produces = APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFile(@RequestParam(required = true) String path,
                             @RequestParam(required = true) String file,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletResponse response) throws IOException {
        Path pathToFile = filesService.getAbsolutePathToFile(userDetails.getUsername(), path, file);
        response.setContentType(Files.probeContentType(pathToFile));
        response.setHeader("Content-disposition", "attachment; filename=\"" + pathToFile.getFileName() + "\"");
        response.setContentLengthLong(Files.size(pathToFile));
        filesService.downloadFile(pathToFile, response.getOutputStream());
    }

    @ApiOperation(value = "Download files",
                  notes = "Gives to user download archive with specified files")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/download/archive", method = RequestMethod.GET,
                    produces = APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFilesInArchive(@RequestParam(required = true) String path,
                                       @RequestParam(required = true) String[] files,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       HttpServletResponse response) throws IOException {
        response.setContentType("application/zip");
        response.setHeader("Content-disposition", "attachment; filename=\"" + new Date() + ".zip\"");
        filesService.downloadFilesInArchive(userDetails.getUsername(), path, files, response.getOutputStream());
    }

    @ApiOperation(value = "Upload file",
                  notes = "Saves file received from user")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/upload/file", method = RequestMethod.POST,
                    consumes = "multipart/form-data")
    public void uploadFile(@RequestParam(required = true) MultipartFile file,
                           @RequestParam(required = true) String path,
                           @RequestParam(required = false) String name,
                           @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        filesService.uploadFile(userDetails.getUsername(), path, name, file);
    }

    @ApiOperation(value = "Upload file structure",
                  notes = "Saves file tree from archive")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad archive"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 204, message = "OK") })
    @ResponseStatus(NO_CONTENT)
    @RequestMapping(value = "/upload/structure", method = RequestMethod.POST,
                    consumes = MULTIPART_FORM_DATA_VALUE)
    public void uploadStructure(@RequestParam(required = true) MultipartFile file,
                                @RequestParam(required = true) String path,
                                @RequestParam(required = false) Boolean skipSubfolder,
                                @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        filesService.uploadFileStructure(userDetails.getUsername(), path, file, skipSubfolder);
    }

    @ApiOperation(value = "Search",
                  notes = "Search for files and folders")
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
                                       @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        return filesService.search(userDetails.getUsername(), path, namePattern, skipSubfolders, maxResults);
    }

    @ApiOperation(value = "Retrieve file info",
                  notes = "Retrieves detailed information about specified file")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "OK") })
    @RequestMapping(value = "/info", method = RequestMethod.GET,
                    produces = APPLICATION_JSON_VALUE)
    public FileDetails getDetailedFileInformation(@RequestParam(required = true) String path,
                                                  @RequestParam(required = true) String name,
                                                  @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        return filesService.getDetailedFileInfo(userDetails.getUsername(), path, name);
    }

}
