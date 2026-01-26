package in.wynk.secret.manager.utils.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerGroupDeleter {

    public static void main(String[] args) {


        final String bootstrapServers = "10.164.88.21:9092,10.164.88.22:9092,10.164.88.23:9092";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";
        String topic = "msp.msp.content";
        String consumerGroup = "msp-processor-group-0ebe7973-97a1-4f62-b880-5d595a96e1f6";
//
//
//        String bootstrapServers = "10.161.24.22:9092,10.161.24.24:9092,10.161.24.23:9092";
//        String topic = "xstream-wcf-events";
//        String username = "appuser";
//        String password = "uJK67AUDI1Ax";
//        String consumerGroup = "republisher-1757430174";

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            props.put("security.protocol", "SASL_PLAINTEXT");
            props.put("sasl.mechanism", "PLAIN");
            props.put("sasl.jaas.config",
                    "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                            "username=\"" + username + "\" password=\"" + password + "\";");
        }

        try (AdminClient adminClient = AdminClient.create(props)) {
            adminClient.deleteConsumerGroups(Collections.singletonList(consumerGroup)).all().get();
            System.out.println("✅ Consumer group deleted: " + consumerGroup);
        } catch (Exception e) {
            System.err.println("❌ Failed to delete consumer group " + consumerGroup + ": " + e.getMessage());
        }
    }
}
