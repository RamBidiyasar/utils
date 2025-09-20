package in.wynk.secret.manager.utils.kafka;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
public class KafkaTimeWindowRepublisher {

    public static void main(String[] args) throws Exception {
        String bootstrapServers = "10.161.24.22:9092,10.161.24.24:9092,10.161.24.23:9092";
        String topic = "xstream-wcf-events";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";
        String consumerGroup = "xstream-2";
      Set<String> UIDS = Set.of("0bh9bxrcFvkJtkMuk0");


        // --- Time window we want to extract from the log (today 14:00 - 14:30) ---
        LocalDate yesterday = LocalDate.now().minusDays(1); // change zone if needed
        ZonedDateTime startTime = yesterday.atTime(14, 0).atZone(ZoneId.systemDefault());
        ZonedDateTime endTime   = yesterday.atTime(14, 30).atZone(ZoneId.systemDefault());

        long startTimestamp = startTime.toInstant().toEpochMilli();
        long endTimestamp   = endTime.toInstant().toEpochMilli();

        System.out.printf("Target time window: %s -> %s (ms: %d -> %d)%n",
                startTime, endTime, startTimestamp, endTimestamp);

        // --- common SASL config ---
        Properties common = new Properties();
        common.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        common.put("security.protocol", "SASL_PLAINTEXT");
        common.put("sasl.mechanism", "PLAIN");
        common.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"" + username + "\" password=\"" + password + "\";");

        // consumer props
        Properties consumerProps = new Properties();
        consumerProps.putAll(common);
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // producer props
        Properties producerProps = new Properties();
        producerProps.putAll(common);
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (AdminClient admin = AdminClient.create(common);
             KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
             KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {

            // --- find partitions for topic ---
            TopicDescription td = admin.describeTopics(Collections.singletonList(topic)).all().get().get(topic);
            List<TopicPartition> partitions = td.partitions().stream()
                    .map(p -> new TopicPartition(topic, p.partition()))
                    .toList();
          //  System.out.println("Partitions: " + partitions);

            // --- compute start offsets (by timestamp) ---
            Map<TopicPartition, OffsetSpec> startReq = new HashMap<>();
            Map<TopicPartition, OffsetSpec> endReq   = new HashMap<>();
            for (TopicPartition tp : partitions) {
                startReq.put(tp, OffsetSpec.forTimestamp(startTimestamp));
                endReq.put(tp,   OffsetSpec.forTimestamp(endTimestamp));
            }

            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> startInfos =
                    admin.listOffsets(startReq).all().get();
            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> endInfos =
                    admin.listOffsets(endReq).all().get();

            // --- determine startOffset and endOffsetExclusive per partition ---
            Map<TopicPartition, Long> startOffsets = new HashMap<>();
            Map<TopicPartition, Long> endOffsetsExclusive = new HashMap<>();
            for (TopicPartition tp : partitions) {
                ListOffsetsResult.ListOffsetsResultInfo sInfo = startInfos.get(tp);
                ListOffsetsResult.ListOffsetsResultInfo eInfo = endInfos.get(tp);

                long startOffset;
                if (sInfo != null && sInfo.offset() >= 0) {
                    startOffset = sInfo.offset();
                } else {
                    // fallback to earliest
                    startOffset = admin.listOffsets(Collections.singletonMap(tp, OffsetSpec.earliest()))
                            .all().get().get(tp).offset();
                    System.out.printf("No start offset for %s at %d; falling back to earliest=%d%n",
                            tp, startTimestamp, startOffset);
                }

                long endExclusive;
                if (eInfo != null && eInfo.offset() >= 0) {
                    // listOffsets(forTimestamp(end)) returns first offset whose timestamp >= endTimestamp.
                    // treat that as exclusive boundary (we will NOT include that offset).
                    endExclusive = eInfo.offset();
                } else {
                    // fallback to latest (consume up to current log end)
                    endExclusive = admin.listOffsets(Collections.singletonMap(tp, OffsetSpec.latest()))
                            .all().get().get(tp).offset();
                    System.out.printf("No end offset for %s at %d; falling back to latest=%d%n",
                            tp, endTimestamp, endExclusive);
                }

                startOffsets.put(tp, startOffset);
                endOffsetsExclusive.put(tp, endExclusive);

                System.out.printf("Partition %d: start=%d endExclusive=%d%n",
                        tp.partition(), startOffset, endExclusive);
            }

            // --- remove partitions with nothing to read (start >= endExclusive) ---
            Set<TopicPartition> toRead = new HashSet<>();
            for (TopicPartition tp : partitions) {
                if (startOffsets.get(tp) < endOffsetsExclusive.get(tp)) {
                    toRead.add(tp);
                } else {
                    System.out.printf("Partition %d has no records in window (start >= endExclusive).%n", tp.partition());
                }
            }

            if (toRead.isEmpty()) {
                System.out.println("No partitions have messages in the requested time window. Exiting.");
                return;
            }

            // assign and seek to each partition's start offset
            consumer.assign(new ArrayList<>(toRead));
            for (TopicPartition tp : toRead) {
                consumer.seek(tp, startOffsets.get(tp));
            }

            // track which partitions are finished
            Set<TopicPartition> remaining = new HashSet<>(toRead);
            long consumedCount = 0;
            long republishedCount = 0;

            System.out.println("Begin consuming historical window (reading until partition end offsets are reached).");

            while (!remaining.isEmpty()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                if (records.isEmpty()) {
                    System.out.println("…poll returned 0 records…");
                }

                for (ConsumerRecord<String, String> r : records) {
                    TopicPartition tp = new TopicPartition(r.topic(), r.partition());
                    long endExclusive = endOffsetsExclusive.get(tp);

                    // If we've reached or passed the exclusive boundary, mark finished for this partition
                    if (r.offset() >= endExclusive) {
                        remaining.remove(tp);
                        System.out.printf("Reached endExclusive for partition=%d (offset %d >= %d). Marking finished.%n",
                                tp.partition(), r.offset(), endExclusive);
                        continue;
                    }

                    // If the record timestamp is beyond the requested endTimestamp, mark finished for this partition.
                    if (r.timestamp() > endTimestamp) {
                        remaining.remove(tp);
                        System.out.printf("Record timestamp %d > endTimestamp for partition=%d; marking finished.%n",
                                r.timestamp(), tp.partition());
                        continue;
                    }

                    consumedCount++;
                    System.out.printf("Consumed partition=%d offset=%d ts=%s key=%s%n",
                            r.partition(), r.offset(), Instant.ofEpochMilli(r.timestamp()).atZone(ZoneId.systemDefault()), r.key());

                    String payload = r.value();
                    if (payload != null) {
                        // simple substring match; replace with JSON parse if needed for exact matching
                        for (String uid : UIDS) {
                            if (payload.contains(uid)) {
                                ProducerRecord<String, String> pr = new ProducerRecord<>(topic, r.key(), payload);
                                producer.send(pr, (meta, ex) -> {
                                    if (ex == null) {
                                        // success
                                    } else {
                                        System.err.printf("Failed to republish offset=%d partition=%d: %s%n", r.offset(), r.partition(), ex.getMessage());
                                    }
                                });
                                republishedCount++;
                                System.out.printf("🔁 Matched UID '%s' -> republished offset=%d partition=%d%n", uid, r.offset(), r.partition());
                                break; // matched one uid is enough
                            }
                        }
                    }
                }

                // After processing records, also check positions: if position >= endExclusive, mark finished
                for (TopicPartition tp : new ArrayList<>(remaining)) {
                    long pos = consumer.position(tp);
                    long endExclusive = endOffsetsExclusive.get(tp);
                    if (pos >= endExclusive) {
                        remaining.remove(tp);
                        System.out.printf("Position for partition %d reached endExclusive (%d >= %d). Marking finished.%n",
                                tp.partition(), pos, endExclusive);
                    }
                }
            }

            System.out.printf("Done. Consumed %d records. Republished %d messages.%n", consumedCount, republishedCount);
        }
    }
}
