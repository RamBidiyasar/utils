package in.wynk.secret.manager.cdn.qwilt;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadTest {

    private static final String URL_TO_HIT = "https://iptv-prd-new-main.dlt.qwilted-cds.cqloud.com/live/med15/sony_max_hd/vdashhd/sony_max_hd.mpd";
    private static final String HEADER = "Ocn-Cache-Status";
    private static final int TPS = 20;
    private static final int DURATION_SECONDS = 60;

    private static final AtomicInteger totalRequests = new AtomicInteger();
    private static final AtomicInteger totalFailures = new AtomicInteger();
    private static final Map<String, AtomicInteger> headerCounts = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        // Schedule the task at fixed rate to match TPS
        Runnable task = () -> {
            if (totalRequests.get() >= TPS * DURATION_SECONDS) {
                return;
            }
            makeRequest();
        };

        executor.scheduleAtFixedRate(task, 0, 1000 / TPS, TimeUnit.MILLISECONDS);

        // Stop after duration ends and print results
        Executors.newSingleThreadScheduledExecutor()
                .schedule(() -> {
                    executor.shutdown();
                    printResults();
                }, DURATION_SECONDS, TimeUnit.SECONDS);
    }

    private static void makeRequest() {
        try {
            URL url = new URL(URL_TO_HIT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            totalRequests.incrementAndGet();

            if (responseCode == 200) {
                String headerValue = connection.getHeaderField(HEADER);
                if (headerValue != null) {
                    headerCounts
                            .computeIfAbsent(headerValue, k -> new AtomicInteger())
                            .incrementAndGet();
                }
            } else {
                totalFailures.incrementAndGet();
            }

            connection.disconnect();

        } catch (Exception e) {
            totalFailures.incrementAndGet();
        }
    }

    private static void printResults() {
        int total = totalRequests.get();
        int failures = totalFailures.get();

        System.out.println("\n==== Test Results ====");
        System.out.printf("Total Requests: %d%n", total);
        System.out.printf("Total Failures: %d%n", failures);
        System.out.printf("Test Duration: %ds%n", DURATION_SECONDS);

        headerCounts.forEach((key, value) -> {
            double percentage = (value.get() * 100.0) / total;
            System.out.printf("%s: %d (%.2f%%)%n", key, value.get(), percentage);
        });
    }
}
