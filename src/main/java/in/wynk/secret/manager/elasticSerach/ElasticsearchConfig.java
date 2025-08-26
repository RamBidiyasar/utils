//package in.wynk.secret.manager.elasticSerach;
//
//import org.apache.http.HttpHost;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.apache.http.ssl.SSLContextBuilder;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.PostConstruct;
//import javax.net.ssl.SSLContext;
//import java.security.KeyManagementException;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//
//@Configuration
//public class ElasticsearchConfig {
//
//    private String elasticsearchHost = "10.169.24.16";
//
//    private String elasticsearchUsername = "elastic";
//
//    private String elasticsearchPassword = "yw+ePkl0xN-vhr*mHrsD";
//
////    @Bean
////    public RestHighLevelClient init() {
////        try {
////            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
////            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchUsername, elasticsearchPassword));
////
////            RestClientBuilder clientBuilder = RestClient.builder(
////                            new HttpHost(elasticsearchHost, 80, "http"))
////                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
////                            .setDefaultCredentialsProvider(credentialsProvider));
////            return new RestHighLevelClient(clientBuilder);
////        } catch (Exception e) {
////            System.out.println("con't build rest client for elasticSearch"+ e);
////            throw e;
////        }
////    }
//
//    @Bean
//    public RestHighLevelClient restHighLevelClient() {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchUsername, elasticsearchPassword));
//
//        RestClientBuilder clientBuilder = RestClient.builder(
//                        new HttpHost(elasticsearchHost, 9200, "http"))
//                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
//                        .setDefaultCredentialsProvider(credentialsProvider));
//        return new RestHighLevelClient(clientBuilder);
////        return new RestHighLevelClient(
////                RestClient.builder(
////                        new HttpHost("10.169.24.16", 9200, "http")
////                )
////        );
//    }
//
//
////    @Bean
////    public RestHighLevelClient client() {
////        try {
////            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
////            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchUsername, elasticsearchPassword));
////
////            RestClientBuilder builder = RestClient.builder(
////                            new HttpHost(elasticsearchHost, 9200, "https"))
////                    .setHttpClientConfigCallback(httpClientBuilder -> {
////                        try {
////                            // Create a custom SSLContext that ignores certificate verification
////                            final SSLContext sslContext = SSLContextBuilder.create()
////                                    .loadTrustMaterial((chain, authType) -> true)
////                                    .build();
////
////                            return httpClientBuilder
////                                    .setDefaultCredentialsProvider(credentialsProvider)
////                                    .setSSLContext(sslContext)
////                                    .setSSLHostnameVerifier((hostname, session) -> true); // Disable hostname verification as well
////                        } catch (NoSuchAlgorithmException | KeyManagementException e) {
////                            System.out.println("Not able to connect " + e.getMessage());
////                            throw new RuntimeException("Failed to create SSLContext", e);
////                        } catch (KeyStoreException e) {
////                            System.out.println("Not able to connect " + e.getMessage());
////                            throw new RuntimeException(e);
////                        }
////                    })
////                    .setRequestConfigCallback(requestConfigBuilder ->
////                            requestConfigBuilder.setConnectTimeout(5000)
////                                    .setSocketTimeout(60000)
////                                    .setConnectionRequestTimeout(0));
////
////            return new RestHighLevelClient(builder);
////        } catch (Exception e) {
////            System.out.println("Not able to connect " + e.getMessage());
////            throw e;
////        }
////    }
//}
//
