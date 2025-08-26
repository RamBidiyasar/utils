package in.wynk.secret.manager.maxmind;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import in.wynk.secret.manager.utils.GCSUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

@Service
public class MaxMindService {

    private static final String LICENSE_KEY = "KMav2W0eMTu5";
    private static final String BUCKET_NAME = "xstrm-test-preprod";
    private static final String DB_HASH_PATH = "maxmind/db_version_hashes/";
    private static final List<String> PRODUCTS_LIST = List.of("GeoLite2-City", "GeoIP2-Country", "GeoIP2-ISP");
    private static final int BUFFER_SIZE = 8192; // Increased buffer size for performance

    private final RestTemplate restTemplate = new RestTemplate();
    private final Storage storage = StorageOptions.getDefaultInstance().getService();
    private final ExecutorService executor = Executors.newFixedThreadPool(3); // Parallel processing

    //@PostConstruct
    public void init() {
        PRODUCTS_LIST.forEach(product -> executor.submit(() -> {
            try {
                fetchAndUploadDatabase(product);
            } catch (Exception e) {
                System.err.println("Error processing product " + product + ": " + e.getMessage());
            }
        }));
    }

    private void fetchAndUploadDatabase(String product) throws IOException {
        String dbHash = fetchDatabaseHash(product);

        if (dbAlreadyExists(dbHash, product)) {
            System.out.println("Database already exists in GCS for " + product + ". Skipping download.");
            return;
        }

        try (InputStream inputStream = downloadDatabaseStream(product)) {
            File extractedFile = extractDatabase(inputStream);
            uploadToGCS(extractedFile, "maxmind/" + product + ".mmdb");
            storeDbHash(dbHash, product);
        }
    }

    private String fetchDatabaseHash(String product) {
        String hashUrl = String.format(
            "https://download.maxmind.com/app/geoip_download?edition_id=%s&license_key=%s&suffix=tar.gz.sha256",
            product, LICENSE_KEY);

        return Optional.ofNullable(restTemplate.getForObject(hashUrl, String.class))
                       .map(response -> response.split(" ")[0].trim())
                       .orElseThrow(() -> new RuntimeException("Failed to fetch database hash for " + product));
    }

    private boolean dbAlreadyExists(String dbHash, String product) {
        try {
            Blob blob = storage.get(BUCKET_NAME, DB_HASH_PATH + product);
            return blob != null && dbHash.equals(new String(blob.getContent()));
        } catch (Exception e) {
            System.out.println("Error checking existing DB hash: " + e.getMessage());
        }
        return false;
    }

    private InputStream downloadDatabaseStream(String product) throws IOException {
        String downloadUrl = String.format(
            "https://download.maxmind.com/app/geoip_download?edition_id=%s&license_key=%s&suffix=tar.gz",
            product, LICENSE_KEY);
        return new URL(downloadUrl).openStream(); // Stream directly instead of writing temp file
    }

    private File extractDatabase(InputStream tarGzStream) throws IOException {
        Path tempDir = Files.createTempDirectory("/tmp/maxmind-extracted");

        try (GZIPInputStream gzipStream = new GZIPInputStream(new BufferedInputStream(tarGzStream, BUFFER_SIZE));
             TarArchiveInputStream tarInput = new TarArchiveInputStream(gzipStream)) {

            TarArchiveEntry entry;
            while ((entry = tarInput.getNextTarEntry()) != null) {
                Path entryPath = tempDir.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(entryPath), BUFFER_SIZE)) {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead;
                        while ((bytesRead = tarInput.read(buffer)) != -1) {
                            bos.write(buffer, 0, bytesRead);
                        }
                    }
                    if (entry.getName().endsWith(".mmdb")) {
                        System.out.println("Extracted: " + entryPath.toAbsolutePath());
                        return entryPath.toFile();
                    }
                }
            }
        }
        throw new IOException("No .mmdb file found in archive.");
    }

    private void uploadToGCS(File file, String gcsPath) throws IOException {
        GCSUtils.uploadFileToCompletePath(BUCKET_NAME, gcsPath, file);
        System.out.println("Uploaded to GCS: " + gcsPath);
    }

    private void storeDbHash(String dbHash, String product) {
        GCSUtils.uploadTextToDirectory(BUCKET_NAME, DB_HASH_PATH + product, dbHash);
        System.out.println("Stored new DB hash in GCS for " + product);
    }
}
