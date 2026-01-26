package in.wynk.secret.manager.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class EventLoadTester {

    private static final int TPS = 10;
    // Target Transactions Per Second
    private static final int DURATION_MINUTES = 10;
    private static final String URL = "https://events-preprod.wynk.in/tv/events/v1/event?appId=WEB";

    private static final HttpClient client = HttpClient.newBuilder ()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final AtomicLong successCount = new AtomicLong(0);
    private static final AtomicLong failureCount = new AtomicLong(0);

    public static void main(String[] args) throws InterruptedException {
        // Use a thread pool for scheduling the triggers
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors()));

        Runnable task = () -> {
            String requestId = UUID.randomUUID().toString();
            long timestamp = System.currentTimeMillis();

            // JSON Body with dynamic ID and TS
            String requestBody = String.format("{\n" +
                    "    \"events\": [\n" +
                    "        {\n" +
                    "            \"appid\": \"WEB\",\n" +
                    "            \"plan_ids_mapped\": 99202\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"id\": \"%s\",\n" +
                    "    \"ts\": %d\n" +
                    "}", requestId, timestamp);

            HttpRequest request = HttpRequest.newBuilder ()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.discarding ())
                  .thenAccept(response -> {
                      if (response.statusCode() >= 200 && response.statusCode() < 300) {
                          successCount.incrementAndGet();
                      } else {
                          System.err.println("Non-200 Response: " + response.statusCode());
                          failureCount.incrementAndGet();
                      }
                  })
                  .exceptionally(e -> {
                      System.err.println("Request failed: " + e.getMessage());
                      failureCount.incrementAndGet();
                      return null;
                  });
        };

        long periodNanos = 1_000_000_000L / TPS; // Period between requests in nanoseconds
        System.out.println("Starting Load Test: " + TPS + " TPS for " + DURATION_MINUTES + " minutes.");
        System.out.println("Target: " + URL);

        // Schedule tasks to fire at intervals to achieve target TPS
        for (int i = 0; i < TPS; i++) {
            long initialDelay = i * periodNanos;
            scheduler.scheduleAtFixedRate(task, initialDelay, 1_000_000_000L, TimeUnit.NANOSECONDS);
        }

        // Monitor loop
        long endTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(DURATION_MINUTES);
        while (System.currentTimeMillis() < endTime) {
            Thread.sleep(5000);
            System.out.printf("Stats: Success=%d, Failure=%d%n", successCount.get(), failureCount.get());
        }

        System.out.println("Stopping load test...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }

        System.out.println("Final Stats: Success=" + successCount.get() + ", Failure=" + failureCount.get());
    }
}
