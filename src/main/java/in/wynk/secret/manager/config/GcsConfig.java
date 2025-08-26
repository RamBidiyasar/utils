//package in.wynk.secret.manager.config;
//
//import com.google.cloud.storage.Storage;
//import com.google.cloud.storage.StorageOptions;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GcsConfig {
//
//    @Bean
//    public Storage storage() {
//        // Load credentials and create a storage instance
//        StorageOptions options = StorageOptions.newBuilder()
//                .setProjectId("prj-wynk-pre-xstrm-svc-01")
//                .build();
//
//        return options.getService();
//    }
//}
//
