package in.wynk.secret.manager.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import in.wynk.secret.manager.utils.GcpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class GoogleCloudStorageService {


    @Value("${spring.application.name}")
    public String applicationName;

    private final Storage storage;

    @PostMapping
    public void init(){
        System.out.println(applicationName);
    }

    public GoogleCloudStorageService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }


    public String uploadFile(String bucketName, MultipartFile file) throws IOException {
        return GcpUtils.uploadFile(bucketName,file);
        // Define the directory where you want to upload the file in GCS
//        String dir = "TEST/"; // Replace with your actual directory logic
//
//        // Define if the file should be publicly readable
//        boolean publicRead = true; // Replace with your actual logic for public read
//
//        // Convert MultipartFile to File
//        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
//        try {
//            file.transferTo(convFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Handle the exception, maybe return an error response or throw a custom exception
//            return "Failed to upload file: " + e.getMessage();
//        }
//
//        // Call the uploadFileToGcs method
//        boolean success = GcpUtils.uploadFileToGcs(bucketName, dir, convFile);
//
//        if (success) {
//            return "File uploaded successfully";
//        } else {
//            return "Failed to upload file";
//        }
    }


    public byte[] downloadFile(String bucketName, String fileName) throws IOException {

        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        return blob.getContent();
    }

    public String getSignedUrl(String bucketName, String fileName) throws IOException{
        return GcpUtils.generatePreSignedUrl(bucketName,fileName,5000L);
    }
}

