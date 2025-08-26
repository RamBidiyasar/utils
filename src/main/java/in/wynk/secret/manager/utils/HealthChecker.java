package in.wynk.secret.manager.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class HealthChecker {

    public static void main(String[] args) {
        //prod
        // List of URLs with all services
//        List<String> urls = List.of(
//                "notification.internal.airtel.tv",
//                "notification-management.internal.airtel.tv",
//                "notify-manager.internal.airtel.tv",
//                "packagelayout.internal.airtel.tv",
//                "package.internal.airtel.tv",
//                "partner-sso.internal.airtel.tv",
//                "play.internal.airtel.tv",
//                "search.internal.airtel.tv",
//                "sync.internal.airtel.tv",
//                "user.internal.airtel.tv",
//                "watch.internal.airtel.tv",
//                "batch.internal.airtel.tv",
//                "batch-api.internal.airtel.tv",
//                "cms-api.internal.airtel.tv",
//                "contentapi.internal.airtel.tv",
//                "discovery-client.internal.airtel.tv",
//                "epg.internal.airtel.tv",
//                "events.internal.airtel.tv",
//                "layoutapi.internal.airtel.tv"
//        );

        //preprod
        List<String> urls = List.of(
                "batch-api-preprod.internal.airtel.tv",
                "batch-preprod-gcp.internal.airtel.tv",
                "cms-api-preprod.internal.airtel.tv",
                "contentapi-preprod.internal.airtel.tv",
                "discovery-client-preprod.internal.airtel.tv",
                "epg-preprod.internal.airtel.tv",
                "event-preprod.internal.airtel.tv",
                "layoutapi-preprod.internal.airtel.tv",
                "notification-management-preprod.internal.airtel.tv",
                "notification-preprod.internal.airtel.tv",
                "notify-manager-preprod.internal.airtel.tv",
               "packagelayout-preprod.internal.airtel.tv",
                "partner-sso-preprod.internal.airtel.tv",
                "play-preprod.internal.airtel.tv",
                "search-preprod.internal.airtel.tv",
                "sync-preprod.internal.airtel.tv",
                "user-preprod.internal.airtel.tv",
                "watch-preprod.internal.airtel.tv"
        );

        List<String> healthyServices = new ArrayList<>();
        List<String> unhealthyServices = new ArrayList<>();

        for (String url : urls) {
            if (checkHealth(url)) {
                healthyServices.add(url);
            } else {
                unhealthyServices.add(url);
            }
        }

        // Print the results
        System.out.println("Healthy Services:");
        for (String service : healthyServices) {
            System.out.println("- " + service);
        }

        System.out.println("\nUnhealthy Services:");
        for (String service : unhealthyServices) {
            System.out.println("- " + service);
        }
    }

    private static boolean checkHealth(String url) {
        try {
            URL healthUrl = new URL("http://" + url + "/actuator/health");
            HttpURLConnection connection = (HttpURLConnection) healthUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5 seconds timeout
            connection.setReadTimeout(5000);    // 5 seconds timeout

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                String jsonResponse = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                        .lines()
                        .reduce("", String::concat);

                // Check if status is "UP"
                if (jsonResponse.contains("\"status\":\"UP\"")) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + " : " + url);
            // Log or handle the exception if needed
        }
        return false;
    }
}
