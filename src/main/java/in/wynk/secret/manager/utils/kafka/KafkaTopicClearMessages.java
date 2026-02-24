package in.wynk.secret.manager.utils.kafka;

import java.util.*;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;

public class KafkaTopicClearMessages {

    public static void main(String[] args) {

//        String bootstrapServers = "10.161.24.16:9092,10.161.24.17:9092,10.161.24.18:9092";
//        String bootstrapServers = "10.161.24.77:9092";
//        String topicName = "wcf_prod.sms_highest_priority";
//        String username = "appuser";
//        String password = "uJK67AUDI1Ax";



//        String bootstrapServers = "10.161.24.77:9092";
//        String topicName = "atv-events-prod-iptv";
//        String username = "appuser";
//        String password = "uJK67AUDI1Ax";



        String bootstrapServers = "10.160.88.23:9092,10.160.88.24:9092,10.160.88.25:9092";
        String topicName = "wcf_prod.sms_highest_priority";
        String username = "appuser";
        String password = "uJK67dC1Ax";


//        String bootstrapServers = "10.169.24.13:9092";
//        String username = "appuser";
//        String password = "uJK67dC1Ax";
//        String topicName = "atv-content-streams";


        // Kafka AdminClient properties with security
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "PLAIN");
        properties.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                                           "username=\"" + username + "\" " +
                                           "password=\"" + password + "\";");

        try (AdminClient adminClient = AdminClient.create(properties)) {
            // Get partition information for the topic
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singletonList(topicName));
            TopicDescription topicDescription = describeTopicsResult.topicNameValues().get(topicName).get();

            Map<TopicPartition, RecordsToDelete> recordsToDelete = new HashMap<>();

            for (TopicPartitionInfo partition : topicDescription.partitions()) {
                TopicPartition topicPartition = new TopicPartition(topicName, partition.partition());

                // Find the latest offset (high watermark) for the partition
                ListOffsetsResult listOffsetsResult = adminClient.listOffsets(Collections.singletonMap(topicPartition, OffsetSpec.latest()));
                long endOffset = listOffsetsResult.partitionResult(topicPartition).get().offset();

                recordsToDelete.put(topicPartition, RecordsToDelete.beforeOffset(endOffset));
                System.out.println("Preparing to clear partition " + partition.partition() + " up to offset " + endOffset);
            }

            if (!recordsToDelete.isEmpty()) {
                adminClient.deleteRecords(recordsToDelete).all().get();
                System.out.println("Successfully cleared all messages for topic: " + topicName);
            } else {
                System.out.println("No partitions found for topic: " + topicName);
            }

        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error clearing messages for topic: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupt status
        }
    }
}