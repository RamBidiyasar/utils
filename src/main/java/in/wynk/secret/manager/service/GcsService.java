//package in.wynk.secret.manager.service;
//
//
//import com.google.cloud.storage.Bucket;
//import com.google.cloud.storage.Blob;
//import com.google.cloud.storage.Storage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.UUID;
//
//@Service
//public class GcsService {
//
//    @Autowired
//    private Storage storage;
//
//    private String bucketName = "your-bucket-name"; // Replace with your bucket name
//
//    public String uploadFile(MultipartFile file) throws IOException {
//        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//        Blob blob = storage.get(bucketName).create(fileName, file.getInputStream(), file.getContentType());
//        return blob.getMediaLink();
//    }
//
//    public void downloadFile(String fileName, String destinationPath) throws IOException {
//        Blob blob = storage.get(bucketName).get(fileName);
//        if (blob != null) {
//            Path path = Paths.get(destinationPath);
//            blob.downloadTo(path);
//        }
//    }
//}
//
