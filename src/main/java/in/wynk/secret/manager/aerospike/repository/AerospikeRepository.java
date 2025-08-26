package in.wynk.secret.manager.aerospike.repository;

import com.aerospike.client.*;
import com.aerospike.client.Record;
import com.aerospike.client.cluster.Node;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;

import in.wynk.secret.manager.aerospike.dto.response.PaginatedResponse;
import in.wynk.secret.manager.aerospike.dto.response.StatsResponse;
import in.wynk.secret.manager.aerospike.utils.RecordsUtils;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.elasticsearch.common.Priority;

public class AerospikeRepository {

    private final AerospikeClient client;

    public AerospikeRepository(AerospikeClient client) {
        this.client = client;
    }

    public void saveRecord(String namespace, String set, String keyStr, Map<String, Object> binData) {
        Key key = new Key(namespace, set, keyStr);
        Bin[] bins = binData.entrySet().stream()
                            .map(e -> new Bin(e.getKey(), String.valueOf(e.getValue())))
                            .toArray(Bin[]::new);
        client.put(new WritePolicy(), key, bins);
        System.out.println("✅ Record saved for key: " + keyStr);
    }

    public Record getRecord(String namespace, String set, String keyStr) {
        Key key = new Key(namespace, set, keyStr);
        return client.get(null, key);
    }

    public boolean exists(String namespace, String set, String keyStr) {
        Key key = new Key(namespace, set, keyStr);
        return client.exists(null, key);
    }

    public boolean deleteRecord(String namespace, String set, String keyStr) {
        Key key = new Key(namespace, set, keyStr);
        return client.delete(null, key);
    }

    public Map<String, Object> scanSet(String namespace, String set, Predicate<Key> keyFilter) {
        Map<String, Object> recordData = new HashMap<>();
        ScanPolicy scanPolicy = new ScanPolicy();
        scanPolicy.includeBinData = true;

        client.scanAll(scanPolicy, namespace, set, (key, record) -> {
            if (keyFilter.test(key)) {
                recordData.put(key.userKey.toString(), RecordsUtils.toMap(record));
            }
        });

        return recordData;
    }

    public void scanSetForKeysOnly(String namespace, String set, Predicate<Key> keyFilter) {
        ScanPolicy scanPolicy = new ScanPolicy();
        scanPolicy.includeBinData = true;

        client.scanAll(scanPolicy, namespace, set, (key, record) -> {
            if (keyFilter.test(key)) {
                System.out.println("Key: " + key.userKey);
            }
        });
    }

    public void scanAndConsume(String namespace, String set, Consumer<Record> recordConsumer) {
        ScanPolicy scanPolicy = new ScanPolicy();
        scanPolicy.includeBinData = true;

        client.scanAll(scanPolicy, namespace, set, (key, record) -> {
            recordConsumer.accept(record);
        });
    }

    public List<String> getAllKeys(String namespace, String set) {
        List<String> keys = new ArrayList<>();
        ScanPolicy scanPolicy = new ScanPolicy();
        scanPolicy.includeBinData = false;

        client.scanAll(scanPolicy, namespace, set, (key, record) -> {
            if (key.userKey != null) {
                keys.add(key.userKey.toString());
            }
        });

        return keys;
    }

    public Map<String, Object> getRecordBins(String namespace, String set, String keyStr) {
        Record record = getRecord(namespace, set, keyStr);
        return (record != null) ? record.bins : Collections.emptyMap();
    }

    public int countInSet(final String namespace, final String set) {
        ScanPolicy scanPolicy = new ScanPolicy();
        scanPolicy.includeBinData = false;

        AtomicInteger counter = new AtomicInteger(0);
        client.scanAll(scanPolicy, namespace, set, (Key key, Record record) -> {
            counter.incrementAndGet();
        });

        System.out.println("✅ Total records in " + namespace + "." + set + ": " + counter.get());
        return counter.get();
    }

    public Record getRecordWithMeta(String namespace, String set, String keyStr) {
        Key key = new Key(namespace, set, keyStr);
        Policy policy = new Policy();
        return client.get(policy, key);
    }

    public void printSetOverview(String namespace, String set) {
        scanSet(namespace, set, key -> true);
    }


    /**
     * Computes set statistics by scanning all records.
     * This is accurate but can be expensive for large sets.
     *
     * @param namespace Aerospike namespace
     * @param set       Aerospike set
     * @return StatsResponse containing recordCount, memoryUsedBytes, deviceUsedBytes (approx)
     */
    public StatsResponse getSetStatistics(String namespace, String set) {
        AtomicLong recordCount = new AtomicLong(0);
        AtomicLong memoryUsedBytes = new AtomicLong(0);

        try {
            ScanPolicy policy = new ScanPolicy();
            policy.includeBinData = true;       // fetch bins to estimate size
            policy.concurrentNodes = true;

            client.scanAll(policy, namespace, set, (Key key, Record record) -> {
                recordCount.incrementAndGet();
                memoryUsedBytes.addAndGet(RecordsUtils.getApproxSize(record));
            });

        } catch (Exception e) {
            System.err.printf("❌ Error scanning stats for %s.%s: %s%n", namespace, set, e.getMessage());
        }

        return StatsResponse.of(
            recordCount.get(),
            memoryUsedBytes.get(),
            memoryUsedBytes.get()
        );
    }




    /**
     * Scans a set and returns a paginated result, including the size of each record.
     */
    public PaginatedResponse scanSetPaginated(String namespace, String set, int page, int pageSize) {
        List<Map.Entry<String, Object>> allRecords = new ArrayList<>();
        ScanPolicy scanPolicy = new ScanPolicy();
        scanPolicy.includeBinData = true;

        client.scanAll(scanPolicy, namespace, set, (key, record) -> {
            Map<String, Object> recordData = RecordsUtils.toMap(record);
            System.out.println("Key: " + key.userKey + ", Size: " + RecordsUtils.getApproxSize(record) + " bytes");
            allRecords.add(new AbstractMap.SimpleEntry<>(key.userKey.toString(), recordData));
        });

        long totalRecords = allRecords.size();
        if (totalRecords == 0) {
            return PaginatedResponse.of(new HashMap<>(), 1, 0, 0);
        }

        long totalPages = (long) Math.ceil((double) totalRecords / pageSize);
        int startIndex = (page - 1) * pageSize;

        if (startIndex >= totalRecords) {
            return PaginatedResponse.of(new HashMap<>(), page, totalPages, totalRecords);
        }

        int endIndex = Math.min(startIndex + pageSize, (int) totalRecords);

        Map<String, Object> pageRecords = allRecords.subList(startIndex, endIndex)
                                                    .stream()
                                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));

        return PaginatedResponse.of(pageRecords, page, totalPages, totalRecords);
    }
}