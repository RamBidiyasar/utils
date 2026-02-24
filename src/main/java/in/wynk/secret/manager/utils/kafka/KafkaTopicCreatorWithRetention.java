package in.wynk.secret.manager.utils.kafka;

import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaTopicCreatorWithRetention {
    public static final int RETENTION_HOURS = 24*2;

    public static void main(String[] args) {
        String bootstrapServers = "10.161.24.22:9092,10.161.24.23:9092,10.161.24.24:9092";
//        String bootstrapServers = "10.161.24.16:9092,10.161.24.17:9092,10.161.24.18:9092";
        String topicName = "content-partner-analytics-details";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";


//
//        String bootstrapServers = "10.169.24.13:9092";
//        String topicName = "content-partner-analytics-details";
//        String username = "appuser";
//        String password = "uJK67dC1Ax";

        // Kafka AdminClient properties with security
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                                           "username=\"" + username + "\" " +
                                           "password=\"" + password + "\";");

        try (AdminClient adminClient = AdminClient.create(properties)) {

            // Check if topic exists
            Set<String> existingTopics = adminClient.listTopics().names().get();
            if (existingTopics.contains(topicName)) {
                System.out.println("Topic already exists: " + topicName);
            } else {
                // Create topic with 1 partition and replication factor 1
                NewTopic newTopic = new NewTopic(topicName, 8, (short) 1);
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
                System.out.println("Topic created: " + topicName);
            }

            ConfigResource configResource = new ConfigResource(Type.TOPIC, topicName);

            ConfigEntry retentionEntry = new ConfigEntry("retention.ms", String.valueOf(TimeUnit.HOURS.toMillis(RETENTION_HOURS)));

            Map<ConfigResource, Collection<AlterConfigOp>> configUpdates = Collections.singletonMap(
                configResource,
                Collections.singletonList(new AlterConfigOp(retentionEntry, AlterConfigOp.OpType.SET))
            );

            adminClient.incrementalAlterConfigs(configUpdates).all().get();
            System.out.println("Retention updated to " + RETENTION_HOURS + " hours for topic: " + topicName);

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error managing topic: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
