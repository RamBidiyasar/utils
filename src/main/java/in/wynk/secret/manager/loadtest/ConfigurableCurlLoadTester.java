package in.wynk.secret.manager.loadtest;

import com.google.common.util.concurrent.RateLimiter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Configurable Load Tester - Paste any curl command in curl.txt
 * 
 * Usage:
 * 1. Create/Edit curl.txt and paste your curl command
 * 2. Modify TPS and DURATION below
 * 3. Run from IDE
 */
public class ConfigurableCurlLoadTester {

    // ========== CONFIGURATION - MODIFY ONLY THESE TWO ==========
    
    private static final int TPS = 400;                    // Target TPS
    private static final int DURATION_SECONDS = 90;       // Test duration
    
    // ============================================================

    // Statistics tracking
    private static final AtomicInteger totalRequests = new AtomicInteger(0);
    private static final AtomicInteger successRequests = new AtomicInteger(0);
    private static final AtomicInteger failedRequests = new AtomicInteger(0);
    private static final AtomicLong totalLatencyMs = new AtomicLong(0);
    
    // Thread pool size based on TPS
    private static final int THREAD_POOL_SIZE = Math.max(TPS / 2, 10);
    
    // Parsed curl configuration
    private static String url;
    private static String method = "GET";
    private static String requestBody = null;
    private static Map<String, String> headers = new LinkedHashMap<>();
    
    public static void main(String[] args) {
        // Parse curl command from curl.txt
        try {
            parseCurlFromFile("curl.txt");
        } catch (Exception e) {
            System.err.println("❌ Error reading curl.txt: " + e.getMessage());
            System.err.println("\n📝 Please create curl.txt in the project root and paste your curl command.");
            System.err.println("Example:");
            System.err.println("curl --location 'https://api.example.com/endpoint' \\");
            System.err.println("--header 'Authorization: Bearer token' \\");
            System.err.println("--data '{\"key\":\"value\"}'");
            return;
        }
        
        printHeader();
        
        // Setup shutdown hook for graceful termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\n🛑 Shutdown signal received. Printing final statistics...");
            printFinalStatistics();
        }));
        
        // Create HTTP client with connection pool
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .executor(Executors.newFixedThreadPool(THREAD_POOL_SIZE))
                .build();
        
        // Rate limiter to control TPS
        RateLimiter rateLimiter = RateLimiter.create(TPS);
        
        // Executor for async request submission
        ExecutorService executor = Executors.newCachedThreadPool();
        
        // Statistics reporting thread
        ScheduledExecutorService statsReporter = Executors.newSingleThreadScheduledExecutor();
        statsReporter.scheduleAtFixedRate(() -> printCurrentStatistics(), 5, 5, TimeUnit.SECONDS);
        
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (DURATION_SECONDS * 1000L);
        
        System.out.println("🚀 Load test started at " + Instant.now());
        System.out.println("────────────────────────────────────────────────────────────");
        
        // Main request loop
        while (System.currentTimeMillis() < endTime) {
            rateLimiter.acquire(); // Rate limiting
            executor.submit(() -> sendRequest(client));
        }
        
        // Cleanup
        executor.shutdown();
        statsReporter.shutdown();
        
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            statsReporter.shutdownNow();
        } catch (InterruptedException e) {
            executor.shutdownNow();
            statsReporter.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        printFinalStatistics();
    }
    
    private static void sendRequest(HttpClient client) {
        long requestStartTime = System.nanoTime();
        
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10));
            
            // Add all headers from curl
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.header(header.getKey(), header.getValue());
            }
            
            // Set HTTP method and body
            if ("POST".equalsIgnoreCase(method) && requestBody != null) {
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBody));
            } else if ("PUT".equalsIgnoreCase(method) && requestBody != null) {
                requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(requestBody));
            } else if ("PATCH".equalsIgnoreCase(method) && requestBody != null) {
                requestBuilder.method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody));
            } else if ("DELETE".equalsIgnoreCase(method)) {
                requestBuilder.DELETE();
            } else {
                requestBuilder.GET();
            }
            
            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            long latencyMs = (System.nanoTime() - requestStartTime) / 1_000_000;
            totalLatencyMs.addAndGet(latencyMs);
            totalRequests.incrementAndGet();
            
            int statusCode = response.statusCode();
            if (statusCode >= 200 && statusCode < 300 || statusCode == 304) {
                successRequests.incrementAndGet();
            } else {
                failedRequests.incrementAndGet();
                System.err.println("❌ Request failed with status: " + statusCode);
            }
            
        } catch (Exception e) {
            totalRequests.incrementAndGet();
            failedRequests.incrementAndGet();
            System.err.println("❌ Exception: " + e.getMessage());
        }
    }
    
    private static void parseCurlFromFile(String filename) throws IOException {
        String content = Files.readString(Paths.get(filename)).trim();
        
        // Remove leading "curl" command
        content = content.replaceFirst("^curl\\s+", "");
        
        // Extract URL (first argument or from --location/--url)
        Pattern urlPattern = Pattern.compile("(?:--location|--url|-L)\\s+'([^']+)'|(?:--location|--url|-L)\\s+\"([^\"]+)\"|(?:^|\\s)(?!--)([^\\s'\"]+://[^\\s'\"]+)");
        Matcher urlMatcher = urlPattern.matcher(content);
        if (urlMatcher.find()) {
            url = urlMatcher.group(1) != null ? urlMatcher.group(1) : 
                  (urlMatcher.group(2) != null ? urlMatcher.group(2) : urlMatcher.group(3));
        }
        
        // If no URL found with protocol, try first quoted string
        if (url == null) {
            Pattern simpleUrlPattern = Pattern.compile("'([^']+)'|\"([^\"]+)\"");
            Matcher simpleUrlMatcher = simpleUrlPattern.matcher(content);
            if (simpleUrlMatcher.find()) {
                String candidate = simpleUrlMatcher.group(1) != null ? simpleUrlMatcher.group(1) : simpleUrlMatcher.group(2);
                if (!candidate.startsWith("--")) {
                    url = candidate;
                    // Add protocol if missing
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                }
            }
        }
        
        if (url == null) {
            throw new IOException("Could not extract URL from curl command");
        }
        
        // Extract headers
        Pattern headerPattern = Pattern.compile("--header\\s+'([^:]+):\\s*([^']+)'|--header\\s+\"([^:]+):\\s*([^\"]+)\"|(?:-H)\\s+'([^:]+):\\s*([^']+)'|(?:-H)\\s+\"([^:]+):\\s*([^\"]+)\"");
        Matcher headerMatcher = headerPattern.matcher(content);
        while (headerMatcher.find()) {
            String key = null, value = null;
            if (headerMatcher.group(1) != null) {
                key = headerMatcher.group(1).trim();
                value = headerMatcher.group(2).trim();
            } else if (headerMatcher.group(3) != null) {
                key = headerMatcher.group(3).trim();
                value = headerMatcher.group(4).trim();
            } else if (headerMatcher.group(5) != null) {
                key = headerMatcher.group(5).trim();
                value = headerMatcher.group(6).trim();
            } else if (headerMatcher.group(7) != null) {
                key = headerMatcher.group(7).trim();
                value = headerMatcher.group(8).trim();
            }
            if (key != null) {
                headers.put(key, value);
            }
        }
        
        // Extract request body (--data, --data-raw, -d)
        Pattern dataPattern = Pattern.compile("(?:--data|--data-raw|-d)\\s+'([^']+)'|(?:--data|--data-raw|-d)\\s+\"([^\"]+)\"");
        Matcher dataMatcher = dataPattern.matcher(content);
        if (dataMatcher.find()) {
            requestBody = dataMatcher.group(1) != null ? dataMatcher.group(1) : dataMatcher.group(2);
            method = "POST";
        }
        
        // Extract method if specified (--request, -X)
        Pattern methodPattern = Pattern.compile("(?:--request|-X)\\s+([A-Z]+)");
        Matcher methodMatcher = methodPattern.matcher(content);
        if (methodMatcher.find()) {
            method = methodMatcher.group(1);
        }
    }
    
    private static void printHeader() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║       Configurable Curl Load Tester                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Target TPS:       " + TPS);
        System.out.println("Duration:         " + DURATION_SECONDS + " seconds");
        System.out.println("HTTP Method:      " + method);
        System.out.println("Thread Pool Size: " + THREAD_POOL_SIZE);
        System.out.println("Headers Count:    " + headers.size());
        System.out.println("Target URL:       " + (url.length() > 80 ? url.substring(0, 80) + "..." : url));
        System.out.println("════════════════════════════════════════════════════════════");
    }
    
    private static void printCurrentStatistics() {
        int total = totalRequests.get();
        int success = successRequests.get();
        int failed = failedRequests.get();
        
        if (total == 0) return;
        
        double successRate = (success * 100.0) / total;
        double avgLatency = totalLatencyMs.get() / (double) total;
        
        System.out.printf("[%s] Total: %d | Success: %d | Failed: %d | Success Rate: %.2f%% | Avg Latency: %.2fms%n",
                Instant.now(), total, success, failed, successRate, avgLatency);
    }
    
    private static void printFinalStatistics() {
        int total = totalRequests.get();
        int success = successRequests.get();
        int failed = failedRequests.get();
        
        if (total == 0) {
            System.out.println("No requests were processed.");
            return;
        }
        
        double successRate = (success * 100.0) / total;
        double avgLatency = totalLatencyMs.get() / (double) total;
        double actualTps = total / (double) DURATION_SECONDS;
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              Final Load Test Statistics                   ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("Test Duration:        " + DURATION_SECONDS + " seconds");
        System.out.println("Target TPS:           " + TPS);
        System.out.printf("Actual TPS:           %.2f%n", actualTps);
        System.out.println("Total Requests:       " + total);
        System.out.println("Successful Requests:  " + success);
        System.out.println("Failed Requests:      " + failed);
        System.out.printf("Success Rate:         %.2f%%%n", successRate);
        System.out.printf("Average Latency:      %.2fms%n", avgLatency);
        System.out.println("End Time:             " + Instant.now());
        System.out.println("════════════════════════════════════════════════════════════");
    }
}
