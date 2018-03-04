package com.cloudpocket.model.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines files groups by extensions.
 */
public interface FileCategories {
    Set<String> PLAIN_TEXT = new HashSet<>(Arrays.asList(
            "txt","log"
    ));
    Set<String> SOURCE_CODE = new HashSet<>(Arrays.asList(
            "c","cpp","h","java","pas","js","php","py","pl","scala","sql","css","html","xml","sh"
    ));
    Set<String> IMAGES = new HashSet<>(Arrays.asList(
            "bmp","jpg","jpeg","png","gif","ico"
    ));
    Set<String> AUDIO = new HashSet<>(Arrays.asList(
            "mp3","ogg","wav","m4a"
    ));
    Set<String> VIDEO = new HashSet<>(Arrays.asList(
            "mpeg","mpg","mp4","avi","mkv","flv","webm","mov","wmv","3gp"
    ));
}
