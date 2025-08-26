package in.wynk.secret.manager.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class GcsSignedUrlController {

    private final  String bucketName = "ingestiondump";


    @GetMapping("/generate-signed-urls")
    public ResponseEntity<List<String>> getSignedUrls(@RequestParam("folder") String folder) throws IOException {
        List<String> signedUrls = new ArrayList<>();

        // Ensure the folder ends with a slash (to differentiate folder "abc" from "abcdef")
        if (!folder.endsWith("/")) {
            folder += "/";
        }


        GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault();
        String serviceAccount = "gke-wynk-pre-xstrm-app-sa@prj-wynk-pre-xstrm-svc-01.iam.gserviceaccount.com";
        ImpersonatedCredentials impersonatedCredentials = ImpersonatedCredentials.create(
                googleCredentials,
                serviceAccount,
                null,
                Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"),
                3600
        );
        Storage storage = StorageOptions.newBuilder().setCredentials(impersonatedCredentials).build().getService();

        // List all files under the given folder and its subfolders
        Iterable<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(folder)).iterateAll();

        for (Blob blob : blobs) {
            // Skip folder markers (if any)
            if (blob.isDirectory()) {
                continue;
            }

            // Generate a signed URL valid for 1 hour
            BlobId blobId = blob.getBlobId();
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            URL signedUrl = storage.signUrl(blobInfo, 6, TimeUnit.HOURS, Storage.SignUrlOption.withV4Signature());
            signedUrls.add(signedUrl.toString());
        }

        return ResponseEntity.ok(signedUrls);
    }
}
