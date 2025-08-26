package in.wynk.secret.manager.aerospike.config;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;

import in.wynk.secret.manager.aerospike.enums.AerospikeEnvironment;
import java.util.HashMap;
import java.util.Map;

public class AerospikeConfig {

    private static final Map<AerospikeEnvironment, AerospikeClient> clientMap = new HashMap<>();

    public static AerospikeClient getClient(AerospikeEnvironment env) {
        if (clientMap.containsKey(env) && clientMap.get(env).isConnected()) {
            return clientMap.get(env);
        }

        ClientPolicy policy = new ClientPolicy();
        Host[] hosts;
        switch (env) {
            case PROD:
                policy.user = "appuser";
                policy.password = "5T85qV^396T}";
                hosts = new Host[]{
                    new Host("10.161.24.82", 3000),
                    new Host("10.161.24.83", 3000),
                    new Host("10.161.24.82", 3000)
                };
                break;
            case PREPROD:
                policy.user = "appuser";
                policy.password = "m}0\"Uiu27`zX";
                hosts = new Host[]{
                    new Host(""
                             + ""
                             + "10.249.218.92", 3000),
                    new Host("10.249.218.93", 3000),
                    new Host("10.249.218.94", 3000)
                };
                break;
            default:
                throw new IllegalArgumentException("Unsupported environment: " + env);
        }

        AerospikeClient client = new AerospikeClient(policy, hosts);
        clientMap.put(env, client);
        return client;
    }
}
