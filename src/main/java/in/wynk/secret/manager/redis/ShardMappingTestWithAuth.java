package in.wynk.secret.manager.redis;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;

import java.util.ArrayList;
import java.util.List;

public class ShardMappingTestWithAuth {

    public static void main(String[] args) {
        String password = "MysTr0ngP@ssw0rd@123";



        // Old Redis nodes (same order, with password)
        List<JedisShardInfo> oldShards = new ArrayList<>();
        JedisShardInfo shard1 = new JedisShardInfo("10.161.24.25", 6379);
        shard1.setPassword(password);
        oldShards.add(shard1);

        JedisShardInfo shard2 = new JedisShardInfo("10.161.24.26", 6379);
        shard2.setPassword(password);
        oldShards.add(shard2);

        JedisShardInfo shard3 = new JedisShardInfo("10.161.24.27", 6379);
        shard3.setPassword(password);
        oldShards.add(shard3);

        // New Redis nodes (same order, with password)
        List<JedisShardInfo> newShards = new ArrayList<>();
        JedisShardInfo newShard1 = new JedisShardInfo("10.161.24.28", 6379);
        newShard1.setPassword(password);
        newShards.add(newShard1);

        JedisShardInfo newShard2 = new JedisShardInfo("10.161.24.29", 6379);
        newShard2.setPassword(password);
        newShards.add(newShard2);

        JedisShardInfo newShard3 = new JedisShardInfo("10.161.24.30", 6379);
        newShard3.setPassword(password);
        newShards.add(newShard3);

        // Create ShardedJedis clients (only for mapping test, no real writes)
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
