package in.wynk.secret.manager.utils.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.wynk.secret.manager.dto.CohortRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KafkaCdcTimeWindowConsumer {

    private static final String TOPIC = "xstream-scylla-production-dp-latest.usercore.partner_claim_info";
    private static final String API_URL =
        "http://user-consumer-prod.internal.airtel.tv/s2s/v1/solace/message/dart";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    // Business filter window (inside CDC JSON)
    private static final long CREATED_START = toMs("20/11/2025 00:00");
    private static final long CREATED_END = toMs("20/11/2025 05:00");

    // Kafka fetch window (real Kafka timestamps)
    private static final long FETCH_START = toMs("20/11/2025 00:00");
    private static final long FETCH_END = toMs("20/11/2025 03:30");

    public static void main(String[] args) throws Exception {

        String bootstrap = "10.161.24.74:9092,10.161.24.75:9092";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";

        Properties common = new Properties();
        common.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        common.put("security.protocol", "SASL_PLAINTEXT");
        common.put("sasl.mechanism", "PLAIN");
        common.put("sasl.jaas.config",
                   "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                   "username=\"" + username + "\" password=\"" + password + "\";");

        Properties consumerProps = new Properties();
        consumerProps.putAll(common);
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "cdc-time-window");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (AdminClient admin = AdminClient.create(common);
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {

            // Get partitions
            TopicDescription desc = admin.describeTopics(List.of(TOPIC)).all().get().get(TOPIC);
            List<TopicPartition> partitions = desc.partitions()
                                                  .stream()
                                                  .map(p -> new TopicPartition(TOPIC, p.partition()))
                                                  .toList();

            // start + end offset lookup
            Map<TopicPartition, OffsetSpec> reqStart = new HashMap<>();
            Map<TopicPartition, OffsetSpec> reqEnd = new HashMap<>();
            for (TopicPartition tp : partitions) {
                reqStart.put(tp, OffsetSpec.forTimestamp(FETCH_START));
                reqEnd.put(tp, OffsetSpec.forTimestamp(FETCH_END));
            }

            var startInfo = admin.listOffsets(reqStart).all().get();
            var endInfo = admin.listOffsets(reqEnd).all().get();

            Map<TopicPartition, Long> startOffsets = new HashMap<>();
            Map<TopicPartition, Long> endOffsets = new HashMap<>();

            for (TopicPartition tp : partitions) {
                long ss = startInfo.get(tp).offset();
                long ee = endInfo.get(tp).offset();
                startOffsets.put(tp, ss);
                endOffsets.put(tp, ee);
                System.out.printf("Partition %d: startOffset=%d endOffset=%d%n",
                                  tp.partition(), ss, ee);
            }

            // Assign (not subscribe)
            consumer.assign(partitions);

            // Seek to each partition’s start offset
            for (TopicPartition tp : partitions) {
                consumer.seek(tp, startOffsets.get(tp));
            }

            HttpClient http = HttpClient.newHttpClient();
            Set<TopicPartition> remaining = new HashSet<>(partitions);

            System.out.println("[START] Reading Kafka events within time window...");

            while (!remaining.isEmpty()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> rec : records) {
                    TopicPartition tp = new TopicPartition(TOPIC, rec.partition());
                    long endOffset = endOffsets.get(tp);

                    if (rec.offset() >= endOffset) {
                        remaining.remove(tp);
                        continue;
                    }

                    // Time cutoff
                    if (rec.timestamp() > FETCH_END) {
                        remaining.remove(tp);
                        continue;
                    }

                    processBusinessLogic(rec.value(), http);
                }
            }

            System.out.println("[END] Completed time-window consumption.");
        }
    }

    private static void processBusinessLogic(String json, HttpClient http) throws Exception {
        try {
            if (StringUtils.isBlank(json)) {
                return;
            }
            JsonNode root = MAPPER.readTree(json);
            JsonNode after = root.get("after");

            if (after == null || after.isNull()) {
                return;
            }

            long createdAt = after.path("created_at").path("value").asLong();
            String status = after.path("status").path("value").asText("");
            String cohort = after.path("cohort").path("value").asText("");

            // Business filter
            if (!"PURCHASE".equalsIgnoreCase(status)) {
                return;
            }
            if (createdAt < CREATED_START || createdAt > CREATED_END) {
                return;
            }

            if (cohort == null || cohort.isBlank()) {
                return;
            }

            String referenceId = safe(after, "reference_id");
            String si = safe(after.path("si"), "value");
            String uid = safe(after, "uid");
            String planId = safe(after.path("plan_id"), "value");
            long validTill = after.path("valid_till").path("value").asLong();

            String rtn = si.split("_")[0];
            String lob = getLob(planId);
            String expiry = formatIstDate(validTill);

            String transactionTime = LocalDateTime.now(ZoneOffset.UTC)
                                                  .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

            CohortRequest req = CohortRequest.builder()
                                             .transactionId(referenceId)
                                             .si(si)
                                             .uid(uid)
                                             .rtn(rtn)
                                             .lob(lob)
                                             .subLob(null)
                                             .segment(cohort)
                                             .oldSegment(null)
                                             .transactionTime(transactionTime)
                                             .thanksExpiry(expiry)
                                             .build();

            sendRequest(req, http);
        } catch (Exception e) {
            System.err.println("Error processing record: " + e.getMessage());
        }
    }

    private static void sendRequest(CohortRequest payload, HttpClient http) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create(API_URL))
                                         .header("Content-Type", "application/json")
                                         .POST(HttpRequest.BodyPublishers.ofString(json))
                                         .build();

        http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String safe(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    private static long toMs(String date) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return LocalDateTime.parse(date, fmt).atZone(IST).toInstant().toEpochMilli();
    }

    private static String formatIstDate(long epochMs) {
        if (epochMs == 0) {
            return null;
        }
        return Instant.ofEpochMilli(epochMs)
                      .atZone(IST)
                      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private static String getLob(String planId) {
        if (planId == null) {
            return "UNKNOWN";
        }
        return switch (planId) {
            case "79007" -> "XSTREAM_D2C";
            case "99000" -> "XSTREAM_CO";
            case "99100" -> "PREPAID";
            default -> "UNKNOWN";
        };
    }
}
