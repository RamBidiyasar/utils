package in.wynk.secret.manager.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpRequestScheduler {

    private static final int TPS = 10; // Transactions per second
    private static final int DURATION_IN_HOURS = 1; // 1 hour
    private static final int TOTAL_DURATION_IN_SECONDS = DURATION_IN_HOURS * 3600;

    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(TPS);

        // Schedule at 1/3 second interval (3 TPS)
        long initialDelay = 0;
        long period = 1000 / TPS;

        Runnable task = () -> {
            try {
                hitAllUrls();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Schedule the task at fixed intervals for the specified duration
        executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);

        // Shutdown the executor after 1 hour
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            executor.shutdown();
            System.out.println("Completed all requests.");
        }, TOTAL_DURATION_IN_SECONDS, TimeUnit.SECONDS);
    }

    private static void hitAllUrls() throws Exception {
        // Hit each URL in parallel or sequence

        // 1. POST request to http://discovery-client.internal.airtel.tv/get/package/something
        //sendPostRequest("http://discovery-client.internal.airtel.tv/get/package/something", null);

        // 2. POST request to http://notification-management.internal.airtel.tv/notification/mock/save
        String jsonPayload = "{\"data\":\"testing some thing\"}";
        //sendPostRequest("http://notification-management.internal.airtel.tv/notification/mock/save", jsonPayload);

        // 3. GET request to http://packagelayout.internal.airtel.tv/app/v1/package?id=sdfsdf
        //sendGetRequest("http://packagelayout.internal.airtel.tv/app/v1/package?id=sdfsdf");

        // 4. POST request to http://user.internal.airtel.tv/test/notification/ios/send
        //sendPostRequest("http://user.internal.airtel.tv/test/notification/ios/send", null);

        // 5. GET request to http://watch.internal.airtel.tv/v2/user/onboardingPref?id=sdfsdf
        sendGetRequest("http://watch.internal.airtel.tv/v2/user/onboardingPref?id=sdfsdf");

//        sendGetRequest("http://contentconsumer.internal.airtel.tv/v1/consumer/trigger");

        // 6. POST request to http://partner-sso.internal.airtel.tv/partner-sso/auth/sso/generateToken
      //  String didHeader = "testing";
        //sendPostRequestWithHeader("http://partner-sso.internal.airtel.tv/partner-sso/auth/sso/generateToken", null, "x-atv-did", didHeader);
    }

    private static void sendGetRequest(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET Response from " + url + ": " + response.statusCode());
    }

    private static void sendPostRequest(String url, String jsonBody) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody());

        if (jsonBody != null) {
            builder = builder.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                             .header("Content-Type", "application/json");
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST Response from " + url + ": " + response.statusCode());
    }

    private static void sendPostRequestWithHeader(String url, String jsonBody, String headerKey, String headerValue) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header(headerKey, headerValue);

        if (jsonBody != null) {
            builder = builder.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                             .header("Content-Type", "application/json");
        }

        HttpRequest request = builder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("POST with Header Response from " + url + ": " + response.statusCode());
    }
}
