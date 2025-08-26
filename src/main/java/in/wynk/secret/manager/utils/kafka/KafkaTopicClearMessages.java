package in.wynk.secret.manager.utils.kafka;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;

public class KafkaTopicClearMessages {

    public static void main(String[] args) {


        String bootstrapServers = "10.161.24.16:9092,10.161.24.17:9092,10.161.24.18:9092";
        String topicName = "atv-continue-watching-prod";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";



        // Kafka AdminClient properties with security
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                                           "username=\"" + username + "\" " +
                                           "password=\"" + password + "\";");

        try (AdminClient adminClient = AdminClient.create(properties)) {
            // Set retention.ms to 1 day (86400000 milliseconds)
            ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
            ConfigEntry retentionEntry = new ConfigEntry("retention.ms", "86400000");

            // Apply retention configuration
            Map<ConfigResource, Collection<AlterConfigOp>> configUpdates = Collections.singletonMap(
                configResource, Collections.singleton(new AlterConfigOp(retentionEntry, AlterConfigOp.OpType.SET))
            );
            adminClient.incrementalAlterConfigs(configUpdates).all().get();

            System.out.println("Retention set to 1 day for topic: " + topicName);

        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error setting retention for topic: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupt status
        }
    }
}
