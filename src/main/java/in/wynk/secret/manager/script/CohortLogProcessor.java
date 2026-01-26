package in.wynk.secret.manager.script;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.wynk.secret.manager.dto.CohortRequest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CohortLogProcessor {

    private static final String LOG_FILE = "/Users/B0296099/Documents/BE_REPOS/utils/src/main/java/in/wynk/secret/manager/script/User-consumer-error-logs.txt";
    private static final String API_URL = "http://user-consumer-prod.internal.airtel.tv/s2s/v1/solace/message/dart";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    // Regex to extract the DTO string: DartsSolaceProducerMessageDto(...)
    private static final Pattern DTO_PATTERN = Pattern.compile("DartsSolaceProducerMessageDto\\((.*?)\\)");

    public static void main(String[] args) {
        CohortLogProcessor processor = new CohortLogProcessor();
        processor.processLogs();
    }

    public void processLogs() {
        System.out.println("Starting processing of log file: " + LOG_FILE);
        int processedCount = 0;
        int sentCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = DTO_PATTERN.matcher(line);
                if (matcher.find()) {
                    String dtoString = matcher.group(1);
                    CohortRequest request = parseDto(dtoString);
                    if (request != null) {
                        processedCount++;
                        boolean success = sendRequest(request);
                        if (success) {
                            sentCount++;
                        }
                        // Optional: Add a small delay to avoid overwhelming the server
                        // Thread.sleep(50);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Processing complete.");
        System.out.println("Total records processed: " + processedCount);
        System.out.println("Total requests successfully sent: " + sentCount);
    }

    private CohortRequest parseDto(String dtoString) {
        try {
            // Helper map to store field values
            // Fields in string: transactionId=..., si=..., uid=..., rtn=..., lob=...,
            // subLob=..., segment=..., oldSegment=..., transactionTime=...,
            // thanksExpiry=...
            // We'll iterate and split.
            // Since the format is key=value, key=value... we can split by ", "
            // CAUTION: If a value contains ", ", simple split might fail. But looking at
            // the log sample, values seem safe.

            String[] parts = dtoString.split(", ");
            String transactionId = getValue(parts, "transactionId");
            String si = getValue(parts, "si");
            String uid = getValue(parts, "uid");
            String rtn = getValue(parts, "rtn");
            String lob = getValue(parts, "lob");
            String subLob = getValue(parts, "subLob");
            String segment = getValue(parts, "segment");
            String oldSegment = getValue(parts, "oldSegment");
            String transactionTime = getValue(parts, "transactionTime");
            String thanksExpiry = getValue(parts, "thanksExpiry");

            return CohortRequest.builder()
                    .transactionId(transactionId)
                    .si(si)
                    .uid(uid)
                    .rtn(rtn)
                    .lob(lob)
                    .subLob(parseNull(subLob))
                    .segment(segment)
                    .oldSegment(parseNull(oldSegment))
                    .transactionTime(transactionTime)
                    .thanksExpiry(thanksExpiry)
                    .build();

        } catch (Exception e) {
            System.err.println("Error parsing DTO string: " + dtoString);
            e.printStackTrace();
            return null;
        }
    }

    private String getValue(String[] parts, String key) {
        String keyPrefix = key + "=";
        for (String part : parts) {
            if (part.startsWith(keyPrefix)) {
                return part.substring(keyPrefix.length());
            }
        }
        return null;
    }

    private String parseNull(String value) {
        if ("null".equals(value)) {
            return null;
        }
        return value;
    }

    private boolean sendRequest(CohortRequest payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println(
                        "✅ Sent for uid=" + payload.getUid() + ", transactionId=" + payload.getTransactionId());
                return true;
            } else {
                System.err.println("❌ Failed for uid=" + payload.getUid() + ", transactionId="
                        + payload.getTransactionId() + ": " + response.statusCode() + " " + response.body());
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Exception for uid=" + payload.getUid() + ", transactionId="
                    + payload.getTransactionId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}