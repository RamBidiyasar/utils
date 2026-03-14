package in.wynk.secret.manager.curl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.Duration;

public class ApiLoader {

    private static final String URL = "http://batch-msp-prod.in.airtel.tv/v1/ingestion/schedule";
    private static final int TOTAL_REQUESTS = 1000;
    private static final int TPS = 20;
    
    private final HttpClient httpClient;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger errorCount = new AtomicInteger(0);
    private final long startTime;

    public ApiLoader() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.startTime = System.currentTimeMillis();
    }

    public void start() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(TPS);
        
        System.out.println("Starting load test...");
        System.out.println("Target: " + URL);
        System.out.println("Configuration: " + TOTAL_REQUESTS + " requests at " + TPS + " TPS");

        // Schedule tasks at fixed rate to maintain TPS
        executor.scheduleAtFixedRate(() -> {
            int currentCount = requestCount.incrementAndGet();
            if (currentCount > TOTAL_REQUESTS) {
                if (currentCount == TOTAL_REQUESTS + 1) {
                    shutdown(executor);
                }
                return;
            }

            sendRequest();

            if (currentCount % 1000 == 0) {
                printProgress();
            }
        }, 0, 1000 / TPS, TimeUnit.MILLISECONDS);
    }

    private void sendRequest() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        successCount.incrementAndGet();
                    } else {
                        errorCount.incrementAndGet();
                    }
                })
                .exceptionally(ex -> {
                    errorCount.incrementAndGet();
                    return null;
                });
    }

    private void printProgress() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        int current = Math.min(requestCount.get(), TOTAL_REQUESTS);
        System.out.printf("[%ds] Progress: %d/%d (Success: %d, Errors: %d)%n", 
                elapsed, current, TOTAL_REQUESTS, successCount.get(), errorCount.get());
    }

    private void shutdown(ScheduledExecutorService executor) {
        System.out.println("Finished sending all requests. Waiting for pending responses...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        printProgress();
        System.out.println("Load test completed.");
    }

    public static void main(String[] args) {
        new ApiLoader().start();
    }
}
