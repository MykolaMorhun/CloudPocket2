package com.cloudpocket.controller;

import com.cloudpocket.model.dto.FileDto;
import com.cloudpocket.services.FilesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

@RestController
@RolesAllowed("user")
@RequestMapping("/api/files")
public class FilesController {

    @Autowired
    FilesService filesService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<FileDto> getFilesList(@RequestParam(required = true) String path,
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
            response.setStatus(400);
        }
        return 0;
    }

    @RequestMapping(value = "/compress", method = RequestMethod.POST)
    public Boolean compressFiles(@RequestParam(required = true) String path,
                                 @RequestParam(required = true) String[] files,
                                 @RequestParam(required = false) String archiveName,
                                 @RequestParam(required = false) String archiveType,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        return null; // TODO implement
    }

    @RequestMapping(value = "/uncompress", method = RequestMethod.POST)
    public Boolean uncompressFiles(@RequestParam(required = true) String path,
                                   @RequestParam(required = true) String[] files,
                                   @RequestParam(required = false) String archiveType,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        return null; // TODO implement
    }

    @RequestMapping(value = "/create/folder", method = RequestMethod.POST)
    public void createFolder(@RequestParam(required = true) String path,
                               @RequestParam(required = true) String name,
                               @AuthenticationPrincipal UserDetails userDetails,
                               HttpServletResponse response) {
        String login = userDetails.getUsername();
        try {
            filesService.createDirectory(login, path, name);
        } catch (FileNotFoundException e) {
            response.setStatus(404);
        } catch (FileAlreadyExistsException e) {
            response.setStatus(409);
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public String downloadFiles(@RequestParam(required = true) String path,
                                @RequestParam(required = true) String[] files,
                                @AuthenticationPrincipal UserDetails userDetails) {
        return null; // TODO implement
    }

    @RequestMapping(value = "/upload/file", method = RequestMethod.POST)
    public String uploadFile(@RequestParam(required = true) String path,
                             @RequestParam(required = false) String name,
                             @AuthenticationPrincipal UserDetails userDetails) {
        return null; // TODO implement
    }

    @RequestMapping(value = "/upload/structure", method = RequestMethod.POST)
    public String uploadStructure(@RequestParam(required = true) String path,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) Boolean skipTopFolder,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        return null; // TODO implement
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<FileDto> search(@RequestParam(required = true) String path,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) Boolean skipSubfolders,
                                @AuthenticationPrincipal UserDetails userDetails) {
        return null; // TODO implement
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public String getDetailedFileInformation(@RequestParam(required = true) String path,
                                             @RequestParam(required = false) String name,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        return null; // TODO implement
    }

}
