package in.wynk.secret.manager.aerospike.dto;

import in.wynk.secret.manager.aerospike.enums.AerospikeEnvironment;
import java.util.Map;
import lombok.Data;

@Data
public class AerospikeRequest {
    private AerospikeEnvironment env;
    private String namespace;
    private String set;
    private String key;
    private Map<String, Object> data;
    private String prefix;
}
