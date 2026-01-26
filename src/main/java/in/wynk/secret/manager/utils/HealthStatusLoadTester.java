package in.wynk.secret.manager.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class HealthStatusLoadTester {

    private static final String TARGET_URL = "https://play.airtel.tv/v1/health/status";
    private static final int TOTAL_REQUESTS = 1000;
    private static final int TARGET_TPS = 30;

    public static void main(String[] args) {
        System.out.println("Starting Load Test...");
        System.out.println("Target URL: " + TARGET_URL);
        System.out.println("Total Requests: " + TOTAL_REQUESTS);
        System.out.println("Target TPS: " + TARGET_TPS);
        System.out.println("------------------------------------------------");

        // Use a shared HttpClient (best practice)
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger non200Count = new AtomicInteger(0);
        
        // Latch to wait for all async requests to complete
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS);

        long startTime = System.currentTimeMillis();
        long intervalNanos = 1_000_000_000 / TARGET_TPS; // Time per request in nanoseconds
        long nextTargetTime = System.nanoTime();

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            // Busy-wait or sleep to maintain TPS
            while (System.nanoTime() < nextTargetTime) {
                long remainingNanos = nextTargetTime - System.nanoTime();
                if (remainingNanos > 1_000_000) { // If more than 1ms wait, sleep
                    try {
                        Thread.sleep(remainingNanos / 1_000_000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            nextTargetTime += intervalNanos;

            // Prepare Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TARGET_URL))
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();

            // Send Async
            client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            successCount.incrementAndGet();
                        } else {
                            non200Count.incrementAndGet();
                            // System.err.println("Non-200 Response: " + response.statusCode());
                        }
                    })
                    .exceptionally(ex -> {
                        failureCount.incrementAndGet();
                        System.err.println("Request Failed: " + ex.getMessage());
                        return null;
                    })
                    .whenComplete((res, ex) -> {
                        latch.countDown();
                        int completed = TOTAL_REQUESTS - (int) latch.getCount();
                        if (completed % 100 == 0) {
                            System.out.print("\rProgress: " + completed + "/" + TOTAL_REQUESTS);
                        }
                    });
        }

        try {
            // Wait for all requests to finish
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        double actualTps = (double) TOTAL_REQUESTS / (durationMillis / 1000.0);

        System.out.println("\n\n------------------------------------------------");
        System.out.println("Load Test Completed.");
        System.out.println("------------------------------------------------");
        System.out.println("Total Time Taken: " + durationMillis + " ms");
        System.out.printf("Actual TPS: %.2f%n", actualTps);
        System.out.println("------------------------------------------------");
        System.out.println("Total Requests: " + TOTAL_REQUESTS);
        System.out.println("200 OK:         " + successCount.get());
        System.out.println("Non-200 Status: " + non200Count.get());
        System.out.println("Exceptions:     " + failureCount.get());
        System.out.println("------------------------------------------------");
    }
}
