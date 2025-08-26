package in.wynk.secret.manager.utils;


import com.google.api.gax.paging.Page;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class GCSUtils {

    private static final Storage storage = StorageOptions.getDefaultInstance().getService();
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String GCS_PREFIX = "gs://";
    private static final String SEPARATOR = "/";
    public static final String EMPTY = "";
    private static final String GCS_URL = "https://storage.cloud.google.com/";
    private static final String GCS_CONSOLE_URL = "https://console.cloud.google.com/storage/browser/_details/";
    
    public static boolean uploadFileToDirectoryAppendWithFileName(String bucket, String dir, File file) {
        try {
            uploadFileToBucket(bucket, dir, file);
            return true;
        } catch (IOException e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error uploading file to path : {} of bucket : {} is : {}", dir, bucket, e.getMessage(), e);
            return false;
        }
    }


    public static void uploadTextToDirectory(String bucket, String key, String data) {
        try {

            BlobId blobId = BlobId.of(bucket, key);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, data.getBytes());
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error uploading data to GCS bucket: {} at path: {} is: {}", bucket, key, e.getMessage(), e);
        }
    }


    public static String uploadFileForSignUrl(String bucket, String key, File file, Long duration, TimeUnit timeUnit) throws IOException {
        try {
        /*
         To run this at local, we need to define storage like this because service account is needed to create sign url :
         GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault();
         String serviceAccount = "gke-wynk-pre-xstrm-app-sa@prj-wynk-pre-xstrm-svc-01.iam.gserviceaccount.com";
         ImpersonatedCredentials impersonatedCredentials = ImpersonatedCredentials.create(
         googleCredentials,
         serviceAccount,
         null,
         Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"),
         3600
         );
         storage = StorageOptions.newBuilder().setCredentials(impersonatedCredentials).build().getService();
         */
            Blob blob = uploadFileToBucket(bucket, key, file);
            return blob.signUrl(duration, timeUnit).toString();
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error uploading data and creating signUrl to GCS bucket : {} at dir : {} is : {}", bucket, key, e.getMessage(), e);
            throw e;
        }
    }

    public static URL uploadFileWithSignedUrlGeneration(String bucket, String key, String contentType) throws IOException {
       try {
           BlobInfo blobInfo = BlobInfo.newBuilder(bucket, key).setContentType(contentType).build();
         /*
         To run this at local, we need to define storage like this because service account is needed to create sign url :
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
        */
           Map<String, String> extensionHeaders = new HashMap<>();
           //extensionHeaders.put(CONTENT_TYPE_FULL, contentType);
           // Generate a signed URL for upload (valid for 15 minutes)
           URL signedUrl = storage.signUrl(blobInfo,
                   15,
                   TimeUnit.MINUTES,
                   Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                   Storage.SignUrlOption.withExtHeaders(extensionHeaders),
                   Storage.SignUrlOption.withV4Signature());
           return signedUrl;
       } catch (Exception e) {
           //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error uploading data and creating signUrl to GCS bucket : {} at dir : {} is : {}", bucket, key, e.getMessage(), e);
           throw e;
       }
    }

    public static String uploadFileAndFetchUri(String bucket, String key, File file) throws IOException {
        Blob blob = uploadFileToBucket(bucket, key, file);
        return GCS_URL + bucket + SEPARATOR + blob.getName();
    }

    public static String uploadFileAndFetchConsoleUrl(String bucket, String key, File file) throws IOException {
        Blob blob = uploadFileToBucket(bucket, key, file);
        return GCS_CONSOLE_URL + bucket + SEPARATOR + blob.getName();
    }

    public static boolean doesFileExists(String bucket, String key) {
        try {
            Blob blob = storage.get(BlobId.of(bucket, key));
            return ObjectUtils.isNotEmpty(blob) && blob.exists();
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error fetching data form GCS bucket : {} for path : {}  is : {}", bucket, key, e.getMessage(), e);
            throw e;
        }
    }

    public static BufferedReader getObject(String bucketName, String key) {
        try {
            Blob blob = storage.get(BlobId.of(bucketName, key));
            if (ObjectUtils.isEmpty(blob)) {
                //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "File not found in GCS: {}/{}", bucketName, key);
                return null;
            }
            return new BufferedReader(new InputStreamReader(Channels.newInputStream(blob.reader())));
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error fetching data form GCS bucket : {} for file : {}  is : {}", bucketName, key, e.getMessage(), e);
            return null;
        }
    }

    public static String generateSignedUrl(String bucket, String key, Long expiration, TimeUnit timeUnit) {
        try {
            BlobId blobId = BlobId.of(bucket, key);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            URL url = storage.signUrl(blobInfo, expiration, timeUnit, Storage.SignUrlOption.withV4Signature());
            return url.toString();
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error generating signed URL for bucket : {} and file : {} is : {}", bucket, key, e.getMessage(), e);
            return null;
        }
    }

    public static void uploadFileToCompletePath(String bucket, String completePath, File tempFile) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
            BlobId blobId = BlobId.of(bucket, completePath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.createFrom(blobInfo, fileInputStream);
        } catch (IOException e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error uploading file : {}  to bucket : {} is : {}", completePath, bucket, e.getMessage(), e);
            throw e;
        }
    }


    public static void cleanDirectory(String bucket, String prefix) {
        int chunkSize = 1000;
        try {
            Page<Blob> blobs = storage.list(bucket, Storage.BlobListOption.prefix(prefix));
            List<BlobId> blobIds = new ArrayList<>();
            for (Blob blob : blobs.iterateAll()) {
                blobIds.add(blob.getBlobId());
                if (blobIds.size() == chunkSize) {
                    storage.delete(blobIds);
                    //log.info("Deleted {} files from directory : {}", blobIds.size(), prefix);
                    blobIds.clear();
                }
            }
            if (!blobIds.isEmpty()) {
                storage.delete(blobIds);
                //log.info("Deleted {} files from directory : {}", blobIds.size(), prefix);
            }
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error cleaning directory : {} of bucket {} is : {}", prefix, bucket, e.getMessage(), e);
            throw e;
        }
    }

    public static void cleanDirectoryWithMaxFile(String bucket, String prefix, int chunkSize, int maxFilesToDelete) {

        int deletedFilesCount = 0;
        try {
            Page<Blob> blobs = storage.list(bucket, Storage.BlobListOption.prefix(prefix));
            Iterator<Blob> blobIterator = blobs.iterateAll().iterator();
            List<BlobId> blobIds = new ArrayList<>(chunkSize);

            while (blobIterator.hasNext() && deletedFilesCount < maxFilesToDelete) {
                blobIds.add(blobIterator.next().getBlobId());

                // If the chunk is full or we are close to the max files limit
                if (blobIds.size() == chunkSize || deletedFilesCount + blobIds.size() >= maxFilesToDelete) {
                    int filesToDelete = Math.min(blobIds.size(), maxFilesToDelete - deletedFilesCount);
                    storage.delete(blobIds.subList(0, filesToDelete));
                    deletedFilesCount += filesToDelete;
                    //log.info("Deleted {} files from directory : {}", filesToDelete, prefix);

                    blobIds.clear();
                }
            }
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error cleaning directory : {} of bucket {} is : {}", prefix, bucket, e.getMessage(), e);
            throw e;
        }

        //log.info("Total files deleted: {} from directory: {}", deletedFilesCount, prefix);
    }



    public static String getFileNameFromPath(String key) {
        String[] pathData = key.split(SEPARATOR);
        return pathData[pathData.length - 1];
    }

    public static void saveToLocal(String bucket, String key, String localPath) throws IOException {
        try {
            Blob blob = storage.get(BlobId.of(bucket, key));
            if (ObjectUtils.isNotEmpty(blob)) {
                createDirectoryIfNotExists(localPath);
                blob.downloadTo(new File(localPath).toPath());
            } else {
                //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "File not found in GCS: {}/{}", bucket, key);
            }
        } catch (Throwable e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error saving file : {} of bucket : {} to local path : {}  is : {}", key, bucket, localPath, e.getMessage(), e);
            throw e;
        }
    }

    public static void saveToLocal(String bucket, String key, File file) {
        try {
            Blob blob = storage.get(BlobId.of(bucket, key));
            if (ObjectUtils.isNotEmpty(blob)) {
                blob.downloadTo(file.toPath());
            } else {
                //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "File not found in GCS: {}/{}", bucket, key);
            }
        } catch (Throwable e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error saving file : {} of bucket : {} to local path : {}  is : {}", key, bucket, file.toPath(), e.getMessage(), e);
            throw e;
        }
    }

    public static String getDirectoryKey(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return key.substring(0, key.lastIndexOf(SEPARATOR) + 1);
    }


    public static void moveFile(String fromBucket, String fromKey, String toBucket, String toKey) {
        try {
            copyFileOnGcs(fromBucket, fromKey, toBucket, toKey);
            deleteFileFromGcs(fromBucket, fromKey);
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error moving file from {} to {}: {}", fromBucket + SEPARATOR + fromKey, toBucket + SEPARATOR + toKey, e.getMessage(), e);
        }
    }

    public static BufferedReader getReader(String bucket, String key) {
        try {
            Blob blob = storage.get(BlobId.of(bucket, key));
            if (ObjectUtils.isEmpty(blob)) {
                //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "File not found in GCS: {}/{}", bucket, key);
                return null;
            }
            return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(blob.getContent())));
        } catch (Throwable e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error while creating BufferedReader for file : {} of bucket : {}  is  : {}", key, bucket, e.getMessage(), e);
            throw e;
        }
    }


    public static List<String> getAllFilesWithPrefix(String bucket, String prefix) {
        try {
            List<String> fileList = new ArrayList<>();
            Page<Blob> blobs = storage.list(bucket, Storage.BlobListOption.prefix(prefix));
            for (Blob blob : blobs.iterateAll()) {
                fileList.add(blob.getName());
            }
            return fileList;
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error while reading from  GCS bucket : {} at prefix : {}  is : {}", bucket, prefix, e.getMessage(), e);
            throw e;
        }
    }

    public static Optional<String> getLatestFileWithPrefix(String bucket, String prefix) {
        try {
            Page<Blob> blobs = storage.list(bucket, Storage.BlobListOption.prefix(prefix));
            Optional<Blob> latestBlob = blobs.streamAll().max(Comparator.comparing(Blob::getUpdateTimeOffsetDateTime));
            return latestBlob.map(Blob::getName);
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error while reading from  GCS bucket : {} at prefix : {}  is : {}", bucket, prefix, e.getMessage(), e);
            throw e;
        }
    }

    public static String saveObjectWithMetaData(String bucket, String key, InputStream inputStream, String contentType, long contentLength) throws IOException {
        try {
            BlobInfo blobInfo = BlobInfo.newBuilder(bucket, key)
                    .setMetadata(Map.of(CONTENT_LENGTH, String.valueOf(contentLength)))
                    .setContentType(contentType)
                    .build();

            try (WriteChannel writer = storage.writer(blobInfo)) {
                byte[] buffer = new byte[1_048_576];  // 1 MB buffer
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    writer.write(ByteBuffer.wrap(buffer, 0, bytesRead));
                }
            }
            return blobInfo.getName();
        } catch (IOException e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error uploading file to GCS bucket : {} is : {}", bucket, e.getMessage(), e);
            throw e;
        }
    }


    public static String getFileAsString(String bucket, String key) {
        try {
            Blob blob = storage.get(BlobId.of(bucket, key));
            if (blob != null) {
                return new String(blob.getContent());
            } else {
                //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "File not found in GCS: {}/{}", bucket, key);
                return null;
            }
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error getting file : {} from bucket : {}  as string: {}", key, bucket, e.getMessage(), e);
            return null;
        }
    }




    public static List<String> listObjectsNamesWithPrefix(String bucket, String prefix, long timeToConsider) {
        List<String> keys = new ArrayList<>();
        try {
            Page<Blob> blobs = storage.list(bucket, Storage.BlobListOption.prefix(prefix));
            for (Blob blob : blobs.iterateAll()) {
                if (blob.getName().contains(".gz") && blob.getUpdateTime() >= timeToConsider) {
                    keys.add(blob.getName());
                }
            }
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error listing blob names with prefix: {} in bucket : {} is  {}", prefix, bucket, e.getMessage(), e);
        }
        return keys;
    }

    public static Scanner getScanner(String bucket, String key) {
        try {
            Blob blob = storage.get(BlobId.of(bucket, key));
            if (ObjectUtils.isNotEmpty(blob)) {
                return new Scanner(new ByteArrayInputStream(blob.getContent()));
            } else {
                //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "File not found in GCS: {}/{}", bucket, key);
                return null;
            }
        } catch (Exception e) {
            //log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error while getting scanner for file : {} in bucket {} is : {}", key, bucket, e.getMessage(), e);
            return null;
        }
    }

    private static void copyFileOnGcs(String fromBucket, String fromKey, String toBucket, String toKey) {
        BlobId srcBlobId = BlobId.of(fromBucket, fromKey);
        BlobId destBlobId = BlobId.of(toBucket, toKey);
        storage.copy(Storage.CopyRequest.of(srcBlobId, destBlobId));
    }

    private static void deleteFileFromGcs(String bucket, String fileName) {
        storage.delete(BlobId.of(bucket, fileName));
    }

    private static Blob uploadFileToBucket(String bucket, String dir, File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            BlobId blobId = BlobId.of(bucket, dir + file.getName());
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            return storage.createFrom(blobInfo, fileInputStream);
        } catch (IOException e) {
            ////log.error(Base//loggingMarkers.STORAGE_BUCKET_ERROR, "Error uploading file : {}  to bucket : {} is : {}", dir, bucket, e.getMessage(), e);
            throw e;
        }
    }

    private static void createDirectoryIfNotExists(String directoryPath) throws IOException {
        //log.info("Creating Directory : {}", directoryPath);
        Path downloadPath = Path.of(directoryPath);

        Path path = downloadPath.getParent();

        if (Files.isDirectory(downloadPath)) {
            path = downloadPath;
        }

        //log.info("Parent directory : {}", path);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}