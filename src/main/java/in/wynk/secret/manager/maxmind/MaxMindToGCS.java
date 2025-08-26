package in.wynk.secret.manager.maxmind;

import com.google.cloud.storage.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.tar.*;

public class MaxMindToGCS {
    private static final String MAXMIND_LICENSE_KEY = "KMav2W0eMTu5";
    private static final String GCS_BUCKET = System.getenv("GCS_BUCKET");
    private static final String[] PRODUCTS_LIST = {"GeoLite2-City", "GeoIP2-Country", "GeoIP2-ISP"};
    private static final Storage storage = StorageOptions.getDefaultInstance().getService();

    public static void main(String[] args) throws IOException {
        for (String product : PRODUCTS_LIST) {
            processProduct(product);
        }
    }

    private static void processProduct(String product) throws IOException {
        String dbHashUrl = String.format("https://download.maxmind.com/app/geoip_download?edition_id=%s&license_key=%s&suffix=tar.gz.sha256", product, MAXMIND_LICENSE_KEY);
        String dbDownloadUrl = String.format("https://download.maxmind.com/app/geoip_download?edition_id=%s&license_key=%s&suffix=tar.gz", product, MAXMIND_LICENSE_KEY);
        
        String dbHash = fetchContent(dbHashUrl).split(" ")[0];
        String gcsHashFile = "maxmind/db_version_hashes/" + product;
        String gcsFile = "maxmind/" + product + ".mmdb";
        
//        if (gcsFileExists(gcsHashFile, dbHash)) {
//            System.out.println(product + " DB file already exists. Skipping.");
//            return;
//        }
        
        File localTarGz = downloadFile(dbDownloadUrl, product + ".tar.gz");
        File extractedFile = extractMMDB(localTarGz);
        
        uploadToGCS(extractedFile, gcsFile);
        uploadToGCS(new ByteArrayInputStream(dbHash.getBytes()), gcsHashFile);
        
        System.out.println("Upload completed for: " + product);
    }

    private static String fetchContent(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return reader.readLine();
        }
    }

    private static boolean gcsFileExists(String gcsPath, String hash) throws IOException {
        Blob blob = storage.get(GCS_BUCKET, gcsPath);
        return blob != null && new String(blob.getContent()).trim().equals(hash);
    }

    private static File downloadFile(String fileUrl, String fileName) throws IOException {

        try {
            File outputFile = new File("/tmp/" + fileName);
            try (ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(fileUrl).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                FileChannel fileChannel = fileOutputStream.getChannel()) {
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            }
            return outputFile;
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            throw exception;
        }
    }

    private static File extractMMDB(File tarGzFile) throws IOException {
        File outputDir = new File("/tmp/unpacked");
        outputDir.mkdirs();
        try (FileInputStream fis = new FileInputStream(tarGzFile);
             GZIPInputStream gis = new GZIPInputStream(fis);
             TarArchiveInputStream tis = new TarArchiveInputStream(gis)) {
            TarArchiveEntry entry;
            while ((entry = tis.getNextTarEntry()) != null) {
                if (entry.getName().endsWith(".mmdb")) {
                    File outputFile = new File(outputDir, entry.getName());
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[8192];
                        int length;
                        while ((length = tis.read(buffer)) != -1) {
                            fos.write(buffer, 0, length);
                        }
                    }
                    return outputFile;
                }
            }
        }
        throw new IOException("MMDB file not found in archive.");
    }

    private static void uploadToGCS(File file, String gcsPath) throws IOException {
        BlobId blobId = BlobId.of(GCS_BUCKET, gcsPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
    }

    private static void uploadToGCS(InputStream data, String gcsPath) throws IOException {
        BlobId blobId = BlobId.of(GCS_BUCKET, gcsPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, data);
    }
}
