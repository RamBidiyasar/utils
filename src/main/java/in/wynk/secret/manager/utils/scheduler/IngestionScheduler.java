package in.wynk.secret.manager.utils.scheduler;

import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.net.http.*;
import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IngestionScheduler {

    private static final List<Integer> RUN_MINUTES = Arrays.asList(0, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 52, 56);

    private static final Path TIMESTAMP_FILE = Paths.get("timestamp.store");
    private static final long DAYS_TO_ADD = 58;
    private static final long processTill = 1546313249000L;

    private static long currentTimestamp;

    public static void main(String[] args) {
        loadTimestamp();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (shouldRunNow()) {
                    runIngestion();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES); // check every minute
    }

    private static boolean shouldRunNow() {
        LocalDateTime now = LocalDateTime.now();

        if(currentTimestamp > processTill){
            System.out.println("Ingestion process completed.");
            System.exit(0);
        }

        return RUN_MINUTES.contains(now.getMinute());
    }

    private static void runIngestion() throws Exception {
        System.out.println("Running ingestion at: " + LocalDateTime.now());
        System.out.println("Using timestamp: " + currentTimestamp);

        String requestBody = String.format("""
                                               {
                                                 "currentTime": %d,
                                                 "hours": 240,
                                                 "intervals": 6,
                                                 "toRunIngestionInRange": true,
                                                 "intervalsToSkip": 0,
                                                 "updatesOnly": false
                                               }
                                               """, currentTimestamp);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create("https://batch.airtel.tv/test/ingestion/cp?cp=HOTSTAR_DTH&ingestionType=INGESTION_TVSHOW"))
                                         .header("Content-Type", "application/json")
                                         .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                                         .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

        // Update timestamp: add 29 days
        currentTimestamp += TimeUnit.DAYS.toMillis(DAYS_TO_ADD);
        saveTimestamp();
    }

    private static void loadTimestamp() {
        try {
            if (Files.exists(TIMESTAMP_FILE)) {
                currentTimestamp = Long.parseLong(Files.readString(TIMESTAMP_FILE).trim());
                System.out.println("Loaded previous timestamp: " + currentTimestamp);
            } else {
                currentTimestamp = 788981985000L; // initial given timestamp
                saveTimestamp();
                System.out.println("Initialized timestamp to: " + currentTimestamp);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read timestamp file", e);
        }
    }

    private static void saveTimestamp() {
        try {
            Files.writeString(TIMESTAMP_FILE, String.valueOf(currentTimestamp));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write timestamp file", e);
        }
    }
}
