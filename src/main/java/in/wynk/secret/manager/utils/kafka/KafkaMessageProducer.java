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
        String bootstrapServers = "10.168.88.20:9092";
        String topicName = "wcf_preprod.payment_renewal_charging";
        String username = "appuser";
        String password = "uJK67dC1Ax";
        String key = "825db3c3-e885-4bc3-b026-e051169c4179";
        String value = """
            {
            	"attemptSequence": 2,
            	"id": "0cd2d7e1-6b77-11f0-ae79-931f52d93b80",
            	"uid": "MukmuUPVfhPbZ0gGS0",
            	"msisdn": "+913111111126",
            	"clientAlias": "airtelxstream",
            	"paymentCode": "APS",
            	"planId": 6488
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
