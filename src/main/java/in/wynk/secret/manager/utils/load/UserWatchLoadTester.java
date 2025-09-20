package in.wynk.secret.manager.utils.load;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UserWatchLoadTester {

    private static final String URL_DC1 = "http://user-watch-dc1.internal.airtel.tv/get/userWatchInfo";
    private static final String URL_DC2 = "http://user-watch-dc2.internal.airtel.tv/get/userWatchInfo";

    // Target TPS
    private static final int TARGET_TPS = 10;

    // Divide TPS equally across both endpoints (15 TPS each)
    private static final int TPS_PER_ENDPOINT = TARGET_TPS / 2;

    // HttpClient (reusable)
    private final HttpClient client;

    public UserWatchLoadTester() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    private void hitEndpoint(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Hit: " + url + " | Status: " + response.statusCode());
        } catch (Exception e) {
            System.err.println("Error hitting " + url + " -> " + e.getMessage());
        }
    }

    public void startLoadTest() {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Interval in ms between requests per endpoint
        long intervalMs = 1000 / TPS_PER_ENDPOINT;

        Runnable task = () -> {
            while (true) {
                executor.submit(() -> hitEndpoint(URL_DC1));
                executor.submit(() -> hitEndpoint(URL_DC2));
                try {
                    Thread.sleep(intervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };

        // Start continuous load
        new Thread(task).start();
    }

    public static void main(String[] args) throws InterruptedException {
        UserWatchLoadTester tester = new UserWatchLoadTester();
        tester.startLoadTest();

        // Run test for 1 minute then exit
        Thread.sleep(TimeUnit.MINUTES.toMillis(10));
        System.out.println("Load test completed.");
        System.exit(0);
    }
}
