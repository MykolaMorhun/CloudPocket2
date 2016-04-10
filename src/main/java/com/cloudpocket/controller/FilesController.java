package com.cloudpocket.controller;

import com.cloudpocket.model.FileDetails;
import com.cloudpocket.model.dto.FileDto;
import com.cloudpocket.services.FilesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Map;

@RestController
@RolesAllowed("user")
@RequestMapping("/api/files")
public class FilesController {

    @Autowired
    FilesService filesService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<FileDto> getFilesList(@RequestParam(required =  true) String path,
                                      @RequestParam(required = false) String order,
                                      @RequestParam(required = false) Boolean isReverse,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      HttpServletResponse response) {
        try {
            return filesService.listFiles(userDetails.getUsername(), path, order, isReverse);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
        return null;
    }

    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public Integer copyFiles(@RequestParam(required = true) String pathFrom,
                             @RequestParam(required = true) String pathTo,
                             @RequestParam(required = true) String[] files,
                             @RequestParam(required = false) Boolean isReplaceIfExist,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletResponse response) {
        try {
            return filesService.copyFiles(userDetails.getUsername(), pathFrom, pathTo, files, isReplaceIfExist);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
        return 0;
    }

    @RequestMapping(value = "/move", method = RequestMethod.PUT)
    public Integer moveFiles(@RequestParam(required = true) String pathFrom,
                             @RequestParam(required = true) String pathTo,
                             @RequestParam(required = true) String[] files,
                             @RequestParam(required = false) Boolean isReplaceIfExist,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletResponse response) {
        try {
            return filesService.moveFiles(userDetails.getUsername(), pathFrom, pathTo, files, isReplaceIfExist);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
        return 0;
    }

    @RequestMapping(value = "/rename", method = RequestMethod.PUT)
    public void renameFile(@RequestParam(required = true) String path,
                           @RequestParam(required = true) String oldName,
                           @RequestParam(required = true) String newName,
                           @AuthenticationPrincipal UserDetails userDetails,
                           HttpServletResponse response) {
        try {
            filesService.rename(userDetails.getUsername(), path, oldName, newName);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Integer deleteFiles(@RequestParam(required = true) String path,
                               @RequestParam(required = true) String[] files,
                               @AuthenticationPrincipal UserDetails userDetails,
                               HttpServletResponse response) {
        try {
            return filesService.deleteFiles(userDetails.getUsername(), path, files);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        }
        return 0;
    }

    @RequestMapping(value = "/compress", method = RequestMethod.POST)
    public void compressFiles(@RequestParam(required = true) String path,
                              @RequestParam(required = true) String[] files,
                              @RequestParam(required = false) String archiveName,
                              @RequestParam(required = false) String archiveType,
                              @AuthenticationPrincipal UserDetails userDetails,
                              HttpServletResponse response) {
        try {
            filesService.createArchive(userDetails.getUsername(), path, files, archiveName, archiveType);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

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
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    @RequestMapping(value = "/create/folder", method = RequestMethod.POST)
    public void createDirectory(@RequestParam(required = true) String path,
                                @RequestParam(required = true) String name,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpServletResponse response) {
        try {
            filesService.createDirectory(userDetails.getUsername(), path, name);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (FileAlreadyExistsException e) {
            response.setStatus(409);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    @RequestMapping(value = "/download/file", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFile(@RequestParam(required = true) String path,
                             @RequestParam(required = true) String file,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletResponse response) {
        try {
            filesService.downloadFile(userDetails.getUsername(), path, file, response);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    @RequestMapping(value = "/download/archive", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadFilesInArchive(@RequestParam(required = true) String path,
                                       @RequestParam(required = true) String[] files,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       HttpServletResponse response) {
        try {
            filesService.downloadFilesInArchive(userDetails.getUsername(), path, files, response);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    @RequestMapping(value = "/upload/file", method = RequestMethod.POST,
                    consumes = "multipart/form-data")
    public void uploadFile(@RequestParam(required = true) MultipartFile file,
                           @RequestParam(required = true) String path,
                           @RequestParam(required = false) String name,
                           @AuthenticationPrincipal UserDetails userDetails,
                           HttpServletResponse response) {
        try {
            filesService.uploadFile(userDetails.getUsername(), path, name, file);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    @RequestMapping(value = "/upload/structure", method = RequestMethod.POST)
    public void uploadStructure(@RequestParam(required = true) MultipartFile file,
                                @RequestParam(required = true) String path,
                                @RequestParam(required = false) Boolean skipSubfolder,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpServletResponse response) {
        try {
            filesService.uploadFileStructure(userDetails.getUsername(), path, file, skipSubfolder);
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public Map<String, FileDto> search(@RequestParam(required = true) String path,
                                       @RequestParam(required = true) String namePattern,
                                       @RequestParam(required = false) Boolean skipSubfolders,
                                       @RequestParam(required = false) Integer maxResults,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       HttpServletResponse response) {
        try {
            return filesService.search(userDetails.getUsername(), path, namePattern, skipSubfolders, maxResults);
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
        return null;
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public FileDetails getDetailedFileInformation(@RequestParam(required = true) String path,
                                                  @RequestParam(required = true) String name,
                                                  @AuthenticationPrincipal UserDetails userDetails,
                                                  HttpServletResponse response) {
        try {
            return filesService.getDetailedFileInfo(userDetails.getUsername(), path, name);
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (IOException e) {
            response.setStatus(500);
        }
        return null;
    }

}
