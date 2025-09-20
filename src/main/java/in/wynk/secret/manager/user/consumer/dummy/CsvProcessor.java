package in.wynk.secret.manager.user.consumer.dummy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CsvProcessor {

    private static final String API_URL = "http://localhost:8388/v2/hub/dummy";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final RestTemplate restTemplate = new RestTemplate();

    private static final Map<String, Object> HARDCODED = Map.of(
            "eventName", "FREE_SUBSCRIPTION_CALLBACK_EVENT",
            "telcoUnlimited", false,
            "preferredPartner", true
    );

    public static void main(String[] args) throws Exception {
        processCsv("input2.csv");
    }

    private static void processCsv(String fileName) throws Exception {
        InputStream inputStream = new ClassPathResource(fileName).getInputStream();

        // Parse CSV with auto header detection
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        CSVParser parser = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreEmptyLines()
                .parse(reader);

        // Normalize headers
        List<String> normalizedHeaders = new ArrayList<>();
        for (String header : parser.getHeaderMap().keySet()) {
            normalizedHeaders.add(header.trim().replaceFirst("^@", ""));
        }

        // Thread pool with 16 workers
        ExecutorService executor = Executors.newFixedThreadPool(16);

        for (CSVRecord csvRecord : parser) {
            executor.submit(() -> {
                try {
                    Map<String, String> row = new HashMap<>();
                    for (int i = 0; i < normalizedHeaders.size(); i++) {
                        String header = normalizedHeaders.get(i);
                        String value = csvRecord.get(i) != null ? csvRecord.get(i).trim() : "";
                        row.put(header, value);
                    }

                    System.out.println("Row raw → " + row);

                    Map<String, Object> record = new LinkedHashMap<>();
                    record.put("planId", parseLong(row.get("planId")));
                    record.put("uid", row.get("uid"));
                    record.put("event", row.get("paymentEvent"));
                    record.put("msisdn", row.get("msisdn"));
                    record.put("service", row.get("service"));
                    record.put("referenceId", row.get("referenceId"));
                    record.put("validTillDate", parseLong(row.get("validTillDate")));
                    record.put("planPurchaseDate", parseLong(row.get("planPurchaseDate")));
                    record.put("autoRenewal", "true".equalsIgnoreCase(row.getOrDefault("autoRenewal", "")));

                    record.putAll(HARDCODED);

                    String payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(record);
                    System.out.println("Payload → " + payload);

                    // Send to API
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> entity = new HttpEntity<>(payload, headers);

                    ResponseEntity<String> response = restTemplate.exchange(
                            API_URL,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );

                    System.out.println("Response → " + response.getBody());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Shutdown and wait for all tasks to finish
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.MINUTES);
    }

    private static long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            String clean = Pattern.compile("[^\\dEe+\\.-]").matcher(value).replaceAll("");
            return (long) Double.parseDouble(clean);
        } catch (Exception e) {
            return 0;
        }
    }
}
