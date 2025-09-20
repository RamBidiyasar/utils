package in.wynk.secret.manager.aerospike.service;

import com.aerospike.client.Record;
import in.wynk.secret.manager.aerospike.config.AerospikeConfig;
import in.wynk.secret.manager.aerospike.dto.response.PaginatedResponse;
import in.wynk.secret.manager.aerospike.dto.response.StatsResponse;
import in.wynk.secret.manager.aerospike.enums.AerospikeEnvironment;
import in.wynk.secret.manager.aerospike.repository.AerospikeRepository;
import in.wynk.secret.manager.aerospike.dto.request.AerospikeRequest;
import in.wynk.secret.manager.aerospike.utils.RecordsUtils;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AerospikeClientService {

    private AerospikeRepository getRepo(AerospikeEnvironment env) {
        return new AerospikeRepository(AerospikeConfig.getClient(env));
    }

    public void save(AerospikeRequest req) {
        getRepo(req.getEnv()).saveRecord(req.getNamespace(), req.getSet(), req.getKey(), req.getData());
    }

    public PaginatedResponse fetch(AerospikeRequest req) {
        Record record = getRepo(req.getEnv()).getRecord(req.getNamespace(), req.getSet(), req.getKey());

        if (record == null) {
            return PaginatedResponse.of(Map.of(), 0,0,0);
        } else {
            return PaginatedResponse.of(Map.of(req.getKey(), RecordsUtils.toMap(record)), 1, 1, 1);
        }
    }

    public PaginatedResponse fetchAll(AerospikeRequest req) {
        int pageSize = 10; // You can make this configurable
        return getRepo(req.getEnv()).scanSetPaginated(req.getNamespace(), req.getSet(), req.getPage(), pageSize);
    }

    public StatsResponse getStats(AerospikeRequest req) {
        return getRepo(req.getEnv()).getSetStatistics(req.getNamespace(), req.getSet());
    }

    public boolean delete(AerospikeRequest request) {
        return getRepo(request.getEnv()).deleteRecord(request.getNamespace(), request.getSet(), request.getKey());
    }

    public PaginatedResponse fetchByPrefix(AerospikeRequest req) {
        Map<String, Object> recordsData =  getRepo(req.getEnv()).scanSet(req.getNamespace(), req.getSet(),
                                             key -> key.userKey != null && key.userKey.toString().matches(req.getPrefix()));

        return getPaginatedResponse(req, recordsData);
    }

    public int count(AerospikeRequest req) {
        return getRepo(req.getEnv()).countInSet(req.getNamespace(), req.getSet());
    }

    public PaginatedResponse fetchBYSuffix(final AerospikeRequest req) {
        Map<String, Object> recordsData =  getRepo(req.getEnv()).scanSet(req.getNamespace(), req.getSet(),
                                                                         key -> key.userKey != null && key.userKey.toString().endsWith(req.getPrefix()));

        return getPaginatedResponse(req, recordsData);
    }

    private PaginatedResponse getPaginatedResponse(final AerospikeRequest req, final Map<String, Object> recordsData) {
        int totalRecords = recordsData.size();
        int pageSize = 10; // You can make this configurable
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        int fromIndex = (req.getPage() - 1) * pageSize;
        Map<String, Object> paginatedData = recordsData.entrySet().stream()
                                                       .skip(fromIndex)
                                                       .limit(pageSize)
                                                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return PaginatedResponse.of(paginatedData, req.getPage(), totalPages, totalRecords);
    }
}
