package in.wynk.secret.manager.aerospike.repository;

import com.aerospike.client.*;
import com.aerospike.client.Record;
import com.aerospike.client.policy.Policy;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
                recordData.put(key.userKey.toString(), Map.of("values", record.bins
                ,"ttl", record.getTimeToLive()));
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
}
