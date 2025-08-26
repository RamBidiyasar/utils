package in.wynk.secret.manager.controller;

import in.wynk.secret.manager.service.GoogleCloudStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private GoogleCloudStorageService googleCloudStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam(value = "bucketName" , defaultValue = "xstrm-test-preprod") String bucketName) {
        String fileUrl = null;
        try {
            fileUrl = googleCloudStorageService.uploadFile(bucketName, file);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(fileUrl);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam(value = "bucketName",defaultValue = "xstrm-test-preprod") String bucketName,
                                               @RequestParam(value = "fileName") String fileName) {
        try {
            byte[] fileContent = googleCloudStorageService.downloadFile(bucketName, fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/url")
    public ResponseEntity<String> getSignedUrl(@RequestParam(value = "bucketName",defaultValue = "xstrm-test-preprod") String bucketName,
                                               @RequestParam(value = "fileName", defaultValue = "tvepisodes_105.json") String fileName) {
        try {
           return ResponseEntity.ok(googleCloudStorageService.getSignedUrl(bucketName, fileName));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}

