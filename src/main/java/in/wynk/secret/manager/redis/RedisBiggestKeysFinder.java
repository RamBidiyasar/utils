package in.wynk.secret.manager.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.*;

public class RedisBiggestKeysFinder {

    static class KeyInfo implements Comparable<KeyInfo> {
        String key;
        long size;
        String ip;

        public KeyInfo(String key, long size, String ip) {
            this.key = key;
            this.size = size;
            this.ip = ip;
        }

        @Override
        public int compareTo(KeyInfo other) {
            return Long.compare(other.size, this.size); // descending order
        }

        @Override
        public String toString() {
            return String.format("Key: %s, Size: %d bytes, IP: %s", key, size, ip);
        }
    }

    public static void main(String[] args) {
//        List<String> listIps = List.of("10.169.24.15", "10.169.24.20");

        List<String> listIps = List.of(
                "10.161.24.25", "10.161.24.26", "10.161.24.27", "10.161.24.28",
                "10.161.24.29", "10.161.24.30", "10.161.24.31", "10.161.24.32",
                "10.161.24.33", "10.161.24.34", "10.161.24.35", "10.161.24.36",
                "10.161.24.37", "10.161.24.38", "10.161.24.39", "10.161.24.40",
                "10.161.24.41", "10.161.24.42", "10.161.24.43", "10.161.24.44",
                "10.161.24.45", "10.161.24.46", "10.161.24.47", "10.161.24.48",
                "10.161.24.49"
        );
        String password = "MysTr0ngP@ssw0rd@123";
        String pattern = "*"; // scan all keys
        int scanCount = 50000;
        int topN = 100; // number of biggest keys to display

        PriorityQueue<KeyInfo> topKeys = new PriorityQueue<>();
        long totalKeysScanned = 0;

        for (String ip : listIps) {
            try (Jedis jedis = new Jedis(ip, 6379)) {
                jedis.auth(password);

                String cursor = ScanParams.SCAN_POINTER_START;
                ScanParams scanParams = new ScanParams().match(pattern).count(scanCount);

                System.out.printf("Scanning Redis at %s...%n", ip);

                do {
                    ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                    List<String> keys = scanResult.getResult();

                    for (String key : keys) {
                        try {
                            long size = getKeySize(jedis, key);
                            
                            // Only track keys with actual size
                            if (size > 0) {
                                topKeys.offer(new KeyInfo(key, size, ip));

                                // Keep only top N keys
                                if (topKeys.size() > topN) {
                                    topKeys.poll();
                                }
                            }

                            totalKeysScanned++;
                            if (totalKeysScanned % 10000 == 0) {
                                System.out.printf("Scanned %d keys so far...%n", totalKeysScanned);
                            }
                        } catch (Exception e) {
                            System.err.printf("Error processing key '%s': %s%n", key, e.getMessage());
                        }
                    }

                    cursor = scanResult.getCursor();
                } while (!cursor.equals(ScanParams.SCAN_POINTER_START));

                System.out.printf("Finished scanning Redis at %s%n", ip);

            } catch (Exception e) {
                System.err.printf("Error connecting to Redis at %s: %s%n", ip, e.getMessage());
            }
        }

        System.out.println("\n====================================================");
        System.out.printf("Total keys scanned: %d%n", totalKeysScanned);
        System.out.println("====================================================");
        System.out.printf("\nTop %d biggest keys:%n", topN);
        System.out.println("====================================================");

        // Convert to list and sort in descending order
        List<KeyInfo> sortedKeys = new ArrayList<>(topKeys);
        Collections.sort(sortedKeys);

        for (int i = 0; i < sortedKeys.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, sortedKeys.get(i));
        }
        System.out.println("====================================================");
    }

    private static long getKeySize(Jedis jedis, String key) {
        try {
            // First check if key exists
            if (!jedis.exists(key)) {
                return 0;
            }

            // Use MEMORY USAGE command (Redis 4.0+) - most accurate
            try {
                Long memoryUsage = jedis.memoryUsage(key);
                if (memoryUsage != null && memoryUsage > 0) {
                    return memoryUsage;
                }
            } catch (Exception e) {
                // MEMORY USAGE not available, fall back to manual calculation
            }

            // Fallback: calculate size based on type
            String type = jedis.type(key);

            switch (type) {
                case "string":
                    byte[] value = jedis.get(key.getBytes());
                    return value != null ? value.length : 0;

                case "list":
                    long listLen = jedis.llen(key);
                    if (listLen == 0) return 0;
                    long listSize = 0;
                    // Sample first 100 elements and extrapolate
                    long sampleSize = Math.min(listLen, 100);
                    for (long i = 0; i < sampleSize; i++) {
                        String item = jedis.lindex(key, i);
                        if (item != null) {
                            listSize += item.getBytes().length;
                        }
                    }
                    // Extrapolate to full list
                    return (listSize / sampleSize) * listLen;

                case "set":
                    long setSize = 0;
                    Set<String> members = jedis.smembers(key);
                    for (String member : members) {
                        if (member != null) {
                            setSize += member.getBytes().length;
                        }
                    }
                    return setSize;

                case "zset":
                    long zsetSize = 0;
                    Set<String> zmembers = jedis.zrange(key, 0, -1);
                    for (String member : zmembers) {
                        if (member != null) {
                            zsetSize += member.getBytes().length;
                        }
                    }
                    return zsetSize;

                case "hash":
                    long hashSize = 0;
                    Map<String, String> hash = jedis.hgetAll(key);
                    for (Map.Entry<String, String> entry : hash.entrySet()) {
                        if (entry.getKey() != null) {
                            hashSize += entry.getKey().getBytes().length;
                        }
                        if (entry.getValue() != null) {
                            hashSize += entry.getValue().getBytes().length;
                        }
                    }
                    return hashSize;

                case "none":
                    return 0;

                default:
                    return 0;
            }
        } catch (Exception e) {
            // Key might have been deleted or expired
            return 0;
        }
    }
}
