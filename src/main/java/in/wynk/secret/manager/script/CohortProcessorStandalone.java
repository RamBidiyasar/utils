package in.wynk.secret.manager.script;

import in.wynk.secret.manager.dto.CohortRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CohortProcessorStandalone {

    private static final String CSV_FILE = "CohortDarts1.csv";
    private static final String API_URL = "http://user-consumer-prod.internal.airtel.tv/s2s/v1/solace/message/dart";
    private static final boolean SEND_REQUESTS = true;

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    private static final DateTimeFormatter OUTPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TRANSACTION_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS").withZone(UTC_ZONE);

    private static final Map<String, String> LOB_MAP = new HashMap<>();

    static {
        LOB_MAP.put("79007", "XSTREAM_D2C");
        LOB_MAP.put("99000", "XSTREAM_CO");
        LOB_MAP.put("99100", "PREPAID");
    }

    private final HttpClient httpClient;

    public CohortProcessorStandalone() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public static void main(String[] args) {
        CohortProcessorStandalone processor = new CohortProcessorStandalone();
        processor.processCohortData();
    }

    public void processCohortData() {
        System.out.println("Starting processing of " + CSV_FILE);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(CSV_FILE).getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            int count = 0;
            for (CSVRecord record : csvParser) {
                count++;
                Map<String, String> row = record.toMap();
                System.out.println("CSV Row " + count + ": " + row);

                String referenceId = record.isMapped("reference_id") ? record.get("reference_id") : null;
                String si = record.isMapped("si") ? record.get("si") : null;
                String uid = record.isMapped("uid") ? record.get("uid") : null;
                String planId = record.isMapped("plan_id") ? record.get("plan_id") : null;
                String cohort = record.isMapped("cohort") ? record.get("cohort") : null;
                String validTillStr = record.isMapped("valid_till") ? record.get("valid_till") : null;

                if (referenceId == null || referenceId.isBlank() ||
                    si == null || si.isBlank() ||
                    uid == null || uid.isBlank()) {
                    System.out.println("⚠ Skipping row due to missing mandatory fields");
                    continue;
                }

                String rtn = si.split("_")[0];
                String lob = LOB_MAP.getOrDefault(planId, "UNKNOWN");

                System.out.println("plan_id: " + planId + ", lob: " + lob);

                String thanksExpiry = null;
                if (validTillStr != null && !validTillStr.isBlank()) {
                    try {
                        Instant validTillInstant = parseInstant(validTillStr);
                        ZonedDateTime validTillIst = validTillInstant.atZone(IST_ZONE);
                        thanksExpiry = OUTPUT_DATE_FORMATTER.format(validTillIst);
                    } catch (Exception e) {
                        System.err.println("⚠ Invalid valid_till format: " + validTillStr + " - " + e.getMessage());
                        continue;
                    }
                }

                String transactionTime = TRANSACTION_TIME_FORMATTER.format(Instant.now());

                CohortRequest payload = CohortRequest.builder()
                        .transactionId(referenceId)
                        .si(si)
                        .uid(uid)
                        .rtn(rtn)
                        .lob(lob)
                        .subLob(null)
                        .segment(cohort)
                        .oldSegment(null)
                        .transactionTime(transactionTime)
                        .thanksExpiry(thanksExpiry)
                        .build();

                System.out.println("Payload: " + payload);

                if (SEND_REQUESTS) {
                    sendRequest(payload);
                } else {
                    System.out.println("⚠ Dry run: request not sent");
                }
            }
            System.out.println("Total rows processed: " + count);

        } catch (Exception e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendRequest(CohortRequest payload) {
        try {
            // Using Java 11+ HttpClient
            String jsonPayload = new com.google.gson.Gson().toJson(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("✅ Sent for uid=" + payload.getUid() + ", reference_id=" + payload.getTransactionId());
            } else {
                System.err.println("❌ Failed for uid=" + payload.getUid() + ", reference_id=" + payload.getTransactionId() + ": " + response.statusCode() + " " + response.body());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception for uid=" + payload.getUid() + ", reference_id=" + payload.getTransactionId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Instant parseInstant(String dateStr) {
        return Instant.parse(dateStr);
    }
}
