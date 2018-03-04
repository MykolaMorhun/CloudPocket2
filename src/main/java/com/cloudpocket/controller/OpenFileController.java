package com.cloudpocket.controller;

import com.cloudpocket.model.dto.FileDto;
import com.cloudpocket.services.FilesService;
import com.cloudpocket.utils.SimpleUrlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

import static com.cloudpocket.model.enums.FileCategories.*;

@Controller
@RequestMapping("/file")
public class OpenFileController {

    @Autowired
    private FilesService filesService;

    @RequestMapping(value = "/open", method = RequestMethod.GET)
    public String viewFile(@RequestParam String path,
                           @RequestParam String filename,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) throws IOException {
        String urlToOpen = new SimpleUrlBuilder("/api/files/download/file").withQueryParam("path", path)
                                                                           .withQueryParam("file", filename)
                                                                           .withQueryParam("inline", "true")
                                                                           .build();
        FileDto fileInfo = filesService.getFileInfo(userDetails.getUsername(), path, filename);
        model.addAttribute("fileInfo", fileInfo);
        model.addAttribute("path", path);
        model.addAttribute("urlToOpen", urlToOpen);

        String fileExt = fileInfo.getExtension();
        if (PLAIN_TEXT.contains(fileExt)) {
            return "redirect:" + urlToOpen;
        } else if (IMAGES.contains(fileExt)) {
            return "file/open/image";
        } else if (SOURCE_CODE.contains(fileExt)) {
            return "file/open/sourcecode";
        } else if (AUDIO.contains(fileExt)) {
            return "file/open/audio";
            //return "redirect:" + urlToOpen;
        } else {
            // hope, that user's browser is smart
            return "redirect:" + urlToOpen;
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editFile(@RequestParam String path,
                           @RequestParam String filename,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) throws IOException {
        String urlToOpen = new SimpleUrlBuilder("/api/files/download/file").withQueryParam("path", path)
                                                                           .withQueryParam("file", filename)
                                                                           .withQueryParam("inline", "true")
                                                                           .build();
        FileDto fileInfo = filesService.getFileInfo(userDetails.getUsername(), path, filename);
        model.addAttribute("fileInfo", fileInfo);
        model.addAttribute("path", path);
        model.addAttribute("urlToOpen", urlToOpen);

        String fileExt = fileInfo.getExtension();
        if (PLAIN_TEXT.contains(fileExt) || SOURCE_CODE.contains(fileExt)) {
            return "file/edit/text";
        } else {
            // editing of this file type is not supported
            throw new UnsupportedOperationException("Editing of *." + fileExt + " files is not supported.");
        }
    }

}
