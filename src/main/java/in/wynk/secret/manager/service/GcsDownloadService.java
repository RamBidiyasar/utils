package in.wynk.secret.manager.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Storage.SignUrlOption;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GcsDownloadService {

    private final Storage storage;

    public GcsDownloadService() throws Exception {
        this.storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()
                .getService();
    }

    /**
     * Generates signed URLs for all files in the specified folder of a GCS bucket.
     */
    public List<URL> generateSignedUrls(String bucketName, String folderPath) {
        List<URL> signedUrls = new ArrayList<>();

        // List all files in the folder
        Bucket bucket = storage.get(bucketName);
        Iterable<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(folderPath)).iterateAll();

        for (Blob blob : blobs) {
            if (!blob.isDirectory()) { // Skip directories
                URL signedUrl = storage.signUrl(
                        blob,
                        15, // URL expiry time (e.g., 15 minutes)
                        TimeUnit.MINUTES,
                        SignUrlOption.withV4Signature()
                );
                signedUrls.add(signedUrl);
            }
        }
        return signedUrls;
    }

    /**
     * Downloads files using signed URLs.
     */
    public void downloadFilesUsingSignedUrls(List<URL> signedUrls, String downloadDir) throws Exception {
        for (URL url : signedUrls) {
            String fileName = Paths.get(url.getPath()).getFileName().toString();
            try (var inputStream = url.openStream();
                 var outputStream = new FileOutputStream(Paths.get(downloadDir, fileName).toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }
}
