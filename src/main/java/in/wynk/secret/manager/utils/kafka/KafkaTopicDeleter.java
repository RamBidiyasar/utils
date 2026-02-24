package in.wynk.secret.manager.utils.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class KafkaTopicDeleter {

    public static void main(String[] args) {

        String bootstrapServers = "10.169.24.13:9092";
        String username = "appuser";
        String password = "uJK67dC1Ax";
        String topicPrefix = "partner-analytics-details";


        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        properties.put(
                "sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"" + username + "\" " +
                        "password=\"" + password + "\";"
        );

        try (AdminClient adminClient = AdminClient.create(properties)) {

            // 1. Fetch all topics
            Set<String> allTopics = adminClient.listTopics().names().get();

            // 2. Filter topics by prefix
            List<String> topicsToDelete = allTopics.stream()
                    .filter(topic -> topic.startsWith(topicPrefix))
                    .collect(Collectors.toList());

            if (topicsToDelete.isEmpty()) {
                System.out.println("No topics found with prefix: " + topicPrefix);
                return;
            }

            // 3. Delete topics
            adminClient.deleteTopics(topicsToDelete).all().get();

            System.out.println("Deleted topics:");
            topicsToDelete.forEach(System.out::println);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while deleting topics");
        } catch (ExecutionException e) {
            System.err.println("Error deleting topics: " + e.getMessage());
        }
    }
}
