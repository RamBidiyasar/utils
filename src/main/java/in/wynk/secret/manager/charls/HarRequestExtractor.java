package in.wynk.secret.manager.charls;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Iterator;

public class HarRequestExtractor {

    public static void main(String[] args) throws Exception {
        File harFile = new File("traffic.har");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(harFile);

        JsonNode entries = root
                .path("log")
                .path("entries");

        if (!entries.isArray()) {
            System.out.println("No entries found in HAR file");
            return;
        }

        for (JsonNode entry : entries) {
            JsonNode request = entry.path("request");

            String method = request.path("method").asText();
            String url = request.path("url").asText();

            System.out.println("=================================");
            System.out.println("Method : " + method);
            System.out.println("URL    : " + url);

            // Headers
            System.out.println("Headers:");
            for (JsonNode header : request.path("headers")) {
                System.out.println(
                        header.path("name").asText() + ": " +
                        header.path("value").asText()
                );
            }

            // Query Params
            if (request.has("queryString")) {
                System.out.println("Query Params:");
                for (JsonNode q : request.path("queryString")) {
                    System.out.println(
                            q.path("name").asText() + "=" +
                            q.path("value").asText()
                    );
                }
            }

            // Body (POST/PUT)
            JsonNode postData = request.path("postData");
            if (!postData.isMissingNode()) {
                System.out.println("Body:");
                System.out.println(postData.toPrettyString());
            }
        }
    }
}
