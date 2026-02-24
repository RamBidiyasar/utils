package in.wynk.secret.manager.loadtest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Configurable Load Tester for hitting API endpoints at a specified TPS (Transactions Per Second).
 * 
 * This utility sends HTTP requests to the Airtel TV content API endpoint at a configurable rate.
 * By default, it runs at 60 TPS but can be adjusted via command-line arguments.
 * 
 * Usage:
 *   java ConfigurableLoadTester [TPS] [DURATION_SECONDS]
 *   
 * Examples:
 *   java ConfigurableLoadTester                    // Runs at 60 TPS for 60 seconds
 *   java ConfigurableLoadTester 100 120            // Runs at 100 TPS for 120 seconds
 *   
 * Features:
 *   - Configurable TPS rate with precise timing control
 *   - Real-time statistics (success rate, average latency, errors)
 *   - Graceful shutdown with final statistics
 *   - Thread pool management for efficient request handling
 */
public class ConfigurableLoadTester {

    private String user = "https://content.airtel.tv";
    private static final String API_URL = "http://localhost:8383/app/v1/config/appConfig?os=Android&bn=536&appId=IPTV&dt=STB";
    private static final int DEFAULT_TPS = 50;
    private static final int DEFAULT_DURATION_SECONDS = Math.toIntExact(TimeUnit.MINUTES.toSeconds(30));
    
    private final int targetTps;
    private final int durationSeconds;
    private final RestTemplate restTemplate;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler;
    
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger successfulRequests = new AtomicInteger(0);
    private final AtomicInteger failedRequests = new AtomicInteger(0);
    private final AtomicLong totalLatency = new AtomicLong(0);
    
    private volatile boolean running = true;
    
    /**
     * Initializes the load tester with specified TPS and duration.
     *
     * @param targetTps Target transactions per second
     * @param durationSeconds Test duration in seconds
     */
    public ConfigurableLoadTester(int targetTps, int durationSeconds) {
        this.targetTps = targetTps;
        this.durationSeconds = durationSeconds;
        this.restTemplate = new RestTemplate();
        this.executorService = Executors.newFixedThreadPool(Math.max(targetTps / 2, 100));
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    /**
     * Creates HTTP headers matching the curl command specification.
     *
     * @return HttpHeaders with all required headers
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Host", "content.airtel.tv");
        headers.set("Content-Type", "application/json");
        headers.set("x-atv-bouquet-id", "24696");
        headers.set("x-atv-utkn", "jlzfcY-T4urOqKZ6B0:YT4t++xmJ284zqfjGn5Lmn7TveY=");
        headers.set("x-b3-sampled", "0");
        headers.set("User-Agent", "okhttp/4.12.0");
        headers.set("Accept-Encoding", "gzip");
        headers.set("x-atv-chip-id", "1002B07B83C6951F");
        headers.set("x-atv-bsn", "B0HSTBYT502542310261");
        headers.set("x-envoy-external-address", "10.249.213.160");
        headers.set("x-cloud-trace-context", "e9b7d7dd8f7bdba9f8331d4b82e4f185/5618341731750105130");
        headers.set("x-envoy-peer-metadata-id", "router~10.161.33.19~xstream-istio-gateway-prod-d6d8467c7-6d82z.istio-system~istio-system.svc.cluster.local");
        headers.set("x-envoy-decorator-operation", "content-service.xstream.svc.cluster.local:80/*");
        headers.set("x-request-id", "6661d326-3bbd-4132-86f1-42ffa0ba1e8d");
        headers.set("x-atv-dth-subscriber-id", "3072217774-001");
        headers.set("x-group-name", "cms");
        headers.set("X-Forwarded-Proto", "http");
        headers.set("x-atv-did", "3e9bf831cd0d7c7f|STB|Android|34|536|1.0.0316");
        headers.set("x-atv-segment", "");
        headers.set("Via", "1.1 google");
        headers.set("If-None-Match", "42a1cf08dd2e93cdba7326b6ca49c871--gzip");
        headers.set("x-envoy-attempt-count", "1");
        headers.set("x-b3-traceid", "a9ee0f67ea68bc21432710a731ac243c");
        headers.set("x-b3-spanid", "432710a731ac243c");
        headers.set("x-atv-traceid", "ea72b302-87e1-47d7-a1b9-df11ae045192");
        headers.set("x-envoy-peer-metadata", "");
        return headers;
    }
    
    /**
     * Executes a single HTTP request and records metrics.
     */
    private void executeRequest() {
        Instant start = Instant.now();
        try {
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                API_URL,
                HttpMethod.GET,
                entity,
                String.class
            );
            
            long latency = Duration.between(start, Instant.now()).toMillis();
            totalLatency.addAndGet(latency);
            
            if (response.getStatusCode().is2xxSuccessful() || response.getStatusCode().value() == 304) {
                successfulRequests.incrementAndGet();
            } else {
                failedRequests.incrementAndGet();
                System.err.println("Non-success status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            failedRequests.incrementAndGet();
            System.err.println("Request failed: " + e.getMessage());
        } finally {
            totalRequests.incrementAndGet();
        }
    }
    
    /**
     * Starts the load test with the configured TPS rate.
     */
    public void start() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         Configurable Load Tester Started                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Target TPS:     " + targetTps);
        System.out.println("Duration:       " + durationSeconds + " seconds");
        System.out.println("Target Endpoint: " + API_URL);
        System.out.println("Start Time:     " + Instant.now());
        System.out.println("────────────────────────────────────────────────────────────");
        
        Instant startTime = Instant.now();
        
        // Calculate interval between requests in milliseconds
        long intervalMillis = 1000L / targetTps;
        
        // Schedule requests at fixed rate
        ScheduledFuture<?> requestScheduler = scheduler.scheduleAtFixedRate(() -> {
            if (running) {
                executorService.submit(this::executeRequest);
            }
        }, 0, intervalMillis, TimeUnit.MILLISECONDS);
        
        // Schedule statistics reporter
        ScheduledFuture<?> statsReporter = scheduler.scheduleAtFixedRate(
            this::printStatistics,
            5,
            5,
            TimeUnit.SECONDS
        );
        
        // Schedule shutdown after duration
        scheduler.schedule(() -> {
            running = false;
            requestScheduler.cancel(false);
            statsReporter.cancel(false);
            shutdown();
        }, durationSeconds, TimeUnit.SECONDS);
    }
    
    /**
     * Prints current statistics to console.
     */
    private void printStatistics() {
        int total = totalRequests.get();
        int success = successfulRequests.get();
        int failed = failedRequests.get();
        double avgLatency = total > 0 ? (double) totalLatency.get() / total : 0;
        double successRate = total > 0 ? (double) success / total * 100 : 0;
        
        System.out.printf("[%s] Total: %d | Success: %d | Failed: %d | Success Rate: %.2f%% | Avg Latency: %.2fms%n",
            Instant.now(), total, success, failed, successRate, avgLatency);
    }
    
    /**
     * Prints final statistics summary.
     */
    private void printFinalStatistics() {
        int total = totalRequests.get();
        int success = successfulRequests.get();
        int failed = failedRequests.get();
        double avgLatency = total > 0 ? (double) totalLatency.get() / total : 0;
        double successRate = total > 0 ? (double) success / total * 100 : 0;
        double actualTps = (double) total / durationSeconds;
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              Final Load Test Statistics                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Test Duration:        " + durationSeconds + " seconds");
        System.out.println("Target TPS:           " + targetTps);
        System.out.println("Actual TPS:           " + String.format("%.2f", actualTps));
        System.out.println("Total Requests:       " + total);
        System.out.println("Successful Requests:  " + success);
        System.out.println("Failed Requests:      " + failed);
        System.out.println("Success Rate:         " + String.format("%.2f%%", successRate));
        System.out.println("Average Latency:      " + String.format("%.2fms", avgLatency));
        System.out.println("End Time:             " + Instant.now());
        System.out.println("════════════════════════════════════════════════════════════");
    }
    
    /**
     * Gracefully shuts down the load tester and prints final statistics.
     */
    private void shutdown() {
        System.out.println("\nShutting down load tester...");
        
        executorService.shutdown();
        scheduler.shutdown();
        
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        printFinalStatistics();
    }
    
    /**
     * Main method to run the load tester.
     *
     * @param args Command-line arguments: [TPS] [DURATION_SECONDS]
     */
    public static void main(String[] args) {
        int tps = DEFAULT_TPS;
        int duration = DEFAULT_DURATION_SECONDS;
        
        if (args.length >= 1) {
            try {
                tps = Integer.parseInt(args[0]);
                if (tps <= 0) {
                    System.err.println("TPS must be positive. Using default: " + DEFAULT_TPS);
                    tps = DEFAULT_TPS;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid TPS value. Using default: " + DEFAULT_TPS);
            }
        }
        
        if (args.length >= 2) {
            try {
                duration = Integer.parseInt(args[1]);
                if (duration <= 0) {
                    System.err.println("Duration must be positive. Using default: " + DEFAULT_DURATION_SECONDS);
                    duration = DEFAULT_DURATION_SECONDS;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid duration value. Using default: " + DEFAULT_DURATION_SECONDS);
            }
        }
        
        ConfigurableLoadTester tester = new ConfigurableLoadTester(tps, duration);
        
        // Add shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nReceived shutdown signal...");
            tester.running = false;
        }));
        
        tester.start();
        
        // Keep main thread alive
        try {
            Thread.sleep((duration + 5) * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
