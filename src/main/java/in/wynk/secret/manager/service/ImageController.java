package in.wynk.secret.manager.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private static final String BUCKET_NAME = "airtel-tv-tvtbr";
    private static final String BASE_URL = "https://image.airtel.tv/";

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("File is not an image.");
        }

        try {
            String uniquePrefix = UUID.randomUUID().toString();
            String datePath = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
            String originalFileName = file.getOriginalFilename().replaceAll("\\s+", "_");
            String finalFileName = uniquePrefix + "_" + originalFileName;
            String gcsPath = "grandslam/content/" + datePath + "/" + finalFileName;

            Storage storage = StorageOptions.getDefaultInstance().getService();

            BlobId blobId = BlobId.of(BUCKET_NAME, gcsPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                                         .setContentType(file.getContentType()) // Set the content type
                                         .build();

            try (InputStream inputStream = file.getInputStream()) {
                storage.create(blobInfo, inputStream); // Upload the file
            }

            String uploadedFileUrl = BASE_URL + gcsPath;
            return ResponseEntity.ok(uploadedFileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading the file.");
        }
    }
}
