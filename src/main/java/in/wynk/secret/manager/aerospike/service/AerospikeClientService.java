package in.wynk.secret.manager.aerospike.service;

import com.aerospike.client.Record;
import in.wynk.secret.manager.aerospike.config.AerospikeConfig;
import in.wynk.secret.manager.aerospike.enums.AerospikeEnvironment;
import in.wynk.secret.manager.aerospike.repository.AerospikeRepository;
import in.wynk.secret.manager.aerospike.dto.AerospikeRequest;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AerospikeClientService {

    private AerospikeRepository getRepo(AerospikeEnvironment env) {
        return new AerospikeRepository(AerospikeConfig.getClient(env));
    }

    public void save(AerospikeRequest req) {
        getRepo(req.getEnv()).saveRecord(req.getNamespace(), req.getSet(), req.getKey(), req.getData());
    }

    public Record fetch(AerospikeRequest req) {
        return getRepo(req.getEnv()).getRecord(req.getNamespace(), req.getSet(), req.getKey());
    }

    public Map<String, Object> fetchAll(AerospikeRequest req) {
        return getRepo(req.getEnv()).scanSet(req.getNamespace(), req.getSet(), k -> true);
    }

    public boolean delete(AerospikeRequest request) {
        return getRepo(request.getEnv()).deleteRecord(request.getNamespace(), request.getSet(), request.getKey());
    }

    public Map<String, Object> fetchByPrefix(AerospikeRequest req) {
       return getRepo(req.getEnv()).scanSet(req.getNamespace(), req.getSet(),
                key -> key.userKey != null && key.userKey.toString().startsWith(req.getPrefix()));
    }

    public int count(AerospikeRequest req) {
       return getRepo(req.getEnv()).countInSet(req.getNamespace(), req.getSet());
    }
}
