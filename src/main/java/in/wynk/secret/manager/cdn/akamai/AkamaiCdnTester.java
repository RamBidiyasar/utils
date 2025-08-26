package in.wynk.secret.manager.cdn.akamai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AkamaiCdnTester {

    private static final String CDN_URL = "https://iptv-live-akcdn.streamready.in/live/med8/jonack/dashd/jonack.mpd";
    private static final int TPS = 50; // 50 Transactions Per Second
    private static final int TEST_DURATION = 3; // Test for 2 minutes
    private static final HttpClient httpClient = HttpClient.newBuilder()
                                                           .connectTimeout(Duration.ofSeconds(5))
                                                           .build();

    // Atomic counters for tracking cache status
    private static final Map<String, AtomicInteger> cacheStatusCount = new ConcurrentHashMap<>();
    private static final AtomicInteger requestFailures = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
        Runnable requestTask = AkamaiCdnTester::sendRequest;

        // Schedule requests at 50 TPS (every 20ms)
        for (int i = 0; i < TPS; i++) {
            scheduler.scheduleAtFixedRate(requestTask, i * 20, 1000, TimeUnit.MILLISECONDS);
        }

        // Run for 2 minutes, then shutdown
        Thread.sleep(TEST_DURATION * 1000);
        scheduler.shutdownNow();

        // Print final results
        printResults();
    }

    private static void sendRequest() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create(CDN_URL))
                                             .timeout(Duration.ofSeconds(5))
                                             .GET()
                                             .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            // Extract Akamai-Cache-Status header
            String cacheStatus = response.headers()
                                         .firstValue("Akamai-Cache-Status")
                                         .orElse("Unknown");

            // Increment counter for this exact cache status value
            cacheStatusCount.computeIfAbsent(cacheStatus, k -> new AtomicInteger()).incrementAndGet();

        } catch (Exception e) {
            requestFailures.incrementAndGet();
        }
    }

    private static void printResults() {
        int totalRequests = cacheStatusCount.values().stream().mapToInt(AtomicInteger::get).sum();
        int totalFailures = requestFailures.get();
        totalRequests += totalFailures; // Include failed requests in total count

        System.out.println("\n===== Akamai CDN Performance Report =====");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Total Failures: " + totalFailures);

        for (Map.Entry<String, AtomicInteger> entry : cacheStatusCount.entrySet()) {
            String status = entry.getKey();
            int count = entry.getValue().get();
            double percentage = (count * 100.0) / totalRequests;
            System.out.printf("%s: %d (%.2f%%)\n", status, count, percentage);
        }
    }
}
