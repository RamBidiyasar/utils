package in.wynk.secret.manager.aerospike.dto.request;

import in.wynk.secret.manager.aerospike.enums.AerospikeEnvironment;
import java.util.Map;

public class AerospikeRequest {
    private AerospikeEnvironment env;
    private String namespace;
    private String set;
    private String key;
    private Map<String, Object> data;
    private String prefix;
    private String suffix;
    private int page = 1;

    public AerospikeEnvironment getEnv() { return env; }
    public void setEnv(AerospikeEnvironment env) { this.env = env; }
    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }
    public String getSet() { return set; }
    public void setSet(String set) { this.set = set; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public String getSuffix() { return suffix; }
    public void setSuffix(String suffix) { this.suffix = suffix; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
}