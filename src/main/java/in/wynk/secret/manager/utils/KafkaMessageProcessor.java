package in.wynk.secret.manager.utils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KafkaMessageProcessor {

    private static final String BOOTSTRAP_SERVERS = "10.164.88.21:9092,10.164.88.22:9092,10.164.88.23:9092";
    private static final String USERNAME = "appuser";
    private static final String PASSWORD = "uJK67AUDI1Ax";
    private static final String SOURCE_TOPIC = "msp.msp.content";
    private static final String TARGET_TOPIC = "msp-processing-prd-app";
    private static final String FILTER_STRING = "Mere Karan Arjun aayenge";
    private static final int CONCURRENCY = 32;
    private static final String GROUP_ID = "msp-processor-group-" + UUID.randomUUID();

    public static void main(String[] args) {
        // 1. Initialize Producer (Shared)
        Properties producerProps = getProducerProps();
        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

        // 2. Initialize Executor for Consumers
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENCY);

        System.out.println("Starting " + CONCURRENCY + " consumers for topic: " + SOURCE_TOPIC);
        System.out.println("Target topic for matches: " + TARGET_TOPIC);

        for (int i = 0; i < CONCURRENCY; i++) {
            int threadId = i;
            executor.submit(() -> {
                Properties consumerProps = getConsumerProps();
                // Ensure all consumers share the group to distribute partitions, 
                // or use unique groups if you want broadcast (unlikely here, assuming distribution).
                // Given "concurrency of 32", usually means parallel processing of partitions.
                consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
                
                try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
                    consumer.subscribe(Collections.singletonList(SOURCE_TOPIC));
                    
                    System.out.println("Consumer thread " + threadId + " started.");

                    while (!Thread.currentThread().isInterrupted()) {
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                        
                        for (ConsumerRecord<String, String> record : records) {
                            String value = record.value();
                            if (value != null && value.contains(FILTER_STRING)) {
                                System.out.println("Match found in partition " + record.partition() + " offset " + record.offset());
                                producer.send(new ProducerRecord<>(TARGET_TOPIC, value));
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Consumer thread " + threadId + " error: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            executor.shutdownNow();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            producer.close();
            System.out.println("Shutdown complete.");
        }));
    }

    private static Properties getProducerProps() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        addSecurityProps(props);
        return props;
    }

    private static Properties getConsumerProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); 
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        addSecurityProps(props);
        return props;
    }

    private static void addSecurityProps(Properties props) {
        if (USERNAME != null && !USERNAME.isEmpty()) {
            props.put("security.protocol", "SASL_PLAINTEXT");
            props.put("sasl.mechanism", "PLAIN");
            props.put("sasl.jaas.config", 
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + USERNAME + "\" password=\"" + PASSWORD + "\";");
        }
    }
}
