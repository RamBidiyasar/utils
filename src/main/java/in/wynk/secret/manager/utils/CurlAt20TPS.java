package in.wynk.secret.manager.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CurlAt20TPS {

    private static final String REQUEST_URL = "http://watch-preprod.internal.airtel.tv/get/userWatchInfo?uid=C_0FEoROcBfycW3oj0";
    private static final int TPS = 100; // Transactions per second
    private static final int DURATION_MINUTES = 5; // Duration in minutes
    private static final int TOTAL_REQUESTS = TPS * DURATION_MINUTES * 60; // Total number of requests

    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(TPS);

        Runnable task = () -> {
            try {
                sendRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        long initialDelay = 0;
        long period = 1000 / TPS; // Schedule tasks to run at intervals to maintain 20 TPS

        // Schedule tasks at a fixed rate to hit the curl at 20 TPS
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
        }

        // Schedule shutdown of the executor after the duration
        executor.schedule(() -> {
            executor.shutdown();
            System.out.println("Completed sending requests.");
        }, DURATION_MINUTES, TimeUnit.MINUTES);
    }

    private static void sendRequest() throws Exception {
        URL url = new URL(REQUEST_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Request successful: " + responseCode);
        } else {
            System.out.println("Failed request: " + responseCode);
        }

        connection.disconnect();
    }
}
