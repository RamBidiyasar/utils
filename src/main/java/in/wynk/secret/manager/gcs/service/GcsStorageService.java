package in.wynk.secret.manager.gcs.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class GcsStorageService {

    private final Storage storage;
    private static final String BUCKET_NAME = "airtel-tv-tvtbr";

    public GcsStorageService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public String uploadImage(String path, MultipartFile file) throws IOException {
        String objectName = path + "/" + file.getOriginalFilename(); // Store in given path
        BlobInfo blobInfo = BlobInfo.newBuilder(BUCKET_NAME, objectName).build();

        storage.create(blobInfo, file.getBytes());

        return "https://image.airtel.tv/" + BUCKET_NAME + "/" + objectName;
    }
}
