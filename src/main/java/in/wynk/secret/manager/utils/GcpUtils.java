package in.wynk.secret.manager.utils;

import com.google.api.gax.paging.Page;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.transfermanager.DownloadResult;
import com.google.cloud.storage.transfermanager.ParallelDownloadConfig;
import com.google.cloud.storage.transfermanager.TransferManager;
import com.google.cloud.storage.transfermanager.TransferManagerConfig;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GcpUtils {
    private static final Logger logger = LoggerFactory.getLogger(GcpUtils.class.getCanonicalName());
    static {
        GoogleCredentials googleCredentials = null;
        try {
            googleCredentials = GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ImpersonatedCredentials impersonatedCredentials = ImpersonatedCredentials.create(
            googleCredentials,
            "gke-wynk-pre-xstrm-app-sa@prj-wynk-pre-xstrm-svc-01.iam.gserviceaccount.com",
            null,
            Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"),
            3600
    );
         storage = StorageOptions.newBuilder().setCredentials(impersonatedCredentials).build().getService();
    }
    private static final Storage storage;

    private static final String GCS_PREFIX = "gs://";
//    private static final Storage storage = StorageOptions.getDefaultInstance().getService();
    private static final String SLASH = "/";
    public static final String EMPTY = "";
    private static final String GCS_URL = "https://storage.cloud.google.com/";
    private static final String GCS_CONSOLE_URL = "https://console.cloud.google.com/storage/browser/_details/";


    public static String uploadFile(String bucketName, MultipartFile file) throws IOException {
        try {
            String fileName = file.getOriginalFilename();
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            Blob blob = storage.create(blobInfo, file.getInputStream());
           return blob.signUrl(5,TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature()).toString();
        }catch (Exception e){
            logger.error("Exception : e ",e);
            throw e;
        }
    }


    //working fine without public
    public static boolean uploadFileToGcs(String bucket, String dir, File localFile) {
        BlobId blobId = BlobId.of(bucket, dir + localFile.getName());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        try {
            storage.createFrom(blobInfo, new FileInputStream(localFile));
            System.out.println(GCS_URL+bucket+SLASH+blobId.getName());
            System.out.println(GCS_CONSOLE_URL+bucket+SLASH+blobId.getName());
            return true;
        } catch (IOException e) {
            logger.error("Error uploading file to GCS: {}", e.getMessage());
            return false;
        }
    }

    //working
    public static boolean doesFileExist(String bucket, String filePath) {
        Blob blob = storage.get(BlobId.of(bucket, filePath));
        return blob != null && blob.exists();
    }

    //TODO-GCP : need to check for this
    public static String generatePreSignedUrl(String bucketName, String objectKey, Long expiration) throws IOException {
        BlobId blobId = BlobId.of(bucketName, objectKey);
        URL url = storage.signUrl(BlobInfo.newBuilder(blobId).build(), expiration, TimeUnit.MILLISECONDS);
        return url.toString();
    }
    public static Optional<String> getLatestFileWithPrefix(String bucket, String filePrefix) {
        Page<Blob> blobs = storage.list(bucket, Storage.BlobListOption.prefix(filePrefix));

        Optional<Blob> latestBlob = blobs.streamAll().max(Comparator.comparing(Blob::getUpdateTimeOffsetDateTime));

        return latestBlob.map(Blob::getName);
    }

    //this is working
    public static List<String> getAllFilesWithPrefix(String bucket, String filePrefix) {
        try{
        List<String> fileList = new ArrayList<>();
        Page<Blob> blobs = storage.list(bucket, BlobListOption.prefix(filePrefix));
        for (Blob blob : blobs.iterateAll()) {
            fileList.add(blob.getName());
        }
        return fileList;}catch (Exception e){
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public static void saveToLocal(String bucket, String key, File file) {
        try {
            Blob blob = storage.get(BlobId.of(bucket, key));
            if (ObjectUtils.isNotEmpty(blob)) {
                blob.downloadTo(file.toPath());
            } else {
                logger.error("File not found in GCS: {}/{}", bucket, key);
            }
        } catch (Exception e) {
            logger.error("Error saving file : {} of bucket : {} to local path : {}  is : {}", key, bucket, file.toPath(), e.getMessage(), e);
            throw e;
        }
    }

    //working
    public static boolean uploadFileToGcs(String bucket, String dir, String fileName, InputStream inputStream) {
        BlobId blobId = BlobId.of(bucket, dir + fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        try {
            storage.createFrom(blobInfo, inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static Blob getFile(String bucket, String fileName){
       return storage.get(BlobId.of(bucket, fileName));
    }

    //working fine
    public static BufferedReader getReaderForGcsFile(String bucket, String fileName) {
        Blob blob = storage.get(BlobId.of(bucket, fileName));
        if (blob == null) {
            return null;
        }
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(blob.getContent())));
    }

    //working fine
    public static void copyFileOnGcs(String fromBucket, String fromKey, String toBucket, String toKey) {
        BlobId srcBlobId = BlobId.of(fromBucket, fromKey);
        BlobId destBlobId = BlobId.of(toBucket, toKey);
        CopyWriter copyWriter = storage.copy(Storage.CopyRequest.of(srcBlobId, destBlobId));
        System.out.println(copyWriter.isDone());
    }

    //working fine
    public static void deleteFileFromGcs(String bucket, String fileName) {
        boolean deleted = storage.delete(BlobId.of(bucket, fileName));
        if (deleted) {
            System.out.println("File deleted successfully");
        } else {
            System.out.println("File not found");
        }
    }


    //need to check when using it
    public static List<File> downloadFiles(String bucket, String gcsDir, String localDir) throws IOException {
        List<File> files = new ArrayList<>();
        Page<Blob> blobs = storage.list(bucket, BlobListOption.prefix(gcsDir));
        for (Blob blob : blobs.iterateAll()) {
            File localFile = new File(localDir + blob.getName());
            blob.downloadTo(localFile.toPath());
            files.add(localFile);
        }
        return files;
    }

    //working fine
    public static void cleanBucketsDirectory(String bucketName, String prefix) {
        Page<Blob> blobs = storage.list(bucketName, BlobListOption.prefix(prefix));
        List<BlobId> blobIds = new ArrayList<>();
        for (Blob blob : blobs.iterateAll()) {
            blobIds.add(blob.getBlobId());
        }
        storage.delete(blobIds);
    }

    public static void saveToLocal(String bucket, String key, String localPath) throws IOException {
        try {
            Blob blob = storage.get(BlobId.of(bucket, key));
            if (blob != null) {
                blob.downloadTo(new File(localPath).toPath());
            }
        }catch (Exception exception){
            logger.error("Error while saving file to local"  ,exception);
        }
    }


    public static void saveToLocal2(String bucket, String key, String localPath) throws IOException {
        try {
            Blob blob = storage.get(BlobId.of(bucket, key));
            if (blob != null) {
                // Prepare the BlobInfo
                BlobInfo blobInfo = BlobInfo.newBuilder(blob.getBlobId()).build();
                TransferManager transferManager =
                        TransferManagerConfig.newBuilder()
                                .setAllowDivideAndConquerDownload(true)
                                .build()
                                .getService();
                ParallelDownloadConfig parallelDownloadConfig =
                        ParallelDownloadConfig.newBuilder()
                                .setBucketName(bucket)
                                .setDownloadDirectory(Paths.get(localPath))
                                .build();
                List<DownloadResult> results =
                        transferManager.downloadBlobs(Collections.singletonList(blobInfo), parallelDownloadConfig).getDownloadResults();
            }
        } catch (Exception exception) {
            System.err.println("Error while saving file to local: " + exception.getMessage());
            exception.printStackTrace();
        }
    }



    //working fine
    public static void moveFile(String fromBucket, String fromKey, String toBucket, String toKey) {
        try {
            copyFileOnGcs(fromBucket, fromKey, toBucket, toKey);
            deleteFileFromGcs(fromBucket, fromKey);
        } catch (Exception e) {
            logger.error("Error moving file from {} to {}: {}", fromBucket + "/" + fromKey, toBucket + "/" + toKey, e.getMessage());
        }
    }


    //this is working
    public static String getFileNameFromPath(String path) {
        String[] pathData = path.split("/");
        return pathData[pathData.length - 1];
    }

    public static void downloadFile(String bucketName, String objectName, String destFilePath) throws IOException {
        Blob blob = storage.get(BlobId.of(bucketName, objectName));
        try (ReadableByteChannel readChannel = blob.reader();
             FileOutputStream fileOutputStream = new FileOutputStream(destFilePath)) {
            fileOutputStream.getChannel().transferFrom(readChannel, 0, Long.MAX_VALUE);
        }
    }

}

