package in.wynk.secret.manager.utils.rate.limitter;

import com.google.common.util.concurrent.RateLimiter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadTester {

    private static final int TPS = 200;
    private static final int DURATION_SECONDS = 60 * 3; // Run for 1 minute
    private static final String URL = "https://apimaster-preprod.wynk.in/v2/user/config?appId=WEB&cache=false";

    private static final String BODY = ""; // If required, add POST body here.

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        RateLimiter rateLimiter = RateLimiter.create(TPS);

        HttpClient httpClient = HttpClient.newHttpClient();

        long endTime = System.currentTimeMillis() + DURATION_SECONDS * 1000L;

        while (System.currentTimeMillis() < endTime) {
            rateLimiter.acquire();

            executor.submit(() -> {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(URL))
                            .POST(HttpRequest.BodyPublishers.ofString(BODY))
                            .header("x-atv-platform", "WEBOS")
                            .header("x-atv-utkn", "gUlq9_NWl24XoyJGT0:irNdzji5glVZYcQ4dR5A4bbgoqs=")
                            .header("sec-ch-ua-platform", "\"macOS\"")
                            .header("Referer", "https://preprod.airtelxstream.in/")
                            .header("sec-ch-ua", "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"")
                            .header("sec-ch-ua-mobile", "?0")
                            .header("x-atv-ab", "57924:1|36795:1|63817:1|94798:1|31112:1|104526:1|35626:0|60640:0|41993:0|63696:1")
                            .header("x-atv-did", "89bc163f-e3cd-4e79-949b-1b3d285b0ded|BROWSER|WEBOS|10.15.7|74|74.2.5|mac|mac")
                            .header("x-atv-traceid", "1a9445d9-140e-4747-b0cb-68f66e0f788f")
                            .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36")
                            .header("Accept", "application/json, text/plain, */*")
                            .build();

                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("Status: " + response.statusCode());
                } catch (Exception e) {
                    System.err.println("Request failed: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        System.out.println("Load test finished.");
    }
}
