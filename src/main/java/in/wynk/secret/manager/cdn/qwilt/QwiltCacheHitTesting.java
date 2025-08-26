package in.wynk.secret.manager.cdn.qwilt;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class QwiltCacheHitTesting {

    private static final String CDN_URL = "https://iptv-prd-new-main.dlt.qwilted-cds.cqloud.com/live/med15/sony_max_hd/vdashhd/sony_max_hd.mpd";
    private static final int TPS = 20; // concurrent requests per second
    private static final int TEST_DURATION = 60; // Test duration in seconds
    private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    private static final Map<String, AtomicInteger> cacheStatusCount = new ConcurrentHashMap<>();
    private static final AtomicInteger requestFailures = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ExecutorService requestExecutor = Executors.newFixedThreadPool(TPS); // Pool for parallel execution

        Runnable requestBatchTask = () -> {
            for (int i = 0; i < TPS; i++) {
                requestExecutor.submit(QwiltCacheHitTesting::sendRequest);
            }
        };

        // Schedule 50 parallel requests every second
        scheduler.scheduleAtFixedRate(requestBatchTask, 0, 1, TimeUnit.SECONDS);

        // Run for the given duration
        Thread.sleep(TEST_DURATION * 1000);

        // Shut down
        scheduler.shutdown();
        requestExecutor.shutdown();
        requestExecutor.awaitTermination(5, TimeUnit.SECONDS);

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

            String cacheStatus = response.headers()
                                         .firstValue("Ocn-Cache-Status")
                                         .orElse("Unknown");

            cacheStatusCount.computeIfAbsent(cacheStatus, k -> new AtomicInteger()).incrementAndGet();

        } catch (Exception e) {
            requestFailures.incrementAndGet();
        }
    }

    private static void printResults() {
        int totalRequests = cacheStatusCount.values().stream().mapToInt(AtomicInteger::get).sum();
        int totalFailures = requestFailures.get();
        totalRequests += totalFailures;

        System.out.println("===== Airtel Edge CDN Performance Report =====");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Total Failures: " + totalFailures);
        System.out.println("Total Duration: " + TEST_DURATION);

        for (Map.Entry<String, AtomicInteger> entry : cacheStatusCount.entrySet()) {
            String status = entry.getKey();
            int count = entry.getValue().get();
            double percentage = (count * 100.0) / totalRequests;
            System.out.printf("%s: %d (%.2f%%)\n", status, count, percentage);
        }
    }
}