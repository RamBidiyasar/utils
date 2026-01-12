package in.wynk.secret.manager.utils.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class KafkaEventTypeStats {

    private static final String TOPIC = "atv-events-prod-iptv";
    
    // Configurable lookback duration (e.g., 1 hour)
    private static final Duration LOOKBACK = Duration.ofMinutes(5);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter HOURLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00");
    private static final DateTimeFormatter DAILY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Thread Pool Size
    private static final int THREAD_COUNT = 4;
    private static final long MAX_TOTAL_EVENTS = 10000;

    public static void main(String[] args) {
        String bootstrap = "10.161.24.16:9092,10.161.24.17:9092,10.161.24.18:9092";
        String username = "appuser";
        String password = "uJK67AUDI1Ax";

        // 1. Calculate Time Window
        long now = System.currentTimeMillis();
        long startTime = now - LOOKBACK.toMillis();
        long endTime = now;

        System.out.printf("Time Window: %s to %s%n", 
            Instant.ofEpochMilli(startTime).atZone(IST), 
            Instant.ofEpochMilli(endTime).atZone(IST));

        Properties common = new Properties();
        common.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        common.put("security.protocol", "SASL_PLAINTEXT");
        common.put("sasl.mechanism", "PLAIN");
        common.put("sasl.jaas.config",
                   "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                   "username=\"" + username + "\" password=\"" + password + "\";");
        
        common.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "60000");
        common.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "60000");

        // Base Consumer Properties
        Properties baseProps = new Properties();
        baseProps.putAll(common);
        baseProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        baseProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        baseProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        baseProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        baseProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "none"); 
        baseProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "45000"); 
        baseProps.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "60000");
        baseProps.put(ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "60000");

        // Small batch configuration for controlled fetching
        baseProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "50");
        baseProps.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "524288"); // 0.5 MB
        baseProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "524288"); // 0.5 MB per partition

        // Thread-safe stats containers
        ConcurrentHashMap<String, AtomicLong> eventCounts = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, AtomicLong> didCounts = new ConcurrentHashMap<>();
        ConcurrentSkipListMap<String, AtomicLong> timeCounts = new ConcurrentSkipListMap<>();
        ConcurrentSkipListMap<String, AtomicLong> hourlyCounts = new ConcurrentSkipListMap<>();
        ConcurrentSkipListMap<String, AtomicLong> dailyCounts = new ConcurrentSkipListMap<>();
        AtomicLong totalEvents = new AtomicLong(0);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        try (AdminClient admin = AdminClient.create(common)) {

            // 2. Get Partitions
            System.out.println("Connecting to AdminClient to describe topics...");
            TopicDescription desc = admin.describeTopics(List.of(TOPIC)).topicNameValues().get(TOPIC).get();
            List<TopicPartition> partitions = desc.partitions() 
                                                  .stream() 
                                                  .map(p -> new TopicPartition(TOPIC, p.partition())).collect(Collectors.toUnmodifiableList());


            // 3. Look up Offsets for Time Window
            System.out.println("Fetching offsets for time window...");
            Map<TopicPartition, OffsetSpec> reqStart = new HashMap<>();
            Map<TopicPartition, OffsetSpec> reqEnd = new HashMap<>();
            
            for (TopicPartition tp : partitions) {
                reqStart.put(tp, OffsetSpec.forTimestamp(startTime));
                reqEnd.put(tp, OffsetSpec.forTimestamp(endTime));
            }

            var startInfo = admin.listOffsets(reqStart).all().get();
            var endInfo = admin.listOffsets(reqEnd).all().get();

            Map<TopicPartition, Long> startOffsets = new HashMap<>();
            Map<TopicPartition, Long> endOffsets = new HashMap<>();
            List<TopicPartition> activePartitions = new ArrayList<>();

            for (TopicPartition tp : partitions) {
                ListOffsetsResult.ListOffsetsResultInfo startRes = startInfo.get(tp);
                ListOffsetsResult.ListOffsetsResultInfo endRes = endInfo.get(tp);

                long s = (startRes != null && startRes.offset() != -1) ? startRes.offset() : -1;
                long e = (endRes != null && endRes.offset() != -1) ? endRes.offset() : -1;
                
                if (s != -1) {
                    if (e == -1 || e < s) {
                         e = Long.MAX_VALUE;
                    }

                    if (s < e) {
                        startOffsets.put(tp, s);
                        endOffsets.put(tp, e);
                        activePartitions.add(tp);
                    }
                }
            }

            if (activePartitions.isEmpty()) {
                System.out.println("No data found in the given time window.");
                return;
            }

            long totalExpected = 0;
            for (TopicPartition tp : activePartitions) {
                long s = startOffsets.get(tp);
                long e = endOffsets.get(tp);
                if (e != Long.MAX_VALUE) {
                    totalExpected += (e - s);
                }
            }
            System.out.println("Total estimated messages to read: " + (totalExpected == 0 ? "Unknown (Reading until latest)" : totalExpected));

            // 4. Distribute Partitions to Threads
            int partitionCount = activePartitions.size();
            System.out.println("Distributing " + partitionCount + " partitions across " + THREAD_COUNT + " threads.");
            
            List<Future<?>> futures = new ArrayList<>();
            
            List<List<TopicPartition>> partitionsPerThread = new ArrayList<>();
            for (int i = 0; i < THREAD_COUNT; i++) partitionsPerThread.add(new ArrayList<>());
            
            for (int i = 0; i < partitionCount; i++) {
                partitionsPerThread.get(i % THREAD_COUNT).add(activePartitions.get(i));
            }

            for (int i = 0; i < THREAD_COUNT; i++) {
                List<TopicPartition> threadPartitions = partitionsPerThread.get(i);
                if (threadPartitions.isEmpty()) continue;

                int threadId = i + 1;
                futures.add(executor.submit(() -> {
                    processPartitions(threadId, threadPartitions, baseProps, startOffsets, endOffsets, endTime, eventCounts, didCounts, timeCounts, hourlyCounts, dailyCounts, totalEvents);
                }));
            }

            for (Future<?> f : futures) {
                try {
                    f.get();
                } catch (ExecutionException e) {
                    System.err.println("Worker thread failed: " + e.getCause().getMessage());
                    e.printStackTrace();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        // 5. Report
        long total = totalEvents.get();
        
        System.out.println("\n[REPORT] Event Type Statistics (Last " + LOOKBACK.toHours() + " hours)");
        System.out.println("------------------------------------------------");
        System.out.printf("%-30s | %-10s | %-10s%n", "Event Type", "Count", "Percentage");
        System.out.println("------------------------------------------------");

        if (total == 0) {
            System.out.println("No events found.");
        } else {
            eventCounts.entrySet().stream() 
                .sorted((a, b) -> Long.compare(b.getValue().get(), a.getValue().get())) // Descending
                .forEach(entry -> { 
                    String type = entry.getKey();
                    long count = entry.getValue().get();
                    double percent = (count * 100.0) / total;
                    System.out.printf("%-30s | %-10d | %6.2f%%%n", type, count, percent);
                });
            System.out.println("------------------------------------------------");
            System.out.println("Total Events: " + total);
        }

        // DID Report
        System.out.println("\n[REPORT] DID Statistics (Top 20)");
        System.out.println("Total Unique DIDs: " + didCounts.size());
        System.out.println("------------------------------------------------");
        System.out.printf("%-30s | %-10s | %-10s%n", "DID", "Count", "Percentage");
        System.out.println("------------------------------------------------");

        if (didCounts.isEmpty()) {
            System.out.println("No DID data found.");
        } else {
            didCounts.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue().get(), a.getValue().get())) // Descending
                .limit(20)
                .forEach(entry -> {
                    String did = entry.getKey();
                    long count = entry.getValue().get();
                    double percent = (total > 0) ? (count * 100.0) / total : 0.0;
                    System.out.printf("%-30s | %-10d | %6.2f%%%n", did, count, percent);
                });
            System.out.println("------------------------------------------------");
        }

//        // Time Series Report (Minute-wise)
//        System.out.println("\n[REPORT] Events by Time (Minute-wise)");
//        System.out.println("------------------------------------------------");
//        System.out.printf("%-20s | %-10s | %-10s%n", "Time (IST)", "Count", "Percentage");
//        System.out.println("------------------------------------------------");
//
//        if (timeCounts.isEmpty()) {
//             System.out.println("No time data available.");
//        } else {
//            timeCounts.forEach((time, count) -> {
//                long c = count.get();
//                double p = (total > 0) ? (c * 100.0) / total : 0.0;
//                System.out.printf("%-20s | %-10d | %6.2f%%%n", time, c, p);
//            });
//            System.out.println("------------------------------------------------");
//        }

        // Time Series Report (Hourly)
        System.out.println("\n[REPORT] Events by Time (Hourly)");
        System.out.println("------------------------------------------------");
        System.out.printf("%-20s | %-10s | %-10s%n", "Time (IST)", "Count", "Percentage");
        System.out.println("------------------------------------------------");
        
        if (hourlyCounts.isEmpty()) {
             System.out.println("No hourly data available.");
        } else {
            hourlyCounts.forEach((time, count) -> {
                long c = count.get();
                double p = (total > 0) ? (c * 100.0) / total : 0.0;
                System.out.printf("%-20s | %-10d | %6.2f%%%n", time, c, p);
            });
            System.out.println("------------------------------------------------");
        }

        // Time Series Report (Daily)
        System.out.println("\n[REPORT] Events by Time (Daily)");
        System.out.println("------------------------------------------------");
        
        if (dailyCounts.isEmpty()) {
             System.out.println("No daily data available.");
        } else {
            dailyCounts.forEach((time, count) -> {
                long c = count.get();
                double p = (total > 0) ? (c * 100.0) / total : 0.0;
                System.out.printf("%-20s | %-10d | %6.2f%%%n", time, c, p);
            });
            System.out.println("------------------------------------------------");
        }
    }

    private static void processPartitions(int threadId, 
                                          List<TopicPartition> partitions, 
                                          Properties baseProps, 
                                          Map<TopicPartition, Long> startOffsets, 
                                          Map<TopicPartition, Long> endOffsets, 
                                          long endTime,
                                          ConcurrentHashMap<String, AtomicLong> counts,
                                          ConcurrentHashMap<String, AtomicLong> didCounts,
                                          ConcurrentSkipListMap<String, AtomicLong> timeCounts,
                                          ConcurrentSkipListMap<String, AtomicLong> hourlyCounts,
                                          ConcurrentSkipListMap<String, AtomicLong> dailyCounts,
                                          AtomicLong total) {
        
        Properties props = new Properties();
        props.putAll(baseProps);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "stats-worker-" + threadId + "-" + UUID.randomUUID());
        
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.assign(partitions);
            for (TopicPartition tp : partitions) {
                consumer.seek(tp, startOffsets.get(tp));
            }
            
            Set<TopicPartition> remaining = new HashSet<>(partitions);
            int emptyPolls = 0;
            final int MAX_EMPTY_POLLS = 30; 

            System.out.println("[Thread-" + threadId + "] Started consuming " + partitions.size() + " partitions.");

            while (!remaining.isEmpty()) {
                if (total.get() >= MAX_TOTAL_EVENTS) {
                    System.out.println("[Thread-" + threadId + "] Global limit of " + MAX_TOTAL_EVENTS + " events reached. Stopping.");
                    break;
                }

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                if (records.isEmpty()) {
                    emptyPolls++;
                    if (emptyPolls >= MAX_EMPTY_POLLS) {
                        System.out.println("[Thread-" + threadId + "] Idle timeout. Stopping.");
                        break;
                    }
                } else {
                    emptyPolls = 0;
                    System.out.println("[Thread-" + threadId + "] Fetched " + records.count() + " records.");
                }

                for (ConsumerRecord<String, String> rec : records) {
                    TopicPartition tp = new TopicPartition(rec.topic(), rec.partition());
                    if (!remaining.contains(tp)) continue;

                    long endOff = endOffsets.getOrDefault(tp, Long.MAX_VALUE);
                    
                    if (rec.offset() >= endOff || rec.timestamp() > endTime) {
                        remaining.remove(tp);
                        continue; 
                    }
                    
                    if (rec.offset() < endOff && rec.timestamp() <= endTime) {
                         processRecord(rec.value(), counts, didCounts, timeCounts, hourlyCounts, dailyCounts, total);
                    }
                }
            }
            System.out.println("[Thread-" + threadId + "] Finished.");
            
        } catch (Exception e) {
            System.err.println("[Thread-" + threadId + "] Error: " + e.getMessage());
        }
    }

    private static void processRecord(String json, 
                                      ConcurrentHashMap<String, AtomicLong> counts,
                                      ConcurrentHashMap<String, AtomicLong> didCounts,
                                      ConcurrentSkipListMap<String, AtomicLong> timeCounts,
                                      ConcurrentSkipListMap<String, AtomicLong> hourlyCounts,
                                      ConcurrentSkipListMap<String, AtomicLong> dailyCounts,
                                      AtomicLong total) {
        try {
            JsonNode root = MAPPER.readTree(json);
            if (root.has("events")) {
                JsonNode eventsArray = root.get("events");
                if (eventsArray.isArray()) {
                    for (JsonNode event : eventsArray) {
                        if (event.has("event_type")) {
                            String type = event.get("event_type").asText("UNKNOWN");
                            counts.computeIfAbsent(type, k -> new AtomicLong(0)).incrementAndGet();
                            
                            String did = event.has("did") ? event.get("did").asText("UNKNOWN") : "UNKNOWN";
                            didCounts.computeIfAbsent(did, k -> new AtomicLong(0)).incrementAndGet();

                            // Time Aggregation
                            long ts = event.has("ts") ? event.get("ts").asLong() : System.currentTimeMillis();
                            timeCounts.computeIfAbsent(formatTime(ts), k -> new AtomicLong(0)).incrementAndGet();
                            hourlyCounts.computeIfAbsent(formatHour(ts), k -> new AtomicLong(0)).incrementAndGet();
                            dailyCounts.computeIfAbsent(formatDay(ts), k -> new AtomicLong(0)).incrementAndGet();

                            total.incrementAndGet();
                        }
                    }
                }
            } else if (root.has("event_type")) {
                String type = root.get("event_type").asText("UNKNOWN");
                counts.computeIfAbsent(type, k -> new AtomicLong(0)).incrementAndGet();

                String did = root.has("did") ? root.get("did").asText("UNKNOWN") : "UNKNOWN";
                didCounts.computeIfAbsent(did, k -> new AtomicLong(0)).incrementAndGet();
                
                long ts = root.has("ts") ? root.get("ts").asLong() : System.currentTimeMillis();
                timeCounts.computeIfAbsent(formatTime(ts), k -> new AtomicLong(0)).incrementAndGet();
                hourlyCounts.computeIfAbsent(formatHour(ts), k -> new AtomicLong(0)).incrementAndGet();
                dailyCounts.computeIfAbsent(formatDay(ts), k -> new AtomicLong(0)).incrementAndGet();

                total.incrementAndGet();
            }
        } catch (Exception e) {
            // System.err.println("JSON Parsing Error: " + e.getMessage());
        }
    }

    private static String formatTime(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), IST).format(TIME_FORMATTER);
    }
    
    private static String formatHour(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), IST).format(HOURLY_FORMATTER);
    }

    private static String formatDay(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), IST).format(DAILY_FORMATTER);
    }
}
