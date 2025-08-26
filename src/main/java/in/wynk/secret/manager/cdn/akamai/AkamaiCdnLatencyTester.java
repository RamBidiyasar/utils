package in.wynk.secret.manager.cdn.akamai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AkamaiCdnLatencyTester {

    private static final String CDN_URL = "https://iptv-live-akcdn.streamready.in/live/med1/aaj_tak/vdashd/aaj_tak.mpd";
    private static final int TPS = 50; // 50 concurrent requests per second
    private static final int TEST_DURATION = 20; // Test duration in seconds
    private static final HttpClient httpClient = HttpClient.newBuilder()
                                                           .connectTimeout(Duration.ofSeconds(5))
                                                           .build();

    private static final AtomicInteger requestFailures = new AtomicInteger();
    private static final List<Long> latencies = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ExecutorService requestExecutor = Executors.newFixedThreadPool(TPS);

        Runnable requestBatchTask = () -> {
            for (int i = 0; i < TPS; i++) {
                requestExecutor.submit(AkamaiCdnLatencyTester::sendRequest);
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
                                             .header("Cookie", "Edge-Cache-Cookie=URLPrefix=aHR0cHM6Ly9pcHR2LWxpdmUtcGxheTEuc3RyZWFtcmVhZHkuaW4vbGl2ZS9tZWQ0L3N0YXJfbW92aWVzX2hkL2Rhc2hkLw==:Expires=1740760200:KeyName=iptv-prod-media-cdn-keys:Signature=dJXdUTC3wA71-qW3b7dgUcrR-DbqBRmb88K6HTo3xPkbka45iDwpWNQeYOLa_j5YmXI5cLmswT9U8M9jL01CDQ==")
                                             .GET()
                                             .build();

            long startTime = System.nanoTime();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            long endTime = System.nanoTime();

            long latencyMs = (endTime - startTime) / 1_000_000; // Convert nanoseconds to milliseconds
            latencies.add(latencyMs);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            requestFailures.incrementAndGet();
        }
    }


    private static void printResults() {
        int totalRequests = latencies.size() + requestFailures.get();
        int totalFailures = requestFailures.get();

        System.out.println("\n===== Akamai CDN Latency Report =====");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Total Failures: " + totalFailures);

        if (!latencies.isEmpty()) {
            Collections.sort(latencies);

            long minLatency = latencies.get(0);
            long maxLatency = latencies.get(latencies.size() - 1);
            double avgLatency = latencies.stream().mapToLong(Long::longValue).average().orElse(0.0);
            long p95Latency = latencies.get((int) (latencies.size() * 0.95) - 1);
            long p99Latency = latencies.get((int) (latencies.size() * 0.99) - 1);

            System.out.printf("Min Latency: %d ms\n", minLatency);
            System.out.printf("Max Latency: %d ms\n", maxLatency);
            System.out.printf("Avg Latency: %.2f ms\n", avgLatency);
            System.out.printf("P95 Latency: %d ms\n", p95Latency);
            System.out.printf("P99 Latency: %d ms\n", p99Latency);
        }
    }
}