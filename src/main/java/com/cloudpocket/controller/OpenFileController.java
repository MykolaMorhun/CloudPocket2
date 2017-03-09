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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/open")
public class OpenFileController {

    @Autowired
    private FilesService filesService;

    private static final Set<String> PLAIN_TEXT = new HashSet<>(Arrays.asList(
            "txt","log"
    ));
    private static final Set<String> IMAGES = new HashSet<>(Arrays.asList(
            "bmp","jpg","jpeg","png","gif","ico"
    ));
    private static final Set<String> SOURCE_CODE = new HashSet<>(Arrays.asList(
            "c","cpp","h","java","pas","js","php","py","pl","scala","sql","css","html","xml","sh"
    ));
    private static final Set<String> AUDIO = new HashSet<>(Arrays.asList(
            "mp3","ogg","wav","m4a"
    ));

    @RequestMapping(value = "/file", method = RequestMethod.GET)
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
            return "openfile/image";
        } else if (SOURCE_CODE.contains(fileExt)) {
            return "openfile/sourcecode";
        } else if (AUDIO.contains(fileExt)) {
            return "openfile/audio";
            //return "redirect:" + urlToOpen;
        } else {
            // hope, that user's browser is smart
            return "redirect:" + urlToOpen;
        }
    }

}
