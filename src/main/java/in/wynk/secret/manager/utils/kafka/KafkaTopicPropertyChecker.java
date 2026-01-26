package in.wynk.secret.manager.utils.kafka;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaTopicPropertyChecker {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String bootstrapServers = "10.161.24.16:9092,10.161.24.17:9092,10.161.24.18:9092";
        String topicName = "atv-events-prod-iptv";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";

        // Kafka AdminClient properties with security
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"" + username + "\" password=\"" + password + "\";");

        try (AdminClient adminClient = AdminClient.create(props)) {

            // --- Topic Description ---
            DescribeTopicsResult describeTopics = adminClient.describeTopics(Collections.singletonList(topicName));
            TopicDescription topicDescription = describeTopics.topicNameValues().get(topicName).get();

            int partitions = topicDescription.partitions().size();
            int replicationFactor = topicDescription.partitions().get(0).replicas().size();

            System.out.println("========== Topic Description ==========");
            System.out.println("Topic Name        : " + topicName);
            System.out.println("Partition Count   : " + partitions);
            System.out.println("Replication Factor: " + replicationFactor);

            // --- Topic Configs ---
            ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
            DescribeConfigsResult configsResult = adminClient.describeConfigs(Collections.singletonList(configResource));
            Config config = configsResult.all().get().get(configResource);


            Map<String, String> explicitConfigs = new LinkedHashMap<>();
            Map<String, String> defaultConfigs = new LinkedHashMap<>();

            for (ConfigEntry entry : config.entries()) {
                if (entry.isDefault()) {
                    defaultConfigs.put(entry.name(), entry.value());
                } else {
                    explicitConfigs.put(entry.name(), entry.value());
                }
            }

            // Print explicit configs
            System.out.println("\n========== Explicitly Set Configs ==========");
            explicitConfigs.forEach((k, v) -> System.out.println(k + " = " + v));

            // Print default configs
            System.out.println("\n========== Default (Broker) Configs ==========");
            defaultConfigs.forEach((k, v) -> System.out.println(k + " = " + v));

            // --- Analysis ---
            System.out.println("\n========== Key Config Analysis ==========");

            printConfigAnalysis("retention.ms", explicitConfigs, defaultConfigs, "Message retention duration");
            printConfigAnalysis("segment.bytes", explicitConfigs, defaultConfigs, "Max segment file size");
            printConfigAnalysis("cleanup.policy", explicitConfigs, defaultConfigs, "Log cleanup policy (delete/compact)");
            printConfigAnalysis("segment.ms", explicitConfigs, defaultConfigs, "Segment time rollover interval");
            printConfigAnalysis("retention.bytes", explicitConfigs, defaultConfigs, "Total log size limit per partition");
            printConfigAnalysis("max.message.bytes", explicitConfigs, defaultConfigs, "Max message size allowed");
            
            // I/O Specific Checks
            printConfigAnalysis("compression.type", explicitConfigs, defaultConfigs, "Compression (producer/snappy/lz4/zstd)");
            printConfigAnalysis("flush.messages", explicitConfigs, defaultConfigs, "Fsync every N messages (bad if set)");
            printConfigAnalysis("flush.ms", explicitConfigs, defaultConfigs, "Fsync every N ms (bad if set)");
            printConfigAnalysis("index.interval.bytes", explicitConfigs, defaultConfigs, "Index entry frequency");

        } catch (Exception e) {
            System.err.println("Error fetching topic properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printConfigAnalysis(String key, Map<String, String> explicit, Map<String, String> defaults, String description) {
        String value = explicit.getOrDefault(key, defaults.getOrDefault(key, "N/A"));
        String source = explicit.containsKey(key) ? "Explicitly Set" : "Default";
        System.out.printf("%-20s : %-20s [%s] - %s%n", key, value, source, description);
    }
}
