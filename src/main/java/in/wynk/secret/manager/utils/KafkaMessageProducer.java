package in.wynk.secret.manager.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KafkaMessageProducer {

    private static final int NUM_THREADS = 10;  // Number of threads to increase throughput
    private static final long DURATION_IN_SECONDS = 3600;  // One hour in seconds
    private static final int MESSAGES_PER_SECOND_PER_THREAD = 1000;  // Adjust based on desired message throughput

    public static void main(String[] args) throws InterruptedException {
        String serversDetails = "sdflkdh";
        String topic = "Topic01";
        String username = "appuser";
        String password = "uJK67dC1Ax";

        // Initialize Kafka producer configuration
        Map<String, Object> configProps = gcpConfigProps(serversDetails, username, password);
        KafkaProducer<String, String> producer = new KafkaProducer<>(configProps);

        // Create thread pool for producing messages in parallel
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        long startTime = System.currentTimeMillis();
        long endTime = startTime + TimeUnit.SECONDS.toMillis(DURATION_IN_SECONDS);

        // Schedule tasks to produce messages
        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                try {
                    while (System.currentTimeMillis() < endTime) {
                        for (int j = 0; j < MESSAGES_PER_SECOND_PER_THREAD; j++) {
                            String message = "Message-" + System.currentTimeMillis();
                            producer.send(new ProducerRecord<>(topic, message));
                        }
                        TimeUnit.SECONDS.sleep(1);  // Control the rate of message production
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Shutdown the executor and producer after the duration
        executor.shutdown();
        executor.awaitTermination(DURATION_IN_SECONDS + 10, TimeUnit.SECONDS);
        producer.close();
    }

    public static Map<String, Object> gcpConfigProps(String serversDetails, String username, String password) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serversDetails);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            configProps.put("security.protocol", "SASL_PLAINTEXT");
            configProps.put("sasl.mechanism", "PLAIN");
            configProps.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + username + "\" password=\"" + password + "\";");
        }
        configProps.put(ProducerConfig.ACKS_CONFIG, "1");
        return configProps;
    }
}
