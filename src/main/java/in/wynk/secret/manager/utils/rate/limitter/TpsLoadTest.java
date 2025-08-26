package in.wynk.secret.manager.utils.rate.limitter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;

public class TpsLoadTest {

    private static final int TPS = 200;
    private static final String URL = "http://watch-preprod.internal.airtel.tv/get/userWatchInfo?uid=SsIA5oGtd7uLiwe430";

    public static void main(String[] args) throws InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(100);
        Runnable requestTask = () -> sendRequest(client);

        int periodMicros = 1_000_000 / TPS;

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            requestTask,
            0,
            periodMicros,
            TimeUnit.MICROSECONDS
        );

        // Run for 10 seconds then stop
        Thread.sleep(10_000_00);
        Thread.sleep(TimeUnit.MINUTES.toMillis(10));
        future.cancel(true);
        scheduler.shutdown();
    }

    private static void sendRequest(HttpClient client) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> System.out.println("Status: " + response.statusCode()))
                .exceptionally(e -> {
                    System.err.println("Error: " + e.getMessage());
                    return null;
                });
        } catch (Exception e) {
            System.err.println("Request creation failed: " + e.getMessage());
        }
    }
}
