package in.wynk.secret.manager.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Druid  {

    private static final Logger logger = LoggerFactory.getLogger(Druid.class);
    private static final String STATUS = "status";
    private static final String CURRENT_TIME = "current_time";
    private static final Storage storage = StorageOptions.getDefaultInstance().getService();

    //    @Value("${storage.bucket.name}")
    private static final String bucketName = "xstrm-test-preprod";

    public static void main(String[] args) throws IOException {
        //System.out.println(copyFiles("moved/","druid"));
        updateCopiedFileStatus("moved/", Collections.singletonList("test-file.txt"),"Running");
    }

    //@Override
    static List<String> copyFiles(String sourceFolder, String destinationFolder) throws IOException {
        List<String> copiedFiles = new ArrayList<>();
        try {
            String sourcePrefix = sourceFolder.endsWith("/") ? sourceFolder : sourceFolder + "/";
            String destinationPrefix = destinationFolder.endsWith("/") ? destinationFolder : destinationFolder + "/";

            Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(sourcePrefix));

            for (Blob blob : blobs.iterateAll()) {
                String sourceKey = blob.getName();
                if (!hasStatusMetadata(sourceKey)) {
                    String relativeSourceKey = sourceKey.substring(sourcePrefix.length());
                    String destinationKey = destinationPrefix + relativeSourceKey;
                    updateMetadataAndCopy(sourceKey, destinationKey);
                    copiedFiles.add(relativeSourceKey);
                }
            }
        } catch (StorageException se) {
            logger.error("StorageException ", se);
            throw new IOException("Failed to list objects in bucket: " + bucketName, se);
        }
        return copiedFiles;
    }

    //@Override
    static void updateCopiedFileStatus(String sourceFolder, List<String> copiedFiles, String newStatus) throws IOException {
        logger.info("Status update call for : {0} with status : {1}", copiedFiles, newStatus);
        for (String fileName : copiedFiles) {
            String key = sourceFolder + fileName;
            updateStatusMetadata(key, newStatus);
        }
    }

    //@Override
    static void deleteFiles(String sourceFolder) throws IOException {
        try {
            String sourcePrefix = sourceFolder.endsWith("/") ? sourceFolder : sourceFolder + "/";

            Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(sourcePrefix));

            for (Blob blob : blobs.iterateAll()) {
                String sourceKey = blob.getName();
                storage.delete(BlobId.of(bucketName, sourceKey));
            }
        } catch (StorageException se) {
            logger.error("StorageException ", se);
            throw new IOException("Failed to list or delete objects in bucket: " + bucketName, se);
        }
    }

    static boolean hasStatusMetadata(String key) {
        try {
            Blob blob = storage.get(BlobId.of(bucketName, key));
            return ObjectUtils.isNotEmpty(blob.getMetadata()) && ObjectUtils.isNotEmpty(blob.getMetadata().get(STATUS));
        } catch (StorageException se) {
            logger.error("StorageException while checking metadata ", se);
            return false;
        }
    }

    static void updateMetadataAndCopy(String sourceKey, String destinationKey) throws IOException {
        try {
            Blob sourceBlob = storage.get(BlobId.of(bucketName, sourceKey));

            if (sourceBlob == null) {
                throw new IOException("Source blob not found for key: " + sourceKey);
            }

            Map<String, String> updatedMetadata = new HashMap<>();
            updatedMetadata.put(STATUS, "RUNNING");
            updatedMetadata.put(CURRENT_TIME, new Date().toString());

            // Update the metadata of the source object
            BlobInfo updatedBlobInfo = sourceBlob.toBuilder().setMetadata(updatedMetadata).build();
            storage.update(updatedBlobInfo);

            // Copy the updated source object to the destination
            storage.copy(Storage.CopyRequest.newBuilder()
                    .setSource(BlobId.of(bucketName, sourceKey))
                    .setTarget(BlobId.of(bucketName, destinationKey))
                    .build());

        } catch (StorageException se) {
            logger.error("StorageException ", se);
            throw new IOException("Failed to update metadata or copy file: " + sourceKey, se);
        }
    }


    static void updateStatusMetadata(String key, String newStatus) throws IOException {
        try {
            Blob blob = storage.get(BlobId.of(bucketName, key));
            if (blob == null) {
                throw new IOException("Blob not found for key: " + key);
            }

            Map<String, String> updatedMetadata = new HashMap<>(Objects.requireNonNull(blob.getMetadata()));
            updatedMetadata.put(STATUS, newStatus);
            logger.info("Update status of : {0} to {1}", key, newStatus);

            // Update the metadata of the same object
            blob.toBuilder().setMetadata(updatedMetadata).build().update();
            storage.update(blob);
        } catch (StorageException se) {
            logger.error("StorageException ", se);
            throw new IOException("Failed to update metadata for file: " + key, se);
        }
    }

}
