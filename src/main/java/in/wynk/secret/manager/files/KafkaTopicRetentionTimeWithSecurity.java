package in.wynk.secret.manager.files;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaTopicRetentionTimeWithSecurity {
    public static void main(String[] args) {
        String bootstrapServers = "10.161.24.22:9092";
        String topicName = "atv.atv.playable_content";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";
//        String consumerGroupId = "secure_consumer_group";
        String consumerGroupId = "playable-content-changelog-guzzler-test"; // Replace with the actual consumer group ID

        // Kafka AdminClient properties with security
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"" + username + "\" " +
                "password=\"" + password + "\";");

        try (AdminClient adminClient = AdminClient.create(properties)) {
            // Describe topic configuration
            ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
            DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Collections.singleton(configResource));

            // Fetch the configurations
            Config config = describeConfigsResult.all().get().get(configResource);

            System.out.println(config.entries());
            // Retrieve the retention.ms value
            String retentionMs = config.get("retention.ms") != null ? config.get("retention.ms").value() : "Not Set";
            System.out.println("Retention Time (ms): " + retentionMs);

            // Delete the consumer group
            DeleteConsumerGroupsResult deleteConsumerGroupsResult = adminClient.deleteConsumerGroups(Collections.singleton(consumerGroupId));
            deleteConsumerGroupsResult.all().get(); // Block until the deletion completes
            System.out.println("Consumer group " + consumerGroupId + " deleted successfully.");
        } catch (ExecutionException e) {
            System.err.println("Error during operation: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Operation interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
