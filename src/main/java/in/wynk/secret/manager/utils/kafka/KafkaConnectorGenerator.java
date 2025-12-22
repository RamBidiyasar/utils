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
import java.util.List;

/**
 * Kafka Connector Generator and Executor
 * Manages MongoDB Kafka Source and Sink Connectors with multiple operations
 */
public class KafkaConnectorGenerator {

    // Kafka Connect Configuration
    private static final String KAFKA_CONNECT_URL = "http://10.169.24.13:8083/connectors";
    private static final String DATABASE = "atv";
    private static final String TOPIC_PREFIX = "mongo";

    // Source Connector Configuration
    private static final String SOURCE_CONNECTION_URI = "mongodb://admin:M0ng0DB%40P%40%24%24w0rd%21@10.169.24.26:27017/?replicaSet=rs1&authSource=admin";
    private static final String SOURCE_SASL_USERNAME = "appuser";
    private static final String SOURCE_SASL_PASSWORD = "uJK67dC1Ax";

    // Sink Connector Configuration
    private static final String SINK_CONNECTION_URI = "mongodb://admin:xstrm%401234@10.169.24.27:27017/?authSource=admin&connectTimeoutMS=10000";
    private static final String SINK_SASL_USERNAME = "appuser";
    private static final String SINK_SASL_PASSWORD = "uJK67dC1Ax";
    private static final String DLQ_TOPIC = "mongo.dlq.errors";

    /**
     * Enum for connector operations
     */
    public enum ConnectorOperation {
        CREATE, // Create new connectors
        DELETE, // Delete existing connectors
        RESTART, // Restart existing connectors
        LIST, // List all connectors
        DELETE_SOURCE, // Delete only source connectors
        DELETE_SINK, // Delete only sink connectors
        RESTART_SOURCE, // Restart only source connectors
        RESTART_SINK // Restart only sink connectors
    }

    /**
     * Enum for connector type
     */
    public enum ConnectorType {
        SOURCE,
        SINK,
        BOTH
    }

    // Statistics
    private static int totalCollections = 0;
    private static int successfulSourceConnectors = 0;
    private static int failedSourceConnectors = 0;
    private static int successfulSinkConnectors = 0;
    private static int failedSinkConnectors = 0;

    public static void main(String[] args) {
        ConnectorOperation operation = ConnectorOperation.CREATE;

        if (args.length > 0) {
            try {
                operation = ConnectorOperation.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid operation: " + args[0]);
                printUsage();
                return;
            }
        }


        List<String> collections = List.of("mw_config", "playable_content");

//        List<String> collections = Arrays.asList("ltp_channel_inventory", "sport_info", "promotional_event", "device_notification", "system.profile", "sequence", "dummy_channel_info", "notification_store", "box_details", "failed_wcf_events", "filtered_shows", "pages", "game_meta", "hotstar_highlights", "match_questions", "cpRule", "theme_config", "box_qms", "airtel_only_config", "mw_config", "rails_copy", "title_akas", "user_otp", "cms_user", "bb_upgrade_order", "rails", "playable_intermediate_content", "product_channel_mapping", "polls", "auto_redemption", "live_content", "match_session_info", "titles", "external_content", "testing", "pending_work", "packages", "deviceUpdate", "people", "playable_content", "temp", "ltp_licence_inventory", "cdn_metrics", "comparators", "pc_prod_5_dec", "integration_templates", "cdn_auth_config", "alertss_config", "isoLanguages", "stick_device", "gracenote_meta", "alerts_config", "old_user_detail", "chatTopics", "alerts", "user_old", "youtube_config", "cdn_metrics_history", "playable_content_debezium", "list_config", "org_config", "notification_store_new", "alert_user", "cp_config", "alert_triggers", "aggregation_config", "cms_roles", "user_block_list", "language", "creators", "sequence_generator", "iptv_mw_config", "language_config", "supply_config", "language_channel_mapping", "demoTest", "reconcile", "app_config");

        totalCollections = collections.size();

        System.out.println("========================================");
        System.out.println("Kafka Connector Manager");
        System.out.println("Operation: " + operation);
        System.out.println("Kafka Connect URL: " + KAFKA_CONNECT_URL);
        System.out.println("Database: " + DATABASE);
        System.out.println("========================================\n");

        LocalDateTime startTime = LocalDateTime.now();

        // Execute operation based on type
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

        LocalDateTime endTime = LocalDateTime.now();

        if (operation != ConnectorOperation.LIST) {
            printSummary(startTime, endTime, operation);
        }
    }

    /**
     * Prints usage information
     */
    private static void printUsage() {
        System.out.println("\nUsage: java KafkaConnectorGenerator [OPERATION]\n");
        System.out.println("Available Operations:");
        System.out.println("  CREATE          - Create both source and sink connectors (default)");
        System.out.println("  DELETE          - Delete both source and sink connectors");
        System.out.println("  DELETE_SOURCE   - Delete only source connectors");
        System.out.println("  DELETE_SINK     - Delete only sink connectors");
        System.out.println("  RESTART         - Restart both source and sink connectors");
        System.out.println("  RESTART_SOURCE  - Restart only source connectors");
        System.out.println("  RESTART_SINK    - Restart only sink connectors");
        System.out.println("  LIST            - List all connectors\n");
        System.out.println("Example: java KafkaConnectorGenerator DELETE_SOURCE\n");
    }

    /**
     * Creates connectors for all collections
     */
    private static void createConnectors(List<String> collections) {
        System.out.println("Total Collections: " + totalCollections + "\n");

        for (int i = 0; i < collections.size(); i++) {
            String collection = collections.get(i);
            System.out.println(String.format("[%d/%d] Processing Collection: %s",
                    i + 1, totalCollections, collection));

            createSourceConnector(collection);
            createSinkConnector(collection);

            System.out.println("--------------------\n");
        }
    }

    /**
     * Deletes connectors for all collections
     */
    private static void deleteConnectors(List<String> collections, ConnectorType type) {
        System.out.println("Total Collections: " + totalCollections + "\n");

        for (int i = 0; i < collections.size(); i++) {
            String collection = collections.get(i);
            System.out.println(String.format("[%d/%d] Processing Collection: %s",
                    i + 1, totalCollections, collection));

            if (type == ConnectorType.SOURCE || type == ConnectorType.BOTH) {
                deleteConnector("source_" + collection);
            }

            if (type == ConnectorType.SINK || type == ConnectorType.BOTH) {
                deleteConnector("sink_" + collection);
            }

            System.out.println("--------------------\n");
        }
    }

    /**
     * Restarts connectors for all collections
     */
    private static void restartConnectors(List<String> collections, ConnectorType type) {
        System.out.println("Total Collections: " + totalCollections + "\n");

        for (int i = 0; i < collections.size(); i++) {
            String collection = collections.get(i);
            System.out.println(String.format("[%d/%d] Processing Collection: %s",
                    i + 1, totalCollections, collection));

            if (type == ConnectorType.SOURCE || type == ConnectorType.BOTH) {
                restartConnector("source_" + collection);
            }

            if (type == ConnectorType.SINK || type == ConnectorType.BOTH) {
                restartConnector("sink_" + collection);
            }

            System.out.println("--------------------\n");
        }
    }

    /**
     * Lists all connectors
     */
    private static void listAllConnectors() {
        try {
            URL url = new URL(KAFKA_CONNECT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (responseCode == 200) {
                System.out.println("All Connectors:\n");
                // Parse the JSON array response
                String connectorsJson = response.toString();
                // Remove brackets and quotes, split by comma
                String connectors = connectorsJson.replace("[", "").replace("]", "")
                        .replace("\"", "");

                if (connectors.trim().isEmpty()) {
                    System.out.println("  No connectors found");
                } else {
                    String[] connectorArray = connectors.split(",");
                    for (int i = 0; i < connectorArray.length; i++) {
                        System.out.println("  " + (i + 1) + ". " + connectorArray[i].trim());
                    }
                    System.out.println("\nTotal: " + connectorArray.length + " connectors");
                }
            } else {
                System.err.println("Failed to list connectors. HTTP Code: " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("Exception occurred while listing connectors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes a specific connector
     */
    private static void deleteConnector(String connectorName) {
        System.out.println("  → Deleting Connector: " + connectorName);

        try {
            URL url = new URL(KAFKA_CONNECT_URL + "/" + connectorName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();

            if (responseCode == 204 || responseCode == 200) {
                System.out.println("  ✓ Connector Deleted Successfully");
                if (connectorName.startsWith("source_")) {
                    successfulSourceConnectors++;
                } else {
                    successfulSinkConnectors++;
                }
            } else if (responseCode == 404) {
                System.out.println("  ⚠ Connector not found: " + connectorName);
                if (connectorName.startsWith("source_")) {
                    failedSourceConnectors++;
                } else {
                    failedSinkConnectors++;
                }
            } else {
                System.err.println("  ✗ Failed to delete connector. HTTP Code: " + responseCode);
                if (connectorName.startsWith("source_")) {
                    failedSourceConnectors++;
                } else {
                    failedSinkConnectors++;
                }
            }

        } catch (Exception e) {
            System.err.println("  ✗ Exception occurred: " + e.getMessage());
            if (connectorName.startsWith("source_")) {
                failedSourceConnectors++;
            } else {
                failedSinkConnectors++;
            }
        }
    }

    /**
     * Restarts a specific connector
     */
    private static void restartConnector(String connectorName) {
        System.out.println("  → Restarting Connector: " + connectorName);

        try {
            URL url = new URL(KAFKA_CONNECT_URL + "/" + connectorName + "/restart");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();

            if (responseCode == 204 || responseCode == 200) {
                System.out.println("  ✓ Connector Restarted Successfully");
                if (connectorName.startsWith("source_")) {
                    successfulSourceConnectors++;
                } else {
                    successfulSinkConnectors++;
                }
            } else if (responseCode == 404) {
                System.out.println("  ⚠ Connector not found: " + connectorName);
                if (connectorName.startsWith("source_")) {
                    failedSourceConnectors++;
                } else {
                    failedSinkConnectors++;
                }
            } else {
                System.err.println("  ✗ Failed to restart connector. HTTP Code: " + responseCode);
                if (connectorName.startsWith("source_")) {
                    failedSourceConnectors++;
                } else {
                    failedSinkConnectors++;
                }
            }

        } catch (Exception e) {
            System.err.println("  ✗ Exception occurred: " + e.getMessage());
            if (connectorName.startsWith("source_")) {
                failedSourceConnectors++;
            } else {
                failedSinkConnectors++;
            }
        }
    }

    /**
     * Creates and executes a source connector for the given collection
     */
    private static void createSourceConnector(String collectionName) {
        String connectorName = "source_" + collectionName;
        String jsonPayload = generateSourceConnectorJson(connectorName, collectionName);

        System.out.println("  → Creating Source Connector: " + connectorName);

        boolean success = executeHttpPost(KAFKA_CONNECT_URL, jsonPayload, connectorName);

        if (success) {
            successfulSourceConnectors++;
            System.out.println("  ✓ Source Connector Created Successfully");
        } else {
            failedSourceConnectors++;
            System.out.println("  ✗ Source Connector Creation Failed");
        }
    }

    /**
     * Creates and executes a sink connector for the given collection
     */
    private static void createSinkConnector(String collectionName) {
        String connectorName = "sink_" + collectionName;
        String topicName = TOPIC_PREFIX + "." + DATABASE + "." + collectionName;
        String jsonPayload = generateSinkConnectorJson(connectorName, collectionName, topicName);

        System.out.println("  → Creating Sink Connector: " + connectorName);

        boolean success = executeHttpPost(KAFKA_CONNECT_URL, jsonPayload, connectorName);

        if (success) {
            successfulSinkConnectors++;
            System.out.println("  ✓ Sink Connector Created Successfully");
        } else {
            failedSinkConnectors++;
            System.out.println("  ✗ Sink Connector Creation Failed");
        }
    }

    /**
     * Executes HTTP POST request
     */
    private static boolean executeHttpPost(String urlString, String jsonPayload, String connectorName) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            // Write payload
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get response
            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();

            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (responseCode == 201 || responseCode == 200) {
                // System.out.println(" Response Code: " + responseCode);
                // System.out.println(" Response: " + response.toString());
                return true;
            } else if (responseCode == 409) {
                System.out.println("    Connector already exists: " + connectorName);
                // System.out.println(" Response: " + response.toString());
                return false;
            } else {
                System.err.println("    HTTP Error Code: " + responseCode);
                System.err.println("    Error Response: " + response.toString());
                return false;
            }

        } catch (Exception e) {
            System.err.println("    Exception occurred: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generates JSON payload for source connector
     */
    private static String generateSourceConnectorJson(String connectorName, String collectionName) {
        return """
                {
                    "name": "%s",
                    "config": {
                      "connector.class": "com.mongodb.kafka.connect.MongoSourceConnector",
                      "tasks.max": "1",
                      "connection.uri": "%s",
                      "database": "%s",
                      "collection": "%s",
                      "topic.prefix": "%s",
                      "startup.mode" : "copy_existing",
                      "output.format.value": "json",
                      "output.format.key": "json",
                      "publish.full.document.only": "false",
                      "change.stream.full.document": "updateLookup",
                      "key.converter": "org.apache.kafka.connect.storage.StringConverter",
                      "value.converter": "org.apache.kafka.connect.storage.StringConverter",
                      "producer.max.request.size": "5242880",
                      "producer.override.security.protocol": "SASL_PLAINTEXT",
                      "producer.override.sasl.mechanism": "PLAIN",
                      "producer.override.sasl.jaas.config": "org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"%s\\\" password=\\\"%s\\\";"
                    }
                  }
                """
                .formatted(
                        connectorName,
                        SOURCE_CONNECTION_URI,
                        DATABASE,
                        collectionName,
                        TOPIC_PREFIX,
                        SOURCE_SASL_USERNAME,
                        SOURCE_SASL_PASSWORD);
    }

    /**
     * Generates JSON payload for sink connector
     */
    private static String generateSinkConnectorJson(String connectorName, String collectionName, String topicName) {
        return """
            {
                "name": "%s",
                "config": {
                    "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector",
                    "tasks.max": "1",
                    "topics": "%s",
                    "connection.uri": "%s",
                    "database": "%s",
                    "collection": "%s",
                    "key.converter.schemas.enable": "false",
                    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
                    "value.converter": "org.apache.kafka.connect.storage.StringConverter",
                    "value.converter.schemas.enable": "false",
                    "document.id.strategy": "com.mongodb.kafka.connect.sink.processor.id.strategy.ProvidedInKeyStrategy",
                    "key.projection.list": "_id",
                    "document.id.strategy.overwrite.existing": "true",
                    "change.data.capture.handler": "com.mongodb.kafka.connect.sink.cdc.mongodb.ChangeStreamHandler",
                    "writemodel.strategy": "com.mongodb.kafka.connect.sink.writemodel.strategy.ReplaceOneDefaultStrategy",
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
                        topicName,
                        SINK_CONNECTION_URI,
                        DATABASE,
                        collectionName,
                        DLQ_TOPIC,
                        SINK_SASL_USERNAME,
                        SINK_SASL_PASSWORD);
    }

    /**
     * Prints execution summary
     */
    private static void printSummary(LocalDateTime startTime, LocalDateTime endTime, ConnectorOperation operation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("\n========================================");
        System.out.println("Execution Summary");
        System.out.println("========================================");
        System.out.println("Operation: " + operation);
        System.out.println("Start Time: " + startTime.format(formatter));
        System.out.println("End Time: " + endTime.format(formatter));
        System.out.println("Duration: " + java.time.Duration.between(startTime, endTime).getSeconds() + " seconds");
        System.out.println("----------------------------------------");
        System.out.println("Total Collections: " + totalCollections);
        System.out.println("Source Connectors:");
        System.out.println("  ✓ Successful: " + successfulSourceConnectors);
        System.out.println("  ✗ Failed: " + failedSourceConnectors);
        System.out.println("Sink Connectors:");
        System.out.println("  ✓ Successful: " + successfulSinkConnectors);
        System.out.println("  ✗ Failed: " + failedSinkConnectors);
        System.out.println("----------------------------------------");
        System.out.println("Total Successful: " + (successfulSourceConnectors + successfulSinkConnectors));
        System.out.println("Total Failed: " + (failedSourceConnectors + failedSinkConnectors));
        System.out.println("========================================");
    }
}
