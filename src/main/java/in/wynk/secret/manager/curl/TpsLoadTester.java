package in.wynk.secret.manager.curl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;

public class TpsLoadTester {

    private static final int TPS = 200; // Target Transactions Per Second
    private static final String URL = "https://sync-preprod.wynk.in/v4/user/content/sync?appId=WEB&diff=true";

    private static final String REQUEST_BODY = """
        {
            "recents": {"add": [], "remove": []},
            "favourites": {
                "add": [
                    {
                        "contentId": "DOCUBAY_MOVIE_4386",
                        "lastUpdatedTimeStamp": 1749548429560,
                        "lastWatchedPosition": 0,
                        "langId": "",
                        "subtitle": ""
                    }
                ],
                "remove": []
            }
        }
        """;

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20);
        Runnable task = () -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("accept", "application/json, text/plain, */*")
                    .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
                    .header("cache-control", "no-cache")
                    .header("content-type", "application/json")
                    .header("origin", "https://preprod.airtelxstream.in")
                    .header("pragma", "no-cache")
                    .header("priority", "u=1, i")
                    .header("referer", "https://preprod.airtelxstream.in/")
                    .header("sec-ch-ua", "\"Chromium\";v=\"136\", \"Google Chrome\";v=\"136\", \"Not.A/Brand\";v=\"99\"")
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", "\"macOS\"")
                    .header("sec-fetch-dest", "empty")
                    .header("sec-fetch-mode", "cors")
                    .header("sec-fetch-site", "cross-site")
                    .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36")
                    .header("x-atv-ab", "57924:1|36795:1|63817:1|94798:1|31112:1|104526:1|35626:0|60640:0|41993:0|63696:1")
                    .header("x-atv-did", "89bc163f-e3cd-4e79-949b-1b3d285b0ded|BROWSER|WEBOS|10.15.7|74|74.2.5|mac|mac")
                    .header("x-atv-traceid", "9f8c34fc-ca02-4055-88ea-5e61b772d185")
                    .header("x-atv-utkn", "gUlq9_NWl24XoyJGT0:uSbPRfz7SmlXipMSs8SCByhz8f8=")
                    .POST(HttpRequest.BodyPublishers.ofString(REQUEST_BODY))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                  .thenAccept(response -> {
                      System.out.println("Response code: " + response.statusCode());
                  })
                  .exceptionally(e -> {
                      System.err.println("Request failed: " + e.getMessage());
                      return null;
                  });
        };

        long periodNanos = 1_000_000_000L / TPS; // Period between requests in nanoseconds
        for (int i = 0; i < TPS; i++) {
            scheduler.scheduleAtFixedRate(task, i * periodNanos, TPS * periodNanos, TimeUnit.NANOSECONDS);
        }

        // Let it run for 60 seconds, then shutdown
        Thread.sleep(60_000);
        scheduler.shutdownNow();
    }
}
