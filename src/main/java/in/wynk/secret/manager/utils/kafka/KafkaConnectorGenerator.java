package in.wynk.secret.manager.utils.kafka;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kafka Connector Generator and Executor (Full Production Version)
 * Manages MongoDB Kafka Source and Sink Connectors with:
 * 1. Timestamp-based startup
 * 2. Dynamic Partition/Task tuning per collection
 * 3. Automatic Topic Creation
 * 4. Fault Tolerance settings
 */
public class KafkaConnectorGenerator {

    // ==========================================
    // 1. GLOBAL CONFIGURATION
    // ==========================================
    private static final String KAFKA_CONNECT_URL = "http://10.169.24.13:8083/connectors";
    private static final String DATABASE = "atv";
    private static final String TOPIC_PREFIX = "mongo";

    // --- TIMESTAMP CONFIGURATION ---
    // Format: EPOCH SECONDS (not milliseconds!)
    // The MongoDB Kafka Connector expects seconds for startup.mode.timestamp
    // Example: date -d "2024-12-01 10:00:00" +%s
    // Alternative formats: ISO-8601 ('1970-01-01T00:00:30Z') or BSON Timestamp
    private static final String START_TIMESTAMP = "1767002071";

    public enum StartupMode {
        LATEST, TIMESTAMP, COPY_EXISTING
    }

    private static final StartupMode STARTUP_MODE = StartupMode.TIMESTAMP;

    // --- SOURCE CREDENTIALS ---
    private static final String SOURCE_URI = "mongodb://admin:M0ng0DB%40P%40%24%24w0rd%21@10.169.24.26:27017/?replicaSet=rs1&authSource=admin";
    private static final String SOURCE_USER = "appuser";
    private static final String SOURCE_PASS = "uJK67dC1Ax";

    // --- SINK CREDENTIALS ---
    private static final String SINK_URI = "mongodb://admin:xstrm%401234@10.169.24.27:27017/?authSource=admin&connectTimeoutMS=10000";
    private static final String SINK_USER = "appuser";
    private static final String SINK_PASS = "uJK67dC1Ax";

    // --- FAULT TOLERANCE ---
    private static final String DLQ_TOPIC = "mongo.dlq.errors";

    // ==========================================
    // 2. STRATEGY: TUNING RULES
    // ==========================================

    static class TopicConfig {
        int sinkTasks; // Number of Sink Connector Tasks (Parallelism)

        public TopicConfig(int sinkTasks) {
            this.sinkTasks = sinkTasks;
        }
    }

    private static final Map<String, TopicConfig> COLLECTION_RULES = new HashMap<>();

    static {
        // DEFAULT RULE: 1 Sink Task
        // Good for small to medium collections.
        COLLECTION_RULES.put("DEFAULT", new TopicConfig(1));

        // HIGH PERFORMANCE RULE: 20 Sink Tasks
        // Apply this to your "Major Write" collections.
        // NOTE: Topics must be pre-created with desired partitions/retention
        TopicConfig highPerfConfig = new TopicConfig(20);
        COLLECTION_RULES.put("playable_content", highPerfConfig);

        // Add more collection-specific rules here as needed
        // Example:
        // COLLECTION_RULES.put("rails", new TopicConfig(5));
    }

    // ==========================================
    // 3. MAIN EXECUTION LOGIC
    // ==========================================

    public enum ConnectorOperation {
        CREATE, DELETE, RESTART, LIST, DELETE_SOURCE, DELETE_SINK, RESTART_SOURCE, RESTART_SINK
    }

    public enum ConnectorType {
        SOURCE, SINK, BOTH
    }

    private static int totalCollections = 0;
    private static int successfulSourceConnectors = 0;
    private static int failedSourceConnectors = 0;
    private static int successfulSinkConnectors = 0;
    private static int failedSinkConnectors = 0;

    public static void main(String[] args) {
        ConnectorOperation operation = ConnectorOperation.CREATE;
        List<String> collections;

        if (args.length > 0) {
            try {
                operation = ConnectorOperation.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                printUsage();
                return;
            }
        }

        // Make collections configurable via command-line argument
        // Usage: java KafkaConnectorGenerator CREATE mw_config,playable_content
        if (args.length > 1) {
            collections = Arrays.asList(args[1].split(","));
            System.out.println("Using default collections: " + String.join(", ", collections));
        } else {
            // Default collections for testing
            collections = List.of("alerts", "mw_config", "aggregation_config", "playable_content");
        }

        // All collections (commented for reference)
        // collections = Arrays.asList("ltp_channel_inventory",
        // "sport_info", "promotional_event", "device_notification", "system.profile",
        // "sequence", "dummy_channel_info", "notification_store", "box_details",
        // "failed_wcf_events", "filtered_shows", "pages", "game_meta",
        // "hotstar_highlights", "match_questions", "cpRule", "theme_config", "box_qms",
        // "airtel_only_config", "mw_config", "rails_copy", "title_akas", "user_otp",
        // "cms_user", "bb_upgrade_order", "rails", "playable_intermediate_content",
        // "product_channel_mapping", "polls", "auto_redemption", "live_content",
        // "match_session_info", "titles", "external_content", "testing",
        // "pending_work", "packages", "deviceUpdate", "people", "playable_content",
        // "temp", "ltp_licence_inventory", "cdn_metrics", "comparators",
        // "pc_prod_5_dec", "integration_templates", "cdn_auth_config",
        // "alertss_config", "isoLanguages", "stick_device", "gracenote_meta",
        // "alerts_config", "old_user_detail", "chatTopics", "alerts", "user_old",
        // "youtube_config", "cdn_metrics_history", "playable_content_debezium",
        // "list_config", "org_config", "notification_store_new", "alert_user",
        // "cp_config", "alert_triggers", "aggregation_config", "cms_roles",
        // "user_block_list", "language", "creators", "sequence_generator",
        // "iptv_mw_config", "language_config", "supply_config",
        // "language_channel_mapping", "demoTest", "reconcile", "app_config");

        totalCollections = collections.size();

        System.out.println("========================================");
        System.out.println("Kafka Connector Manager");
        System.out.println("Operation: " + operation);
        System.out.println("Start Timestamp (Epoch Sec): " + START_TIMESTAMP);
        System.out.println("========================================\n");

        LocalDateTime startTime = LocalDateTime.now();

        switch (operation) {
            case LIST -> listAllConnectors();
            case CREATE -> createConnectors(collections);
            case DELETE -> deleteConnectors(collections, ConnectorType.BOTH);
            case DELETE_SOURCE -> deleteConnectors(collections, ConnectorType.SOURCE);
            case DELETE_SINK -> deleteConnectors(collections, ConnectorType.SINK);
            case RESTART -> restartConnectors(collections, ConnectorType.BOTH);
            case RESTART_SOURCE -> restartConnectors(collections, ConnectorType.SOURCE);
            case RESTART_SINK -> restartConnectors(collections, ConnectorType.SINK);
        }

        if (operation != ConnectorOperation.LIST) {
            printSummary(startTime, LocalDateTime.now(), operation);
        }
    }

    // ==========================================
    // 4. CONNECTOR GENERATION METHODS
    // ==========================================

    private static void createConnectors(List<String> collections) {
        for (int i = 0; i < collections.size(); i++) {
            String col = collections.get(i);
            System.out.println(String.format("[%d/%d] Processing: %s", i + 1, totalCollections, col));
            createSourceConnector(col);
            createSinkConnector(col);
            System.out.println("--------------------");
        }
    }

    private static void createSourceConnector(String collectionName) {
        String connectorName = "source_" + collectionName;

        String startupConfig;
        if (STARTUP_MODE == StartupMode.TIMESTAMP) {
            startupConfig = String.format("""
                    "startup.mode": "timestamp",
                          "startup.mode.timestamp.start.at.operation.time": "%s",
                    """, START_TIMESTAMP);
        } else {
            startupConfig = String.format("""
                    "startup.mode": "%s",
                    """, STARTUP_MODE.name().toLowerCase());
        }

        // JSON Generation
        String jsonPayload = """
                {
                    "name": "%s",
                    "config": {
                      "connector.class": "com.mongodb.kafka.connect.MongoSourceConnector",
                      "tasks.max": "1",
                      "connection.uri": "%s",
                      "database": "%s",
                      "collection": "%s",
                      "topic.prefix": "%s",
                      %s
                      "output.format.value": "json",
                      "output.format.key": "json",
                      "key.converter": "org.apache.kafka.connect.storage.StringConverter",
                      "value.converter": "org.apache.kafka.connect.storage.StringConverter",
                      "key.converter.schemas.enable": "false",
                      "value.converter.schemas.enable": "false",
                      "publish.full.document.only": "true",
                      "publish.full.document.only.tombstone.on.delete": "true",
                      "change.stream.full.document": "updateLookup",
                      "errors.tolerance": "all",
                      "errors.log.enable": "true",
                      "producer.max.request.size": "5242880",
                      "producer.enable.idempotence": "true",
                      "producer.acks": "all",
                      "producer.override.security.protocol": "SASL_PLAINTEXT",
                      "producer.override.sasl.mechanism": "PLAIN",
                      "producer.override.sasl.jaas.config": "org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"%s\\\" password=\\\"%s\\\";"
                    }
                  }
                """
                .formatted(
                        connectorName, SOURCE_URI, DATABASE, collectionName, TOPIC_PREFIX,
                        startupConfig.trim(),
                        SOURCE_USER, SOURCE_PASS);

        System.out.println("  → Creating Source: " + connectorName);
        if (executeHttpPost(KAFKA_CONNECT_URL, jsonPayload, connectorName)) {
            successfulSourceConnectors++;
            System.out.println("  ✓ Source Created");
        } else {
            failedSourceConnectors++;
            System.out.println("  ✗ Source Failed");
        }
    }

    private static void createSinkConnector(String collectionName) {
        String connectorName = "sink_" + collectionName;
        String topicName = TOPIC_PREFIX + "." + DATABASE + "." + collectionName;

        // Strategy Lookup
        TopicConfig config = COLLECTION_RULES.getOrDefault(collectionName, COLLECTION_RULES.get("DEFAULT"));

        // JSON Generation
        String jsonPayload = """
                {
                    "name": "%s",
                    "config": {
                        "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector",
                        "tasks.max": "%d",
                        "topics": "%s",
                        "connection.uri": "%s",
                        "database": "%s",
                        "collection": "%s",
                        "consumer.auto.offset.reset": "earliest",
                        "key.converter": "org.apache.kafka.connect.storage.StringConverter",
                        "value.converter": "org.apache.kafka.connect.storage.StringConverter",
                        "key.converter.schemas.enable": "false",
                        "value.converter.schemas.enable": "false",
                        "document.id.strategy": "com.mongodb.kafka.connect.sink.processor.id.strategy.ProvidedInValueStrategy",
                        "document.id.strategy.overwrite.existing": "true",
                        "writemodel.strategy": "com.mongodb.kafka.connect.sink.writemodel.strategy.ReplaceOneDefaultStrategy",
                        "max.batch.size": "500",
                        "consumer.override.max.poll.records": "500",
                        "consumer.override.fetch.max.wait.ms": "100",
                        "consumer.session.timeout.ms": "45000",
                        "consumer.heartbeat.interval.ms": "10000",
                        "max.num.retries": "3",
                        "retries.defer.timeout": "5000",
                        "errors.tolerance": "all",
                        "errors.log.enable": "true",
                        "errors.log.include.messages": "true",
                        "errors.deadletterqueue.topic.name": "%s",
                        "errors.deadletterqueue.context.headers.enable": "true",
                        "errors.deadletterqueue.topic.replication.factor": "1",
                        "consumer.override.security.protocol": "SASL_PLAINTEXT",
                        "consumer.override.sasl.mechanism": "PLAIN",
                        "consumer.override.sasl.jaas.config": "org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"%s\\\" password=\\\"%s\\\";",
                        "consumer.override.max.partition.fetch.bytes": "5242880",
                        "consumer.override.fetch.max.bytes": "52428800"
                    }
                }
                """
                .formatted(
                        connectorName,
                        config.sinkTasks, // Dynamic Task Count
                        topicName, SINK_URI, DATABASE, collectionName,
                        DLQ_TOPIC, SINK_USER, SINK_PASS);

        System.out.println("  → Creating Sink: " + connectorName + " (Tasks: " + config.sinkTasks + ")");
        if (executeHttpPost(KAFKA_CONNECT_URL, jsonPayload, connectorName)) {
            successfulSinkConnectors++;
            System.out.println("  ✓ Sink Created");
        } else {
            failedSinkConnectors++;
            System.out.println("  ✗ Sink Failed");
        }
    }

    // ==========================================
    // 5. BULK OPERATION HELPERS
    // ==========================================

    private static void deleteConnectors(List<String> collections, ConnectorType type) {
        for (String col : collections) {
            if (type == ConnectorType.SOURCE || type == ConnectorType.BOTH) {
                deleteConnector("source_" + col);
            }
            if (type == ConnectorType.SINK || type == ConnectorType.BOTH) {
                deleteConnector("sink_" + col);
            }
        }
    }

    private static void restartConnectors(List<String> collections, ConnectorType type) {
        for (String col : collections) {
            if (type == ConnectorType.SOURCE || type == ConnectorType.BOTH) {
                restartConnector("source_" + col);
            }
            if (type == ConnectorType.SINK || type == ConnectorType.BOTH) {
                restartConnector("sink_" + col);
            }
        }
    }

    // ==========================================
    // 6. LOW-LEVEL HTTP METHODS (Full Implementation)
    // ==========================================

    private static void deleteConnector(String connectorName) {
        System.out.println("  → Deleting: " + connectorName);
        try {
            URL url = new URL(KAFKA_CONNECT_URL + "/" + connectorName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(5000);

            int code = conn.getResponseCode();

            if (code == 204 || code == 200) {
                System.out.println("    ✓ Deleted successfully");
                updateStats(connectorName, true);
            } else if (code == 404) {
                System.out.println("    ⚠ Not found (Skipped)");
                updateStats(connectorName, false);
            } else {
                System.err.println("    ✗ Failed (HTTP " + code + ")");
                updateStats(connectorName, false);
            }
        } catch (Exception e) {
            System.err.println("    ✗ Exception: " + e.getMessage());
            updateStats(connectorName, false);
        }
    }

    private static void restartConnector(String connectorName) {
        System.out.println("  → Restarting: " + connectorName);
        try {
            URL url = new URL(KAFKA_CONNECT_URL + "/" + connectorName + "/restart");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // POST requires output stream even if empty
            conn.setConnectTimeout(5000);

            int code = conn.getResponseCode();

            if (code == 204 || code == 200) {
                System.out.println("    ✓ Restarted successfully");
                updateStats(connectorName, true);
            } else if (code == 404) {
                System.out.println("    ⚠ Not found (Skipped)");
                updateStats(connectorName, false);
            } else {
                System.err.println("    ✗ Failed (HTTP " + code + ")");
                updateStats(connectorName, false);
            }
        } catch (Exception e) {
            System.err.println("    ✗ Exception: " + e.getMessage());
            updateStats(connectorName, false);
        }
    }

    private static boolean executeHttpPost(String urlString, String jsonPayload, String connectorName) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            // Read response body for debugging
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            if (responseCode == 201 || responseCode == 200) {
                return true;
            } else if (responseCode == 409) {
                System.out.println("    ⚠ Connector already exists");
                return false;
            } else {
                System.err.println("    ✗ HTTP Error: " + responseCode);
                System.err.println("    Error Details: " + response.toString());
                return false;
            }

        } catch (Exception e) {
            System.err.println("    ✗ Exception: " + e.getMessage());
            return false;
        }
    }

    private static void listAllConnectors() {
        System.out.println("Fetching Connector List...");
        try {
            URL url = new URL(KAFKA_CONNECT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Simple JSON array parsing (removing brackets/quotes)
                String rawJson = response.toString();
                String cleaned = rawJson.replace("[", "").replace("]", "").replace("\"", "");

                if (cleaned.trim().isEmpty()) {
                    System.out.println("No connectors found.");
                } else {
                    String[] connectors = cleaned.split(",");
                    System.out.println("\nFound " + connectors.length + " Connectors:");
                    for (String c : connectors) {
                        System.out.println(" - " + c.trim());
                    }
                }
            } else {
                System.err.println("Failed to get list. HTTP " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("Exception listing connectors: " + e.getMessage());
        }
    }

    // ==========================================
    // 7. UTILITY & STATS
    // ==========================================

    private static void updateStats(String connectorName, boolean success) {
        if (success) {
            if (connectorName.startsWith("source_"))
                successfulSourceConnectors++;
            else
                successfulSinkConnectors++;
        } else {
            if (connectorName.startsWith("source_"))
                failedSourceConnectors++;
            else
                failedSinkConnectors++;
        }
    }

    private static void printSummary(LocalDateTime startTime, LocalDateTime endTime, ConnectorOperation operation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("\n========================================");
        System.out.println("Execution Summary");
        System.out.println("========================================");
        System.out.println("Operation: " + operation);
        System.out.println("Duration: " + java.time.Duration.between(startTime, endTime).getSeconds() + " seconds");
        System.out.println("----------------------------------------");
        System.out.println("Source Connectors: " + successfulSourceConnectors + " Success / " + failedSourceConnectors
                + " Failed");
        System.out.println(
                "Sink Connectors:   " + successfulSinkConnectors + " Success / " + failedSinkConnectors + " Failed");
        System.out.println("========================================");
    }

    private static void printUsage() {
        System.out.println("Usage: java KafkaConnectorGenerator [OPERATION] [COLLECTIONS]");
        System.out.println(
                "Operations: CREATE, DELETE, RESTART, LIST, DELETE_SOURCE, DELETE_SINK, RESTART_SOURCE, RESTART_SINK");
        System.out.println("Collections: Comma-separated list (optional, defaults to mw_config,playable_content)");
        System.out.println("Example: java KafkaConnectorGenerator CREATE mw_config,playable_content,sport_info");
    }
}