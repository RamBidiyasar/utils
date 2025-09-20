package in.wynk.secret.manager.redis;


import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.ArrayList;
import java.util.List;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;

public class ShardMappingTest {

    public static void main(String[] args) {
        // Old Redis nodes (same order)
        List<JedisShardInfo> oldShards = new ArrayList<>();
        oldShards.add(new JedisShardInfo("10.0.0.1", 6379));
        oldShards.add(new JedisShardInfo("10.0.0.2", 6379));
        oldShards.add(new JedisShardInfo("10.0.0.3", 6379));

        // New Redis nodes (same order, different IPs)
        List<JedisShardInfo> newShards = new ArrayList<>();
        newShards.add(new JedisShardInfo("192.168.1.10", 6379));
        newShards.add(new JedisShardInfo("192.168.1.11", 6379));
        newShards.add(new JedisShardInfo("192.168.1.12", 6379));

        // Create pools (we won’t connect, just check shard selection)
        ShardedJedis oldClient = new ShardedJedis(oldShards);
        ShardedJedis newClient = new ShardedJedis(newShards);

        String[] keys = {"user:1", "user:2", "user:3", "session:100", "order:555"};

        for (String key : keys) {
            String oldHost = oldClient.getShardInfo(key).getHost();
            String newHost = newClient.getShardInfo(key).getHost();
            System.out.printf("Key: %-12s | Old -> %-15s | New -> %-15s | Same Slot? %s%n",
                    key, oldHost, newHost, oldHost.equals(newHost) ? "YES" : "NO");
        }

        oldClient.close();
        newClient.close();
    }
}
