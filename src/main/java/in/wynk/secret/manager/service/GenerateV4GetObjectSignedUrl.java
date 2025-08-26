package in.wynk.secret.manager.service;


import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class GenerateV4GetObjectSignedUrl {
    /**
     * Signing a URL requires Credentials which implement ServiceAccountSigner. These can be set
     * explicitly using the Storage.SignUrlOption.signWith(ServiceAccountSigner) option. If you don't,
     * you could also pass a service account signer to StorageOptions, i.e.
     * StorageOptions().newBuilder().setCredentials(ServiceAccountSignerCredentials). In this example,
     * neither of these options are used, which means the following code only works when the
     * credentials are defined via the environment variable GOOGLE_APPLICATION_CREDENTIALS, and those
     * credentials are authorized to sign a URL. See the documentation for Storage.signUrl for more
     * details.
     */

    public static void main(String[] args) {


        generateV4GetObjectSignedUrl("prj-wynk-pre-xstrm-svc-01", "xstrm-test-preprod", "tvepisodes_105.json");
    }
    public static void generateV4GetObjectSignedUrl(
            String projectId, String bucketName, String objectName) throws StorageException {

        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();

        // Define resource
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build();
         URL url = storage.signUrl(blobInfo,5, TimeUnit.MINUTES);

//        URL url =
//                storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.signWith(new ServiceAccountSigner() {
//                    @Override
//                    public String getAccount() {
//                        return "ram.bidiyasar@airtel.com";
//                    }
//
//                    @Override
//                    public byte[] sign(byte[] bytes) {
//                        long freeMemory = Runtime.getRuntime().freeMemory();
//
//                        // Convert the long value to a byte array
//                        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
//                        buffer.putLong(freeMemory);
//
//                        return buffer.array();
//                    }
//                }), Storage.SignUrlOption.httpMethod(HttpMethod.GET));

        System.out.println("Generated GET signed URL:");
        System.out.println(url);
        System.out.println("You can use this URL with any user agent, for example:");
        System.out.println("curl '" + url + "'");
    }
}
