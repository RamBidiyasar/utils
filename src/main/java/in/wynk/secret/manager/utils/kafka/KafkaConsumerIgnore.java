package in.wynk.secret.manager.utils.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaConsumerIgnore {

    public static void main(String[] args) {
        String bootstrapServers = "10.169.24.13:9092";
        String consumerGroup = "xstream-2";
        String topic = "new1";
        String username = "appuser";
        String password = "uJK67dC1Ax";

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // or "latest"

        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            props.put("security.protocol", "SASL_PLAINTEXT");
            props.put("sasl.mechanism", "PLAIN");
            props.put("sasl.jaas.config",
                    "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                            "username=\"" + username + "\" password=\"" + password + "\";");
        }

        // Create one consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));

        // 64 workers to process (ignore) messages concurrently
        ExecutorService executor = Executors.newFixedThreadPool(64);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            consumer.wakeup();
            executor.shutdown();
        }));

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> record : records) {
                    executor.submit(() -> {
                        System.out.println("Received message: " + record.value() + ", partition: " + record.partition() + ", offset: " + record.offset());
                        // Ignore the message, just discard
                        // Could log partition/offset if needed
                         System.out.printf("Ignored message at partition=%d offset=%d%n", record.partition(), record.offset());
                    });
                }
                consumer.seekToBeginning(consumer.assignment());
            }
        } catch (Exception e) {
            System.err.println("Consumer stopped: " + e.getMessage());
        } finally {
            consumer.close();
            executor.shutdown();
        }
    }
}
