//package in.wynk.secret.manager.elasticSerach;
//
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.indices.GetIndexRequest;
//import org.elasticsearch.client.indices.GetIndexResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//
//@Service
//public class ElasticsearchService {
//
//    @Autowired
//    private  RestHighLevelClient client;
//
//    public List<String> getAllIndexes() throws IOException {
//        try {
//            // Create a GetIndexRequest to match all indexes
//            GetIndexRequest request = new GetIndexRequest("*");
////
////            // Execute the request
//            GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
////
////            // Return the list of index names
//            return Arrays.asList(response.getIndices());
//        }catch (Exception e){
//            System.out.println("ERRor while connecting to es : "+ e.getMessage());
//            throw  e;
//        }
//    }
//}
//
