package in.wynk.secret.manager.gcs.controller;

import in.wynk.secret.manager.gcs.service.GcsStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/gcs")
public class GcsController {

    private final GcsStorageService gcsStorageService;

    public GcsController(GcsStorageService gcsStorageService) {
        this.gcsStorageService = gcsStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("path") String path) {
        try {
            String gcsUrl = gcsStorageService.uploadImage(path, file);
            return ResponseEntity.ok("File uploaded successfully: " + gcsUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
}
