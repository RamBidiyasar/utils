package in.wynk.secret.manager.aerospike.service;

import com.aerospike.client.Record;

import in.wynk.secret.manager.aerospike.enums.AerospikeEnvironment;
import in.wynk.secret.manager.aerospike.repository.AerospikeRepository;
import in.wynk.secret.manager.aerospike.config.AerospikeConfig;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AerospikeService {

    private final AerospikeRepository repository;

    public AerospikeService(AerospikeEnvironment environment) {
        this.repository = new AerospikeRepository(AerospikeConfig.getClient(environment));
    }

    public void save(String namespace, String set, String key, Map<String, Object> data) {
        repository.saveRecord(namespace, set, key, data);
    }

    public void fetch(String namespace, String set, String key) {
        Record record = repository.getRecord(namespace, set, key);
        if (record != null) {
            System.out.println("📄 Record found: " + record.bins);
        } else {
            System.out.println("❌ No record found for key: " + key);
        }
    }

    public void fetchAll(String namespace, String set) {
        System.out.println("📂 Scanning all records in " + namespace + "." + set);
        repository.scanSet(namespace, set, k -> true);
    }

    public void findAllKeys(String namespace, String set) {
        repository.scanSetForKeysOnly(namespace, set, k -> true);
    }

    public void fetchByKeyPrefix(String namespace, String set, String prefix) {
        System.out.println("🔍 Searching for keys starting with: " + prefix);
        repository.scanSet(namespace, set, key ->
            key.userKey != null && key.userKey.toString().startsWith(prefix)
        );
    }


    public void countInSet(final String namespace, final String set) {
        System.out.println("📊 Counting records in: " + namespace + "." + set);

        repository.countInSet(namespace, set);
    }


    public boolean exists(String namespace, String set, String key) {
        return repository.exists(namespace, set, key);
    }

    public boolean delete(String namespace, String set, String key) {
        return repository.deleteRecord(namespace, set, key);
    }

    public void printSetOverview(String namespace, String set) {
        System.out.println("🧾 Full set dump of: " + namespace + "." + set);
        repository.printSetOverview(namespace, set);
    }

    public List<String> getAllKeys(String namespace, String set) {
        return repository.getAllKeys(namespace, set);
    }

    public Map<String, Object> getBins(String namespace, String set, String key) {
        return repository.getRecordBins(namespace, set, key);
    }

    public void scanAndConsume(String namespace, String set, Consumer<Record> consumer) {
        repository.scanAndConsume(namespace, set, consumer);
    }

    public void fetchWithMeta(String namespace, String set, String key) {
        Record record = repository.getRecordWithMeta(namespace, set, key);
        if (record != null) {
            System.out.println("📄 Record: " + record.bins + " | Gen: " + record.generation + ", TTL: " + record.getTimeToLive());
        } else {
            System.out.println("❌ Record not found: " + key);
        }
    }

}
