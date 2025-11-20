package in.wynk.secret.manager.utils.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class KafkaTopicLister {

    public static void main(String[] args) {
        // Replace with your Kafka bootstrap servers
        String bootstrapServers = "10.161.24.22:9092,10.161.24.24:9092,10.161.24.23:9092";
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
            ListTopicsResult topics = adminClient.listTopics();
            Set<String> topicNames = topics.names().get();

            System.out.println("List of Kafka topics:");
            topicNames.forEach(System.out::println);

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error listing topics: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
