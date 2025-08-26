package in.wynk.secret.manager.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PostRequestSender {
    private static final String URL = "https://sync-preprod.wynk.in/v4/user/content/sync?appId=WEB&diff=true";
    private static final String JSON_BODY = """
            {"recents":{"add":[],"remove":[]},"favourites":{"add":[],"remove":[]}}
            """;

    public static void main(String[] args) {
        HttpClient client = HttpClient.newBuilder()
                                      .connectTimeout(Duration.ofSeconds(10))
                                      .build();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        int requestsPerSecond = 25;
        long interval = 1000 / requestsPerSecond; // Milliseconds per request

        for (int i = 0; i < requestsPerSecond; i++) {
            executor.execute(() -> {
                while (true) {
                    sendPostRequest(client);
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }));
    }

    private static void sendPostRequest(HttpClient client) {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(URL))
                                         .header("x-atv-utkn", "gUlq9_NWl24XoyJGT0:LTx3LAZ96dHrqkiclIS22mQSm4U=")
                                         .header("sec-ch-ua-platform", "\"macOS\"")
                                         .header("Referer", "https://preprod.airtelxstream.in/")
                                         .header("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
                                         .header("sec-ch-ua-mobile", "?0")
                                         .header("x-atv-ab", "57924:1|36795:1|63817:1|94798:1|31112:1|104526:1|35626:0|60640:0|41993:0|63696:1")
                                         .header("x-atv-did", "bb09e846-5b0e-479a-9927-1ccc2dce58df|BROWSER|WEBOS|10.15.7|75|75.0.0|mac|mac")
                                         .header("x-atv-traceid", "bddf114d-565d-4b7f-ba26-9d3f8bd949a3")
                                         .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                                         .header("Accept", "application/json, text/plain, */*")
                                         .header("Content-Type", "application/json")
                                         .POST(HttpRequest.BodyPublishers.ofString(JSON_BODY))
                                         .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response: " + response.statusCode() + " - " + response.body());
        } catch (Exception e) {
            System.err.println("Request failed: " + e.getMessage());
        }
    }
}
