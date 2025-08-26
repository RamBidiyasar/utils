package in.wynk.secret.manager.cdn.akamai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AkamaiCdnTesterV2 {
    private static final String CDN_URL = "https://iptv-live-akcdn.streamready.in/live/med6/dd_national_hd/dashd/dd_national_hd.mpd";
    private static final int TPS = 20; // 50 Transactions Per Second
    private static final int TEST_DURATION = 30; // Test duration in seconds

    private static final HttpClient httpClient = HttpClient.newBuilder()
                                                           .connectTimeout(Duration.ofSeconds(5))
                                                           .build();

    // Atomic counters for tracking cache performance
    private static final AtomicInteger totalRequests = new AtomicInteger();
    private static final AtomicInteger childHits = new AtomicInteger();
    private static final AtomicInteger childMisses = new AtomicInteger();
    private static final AtomicInteger parentHits = new AtomicInteger();
    private static final AtomicInteger parentMisses = new AtomicInteger();
    private static final AtomicInteger originRequests = new AtomicInteger();
    private static final AtomicInteger otherStatuses = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
        Runnable requestTask = AkamaiCdnTesterV2::sendRequest;

        // Schedule requests at 50 TPS (every 20ms)
        for (int i = 0; i < TPS; i++) {
            scheduler.scheduleAtFixedRate(requestTask, i * 20, 1000, TimeUnit.MILLISECONDS);
        }

        // Run for the given duration
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
            String cacheStatus = response.headers().firstValue("Akamai-Cache-Status").orElse("Unknown");
            totalRequests.incrementAndGet();
            processCacheStatus(cacheStatus);

        } catch (Exception e) {
            System.err.println("Request failed: " + e.getMessage());
        }
    }

    private static void processCacheStatus(String cacheStatus) {
        if (cacheStatus.equals("Unknown")) {
            otherStatuses.incrementAndGet();
            return;
        }

        boolean childMiss = false, parentMiss = false;

        for (String part : cacheStatus.split(",")) {
            part = part.trim();
            if (part.startsWith("Hit from child")) {
                childHits.incrementAndGet();
            } else if (part.startsWith("Miss from child")) {
                childMisses.incrementAndGet();
                childMiss = true;
            } else if (part.startsWith("Hit from parent")) {
                parentHits.incrementAndGet();
            } else if (part.startsWith("Miss from parent")) {
                parentMisses.incrementAndGet();
                parentMiss = true;
            }
        }

        // If both child and parent missed, request went to origin
        if (childMiss && parentMiss) {
            originRequests.incrementAndGet();
        }
    }

    private static void printResults() {
        int total = totalRequests.get();
        int originHits = originRequests.get();
        int childHitCount = childHits.get();
        int childMissCount = childMisses.get();
        int parentHitCount = parentHits.get();
        int parentMissCount = parentMisses.get();
        int otherCount = otherStatuses.get();

        System.out.println("\n===== Akamai CDN Performance Report =====");
        System.out.println("Total Requests: " + total);
        System.out.println("Requests reaching origin: " + originHits + " (" + percentage(originHits, total) + "%)");

        System.out.println("\n--- Child Cache ---");
        System.out.println("Hit: " + childHitCount + " (" + percentage(childHitCount, total) + "%)");
        System.out.println("Miss: " + childMissCount + " (" + percentage(childMissCount, total) + "%)");

        System.out.println("\n--- Parent Cache ---");
        System.out.println("Hit: " + parentHitCount + " (" + percentage(parentHitCount, total) + "%)");
        System.out.println("Miss: " + parentMissCount + " (" + percentage(parentMissCount, total) + "%)");

        System.out.println("\n--- Overall ---");
        int totalHits = childHitCount + parentHitCount;
        int totalMisses = childMissCount + parentMissCount;
        System.out.println("Total Hits: " + totalHits + " (" + percentage(totalHits, total) + "%)");
        System.out.println("Total Misses: " + totalMisses + " (" + percentage(totalMisses, total) + "%)");

        System.out.println("\n--- Other Cache Statuses ---");
        System.out.println("Unknown: " + otherCount + " (" + percentage(otherCount, total) + "%)");
    }

    private static String percentage(int count, int total) {
        return total == 0 ? "0.00%" : String.format("%.2f%%", (count * 100.0) / total);
    }
}
