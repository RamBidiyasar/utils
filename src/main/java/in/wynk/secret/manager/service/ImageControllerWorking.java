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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/images/1")
public class ImageControllerWorking {

    private static final String BUCKET_NAME = "airtel-tv-tvtbr";
    private static final String BASE_URL = "https://image.airtel.tv/";
    private static final String TEMP_DIR = "/tmp/data/test/";

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // Check if the uploaded file is an image
        if (file.isEmpty() || !file.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("File is not an image.");
        }

        try {
            // Ensure the temporary directory exists
            File tempDir = new File(TEMP_DIR);
            if (!tempDir.exists()) {
                tempDir.mkdirs(); // Create directory if it doesn't exist
            }

            // Remove spaces from the original filename
            String originalFileName = file.getOriginalFilename().replaceAll("\\s+", "_");
            // Generate a unique prefix
            String uniquePrefix = UUID.randomUUID().toString();
            // Get the current date formatted as yyyy_MM_dd
            String datePath = new SimpleDateFormat("yyyy_MM_dd").format(new Date());

            // Construct the final file name and temp file path
            String finalFileName = uniquePrefix + "_" + originalFileName;
            File tempFile = new File(tempDir, finalFileName);

            // Save the file to a temporary location
            file.transferTo(tempFile);

            // Initialize GCS client
            Storage storage = StorageOptions.getDefaultInstance().getService();
            String gcsPath = "grandslam/content/" + datePath + "/" + finalFileName;

            // Upload the file to GCS
            try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
                BlobId blobId = BlobId.of(BUCKET_NAME, gcsPath);

                // Create BlobInfo with the content type
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                        .setContentType(file.getContentType()) // Set the content type
                        .build();
                Blob blob = storage.create(blobInfo, fileInputStream); // Upload the file
            }

            // Delete the temporary file after uploading
            tempFile.delete();

            // Return the URL of the uploaded file
            String uploadedFileUrl = BASE_URL + gcsPath;
            return ResponseEntity.ok(uploadedFileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading the file.");
        }
    }
}
