package in.wynk.secret.manager.service;

import com.google.auth.Credentials;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.cloud.storage.*;
import in.wynk.secret.manager.utils.GcpUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.springframework.boot.context.properties.bind.Bindable.mapOf;



public class Test {


    private static final  Storage storage = StorageOptions.getDefaultInstance().getService();


    public static void main(String[] args) throws IOException {


        String bucket = "xstrm-test-preprod";
        String fileName = "tvepisodes_105.json";

//        Blob blob = storage.get(BlobId.of(bucket, fileName));
//        System.out.println(blob.getBlobId());

//        boolean b = blob.getMetadata() != null && blob.getMetadata().get("object") != null;

//        boolean c = ObjectUtils.isNotEmpty(blob.getMetadata()) && ObjectUtils.isNotEmpty(blob.getMetadata().get("data"));
//        System.out.println(c);


//        System.out.println(b);

//        GcpUtils.moveFile(bucket,fileName,bucket,"TEST/tvepisodes_105.json");
//        String nameOfFile = GcpUtils.getFileNameFromPath("gs://xstrm-test-preprod/TEST/artist_img_face.jpg");
//        System.out.println(nameOfFile);

//        GcpUtils.cleanBucketsDirectory(bucket,"TEST/");

//        BufferedReader bufferedReader = GcpUtils.getReaderForGcsFile(bucket,fileName);
//        assert bufferedReader != null;
//        System.out.println(bufferedReader.lines().toList());
//        try {
//            bufferedReader.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        System.out.println(GcpUtils.generatePreSignedUrl(bucket, fileName, 5000L));


//        System.out.println(GcpUtils.getLatestFileWithPrefix(bucket,"TEST/"));

//        Storage storage = StorageOptions.getDefaultInstance().getService();
//        BlobInfo blobInfo = BlobInfo.newBuilder(bucket, fileName).build();



//        System.out.println(GcpUtils.getAllFilesWithPrefix(bucket,"TEST/"));


//        gs://wynk-location/maxmind/GeoLite2-City.mmdb


//        System.out.println(GcpUtils.getAllFilesWithPrefix("wynk-location","/"));
//        long a = System.currentTimeMillis();
//        GcpUtils.saveToLocal("wynk-location-xstream-preprod","maxmind/GeoLite2-City.mmdb","/tmp/logs/testing");
//        System.out.println(System.currentTimeMillis()-a);
//
//


//        long b = System.currentTimeMillis();
//        GcpUtils.saveToLocal("wynk-location-xstream-preprod","maxmind/GeoLite2-City.mmdb","testing.mmdb");
//        System.out.println(System.currentTimeMillis()-b);


        
        long c = System.currentTimeMillis();
        GcpUtils.saveToLocal("wynk-location-xstream-preprod","maxmind/GeoLite2-City.mmdb","/tmp/maxmind/2024-08-30-maxmind-geolite2.mmdb");
        System.out.println(System.currentTimeMillis()-c);


//
//
//        System.out.println(System.currentTimeMillis()-b);




//


    }






}
