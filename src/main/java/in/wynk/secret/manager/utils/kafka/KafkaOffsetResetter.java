package in.wynk.secret.manager.utils.kafka;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.ListOffsetsResult.ListOffsetsResultInfo;
import org.apache.kafka.clients.admin.AlterConsumerGroupOffsetsResult;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class KafkaOffsetResetter {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String bootstrapServers = "10.161.24.22:9092,10.161.24.24:9092,10.161.24.23:9092";
        String topic = "xstream-wcf-events";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";
        String consumerGroup = "republisher-group-2";

        // Get tomorrow's date
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Set time to 02:10
        LocalDateTime dateTime = tomorrow.atTime(2, 10);

        // Convert to epoch millis (system default zone)
        long timestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            props.put("security.protocol", "SASL_PLAINTEXT");
            props.put("sasl.mechanism", "PLAIN");
            props.put("sasl.jaas.config",
                    "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                            "username=\"" + username + "\" password=\"" + password + "\";");
        }
        props.put(ProducerConfig.ACKS_CONFIG, "1");

        try (AdminClient adminClient = AdminClient.create(props)) {

            // 1. Get partitions for the topic
            List<TopicPartition> partitions = new ArrayList<>();
            adminClient.describeTopics(Collections.singleton(topic))
                    .allTopicNames()
                    .get()
                    .get(topic)
                    .partitions()
                    .forEach(p -> partitions.add(new TopicPartition(topic, p.partition())));

            // 2. Build request for offsets at timestamp
            Map<TopicPartition, OffsetSpec> request = new HashMap<>();
            partitions.forEach(tp -> request.put(tp, OffsetSpec.forTimestamp(timestamp)));

            // 3. Fetch offsets
            ListOffsetsResult offsetsResult = adminClient.listOffsets(request);
            Map<TopicPartition, ListOffsetsResultInfo> offsets = offsetsResult.all().get();

            // 4. Prepare offsets for reset
            Map<TopicPartition, OffsetAndMetadata> newOffsets = new HashMap<>();
            offsets.forEach((tp, offsetInfo) -> {
                if (offsetInfo != null && offsetInfo.offset() >= 0) {
                    newOffsets.put(tp, new OffsetAndMetadata(offsetInfo.offset()));
                    System.out.printf("Resetting partition %s to offset %d (timestamp=%d)%n",
                            tp, offsetInfo.offset(), offsetInfo.timestamp());
                } else {
                    System.out.printf("Skipping partition %s (no valid offset found for timestamp)%n", tp);
                }
            });

            // 5. Apply reset
            if (!newOffsets.isEmpty()) {
                AlterConsumerGroupOffsetsResult result =
                        adminClient.alterConsumerGroupOffsets(consumerGroup, newOffsets);
                result.all().get();
                System.out.println("✅ Offsets successfully reset to 2 hours ago for group: " + consumerGroup);
            } else {
                System.out.println("⚠️ No offsets to reset for group: " + consumerGroup);
            }
        }
    }
}
