package in.wynk.secret.manager.utils.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaMessageProducer {

    public static void main(String[] args) {
        String bootstrapServers = "10.161.24.22:9092,10.161.24.24:9092,10.161.24.23:9092";
        String topicName = "xstream-wcf-events";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";
        String key = "825db3c3-e885-4bc3-b026-e051169c4179";
        String value = """
            {
              "uid": "csuXNH1Dn5DgKtXYd0",
              "msisdn": "9761697439",
              "event": "PURCHASE",
              "planId": 690,
              "validTillDate": 1760783146219,
              "autoRenewal": false,
              "preferredPartner": false,
              "referenceId": "f3970934-1f6d-11eb-afe2-7f046742a12d322233222222",
              "planPurchaseDate": 1758191146219,
              "platformSource": null,
              "telcoUnlimited": false,
              "siIdentifier": null
            }
            """;

        // Kafka Producer properties with security
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                                           "username=\"" + username + "\" " +
                                           "password=\"" + password + "\";");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, value);
            RecordMetadata metadata = producer.send(record).get(); // Synchronous send

            System.out.printf("Message sent to topic %s partition %d offset %d%n",
                    metadata.topic(), metadata.partition(), metadata.offset());

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error sending message to Kafka: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
