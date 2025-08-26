package in.wynk.secret.manager.service;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
public class MutliPartFileUplaodController {

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file){
        return file.getName();
    }
}
