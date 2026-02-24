package in.wynk.secret.manager.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PlaybackInfoLoadTester {

    private static final int TPS = 100; // Target Transactions Per Second
    private static final String URL = "http://localhost:8387/playback/v2/info?contentId=889&sh=390&sw=700&res=1920x1080&appId=IPTV";

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    
    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failureCount = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Math.min(TPS, 100)); // Cap threads reasonably
        
        Runnable task = () -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("x-b3-parentspanid", "4d7543f1ac0a565a")
                    .header("x-atv-stkn", "gcVKPfowZa1ESm8N83xSLUfdJU6+1tsH7zvFmFf0NCzX4b34TxuMICZSSyvccnXz")
                    .header("x-atv-utkn", "C4uax6HvaRRksMn5x0:HAchx5HSg1Y3nsG3FLZqXaiHMS0=")
                    .header("x-b3-sampled", "0")
                    .header("x-os-id", "Android")
                    .header("x-atv-chip-id", "1002B0578D69F46A")
                    .header("x-atv-customer", "|BROADBAND|1||3")
                    .header("via", "1.1 google")
                    .header("x-atv-circle", "kl")
                    .header("if-modified-since", "Thu, 20 Nov 2025 18:45:57 GMT")
                    .header("x-atv-bsn", "B0HSTBYT502519510257")
                    .header("x-envoy-external-address", "10.249.213.24")
                    .header("x-app-id", "IPTV")
                    .header("x-cloud-trace-context", "5c117332e9cd963e32a42e0c79b0ec79/10461466182292216954")
                    .header("content-type", "application/json")
                    .header("x-request-id", "3322c1bb-d830-4a27-9482-013c4af0658a")
                    .header("x-atv-dth-subscriber-id", "3069162919-001")
                    .header("x-version-id", "1.0.0")
                    .header("x-forwarded-proto", "http")
                    .header("x-forwarded-for", "223.181.13.113")
                    .header("x-atv-did", "3c94fe6c143af6ec|STB|Android|34|448|1.0.0228")
                    .header("x-atv-segment", "")
                    .header("x-envoy-attempt-count", "1")
                    .header("x-b3-traceid", "4676748f636a1a044d7543f1ac0a565a")
                    .header("x-b3-spanid", "9c88105e4173f75c")
                    .header("x-forwarded-client-cert", "By=spiffe://cluster.local/ns/xstream/sa/gke-wynk-prd-xstrm-app-sa;Hash=21c3089410c0bd15222e619eaadfdead763402962333af79aadbf7cfbae1eba5;Subject=\";URI=spiffe://cluster.local/ns/istio-system/sa/xstream-istio-gateway-prod")
                    .header("x-org-id", "atv")
                    .header("accept-encoding", "gzip")
                    .header("user-agent", "okhttp/4.12.0")
                    .header("x-auth-bypass-token", "test-token-1")
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                  .thenAccept(response -> {
                      if (response.statusCode() >= 200 && response.statusCode() < 300) {
                          successCount.incrementAndGet();
                      } else {
                          System.out.println("Failed Status: " + response.statusCode());
                          failureCount.incrementAndGet();
                      }
                  })
                  .exceptionally(e -> {
                      System.err.println("Request failed: " + e.getMessage());
                      failureCount.incrementAndGet();
                      return null;
                  });
        };

        System.out.println("Starting load test on " + URL + " at " + TPS + " TPS.");

        long periodNanos = 1_000_000_000L / TPS; // Period between requests in nanoseconds
        
        // Spread the tasks out over the second to achieve smoother TPS
        for (int i = 0; i < TPS; i++) {
            long initialDelay = i * periodNanos;
            // Schedule at fixed rate: run every 1 second (1e9 nanoseconds) with offset
            scheduler.scheduleAtFixedRate(task, initialDelay, 1_000_000_000L, TimeUnit.NANOSECONDS);
        }
        
        // Reporting thread
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            int s = successCount.getAndSet(0);
            int f = failureCount.getAndSet(0);
            System.out.println("Last Second: Success=" + s + ", Failure=" + f);
        }, 1, 1, TimeUnit.SECONDS);

        // Run indefinitely until user stops, or set a duration
        // Thread.sleep(60_000); 
        // scheduler.shutdownNow();
    }
}
