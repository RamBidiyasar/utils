package in.wynk.secret.manager.utils.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaTopicDeleter {

    public static void main(String[] args) {
//        String bootstrapServers = "10.169.24.13:9092";
//        String topicName = "atv-continue-watching-sync-preprod";
//        String username = "appuser";
//        String password = "uJK67dC1Ax";


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

            // Delete the topic
            adminClient.deleteTopics(Collections.singletonList(topicName)).all().get();
            System.out.println("Topic deleted successfully: " + topicName);

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error deleting topic: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
