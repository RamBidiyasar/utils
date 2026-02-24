package in.wynk.secret.manager.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RedisUidCleanup {

    public static void main(String[] args) {
        // List of IPs to scan (Configured based on existing files like RedisKeyDeleter.java)
//        List<String> listIps = List.of("10.169.24.15", "10.169.24.20");
        
        // Use this list if you need to scan the larger cluster found in RedisKeyFetcher.java:

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
        String uid = "nIq19n7MegimQVxa40";
        String matchPattern = "*" + uid + "*";

        List<String> deletedKeys = new ArrayList<>();
        List<String> failedDeletions = new ArrayList<>();

        System.out.println("Starting cleanup for keys containing UID: " + uid);
        System.out.println("Match Pattern: " + matchPattern);

        for (String ip : listIps) {
            try (Jedis jedis = new Jedis(ip, 6379)) {
                jedis.auth(password);

                ScanParams scanParams = new ScanParams().match(matchPattern).count(1000);
                String cursor = ScanParams.SCAN_POINTER_START;
                boolean done = false;

                System.out.println("Scanning Redis at " + ip + "...");

                while (!done) {
                    ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                    List<String> keys = scanResult.getResult();

                    for (String key : keys) {
                        try {
                            // Check existence to avoid redundant delete calls or errors
                            if (jedis.exists(key)) {
                                long delResult = jedis.del(key);
                                if (delResult > 0) {
                                    String msg = "Deleted key '" + key + "' from " + ip;
                                    System.out.println(msg);
                                    deletedKeys.add(msg);
                                } else {
                                    String msg = "Failed to delete key '" + key + "' from " + ip;
                                    System.err.println(msg);
                                    failedDeletions.add(msg);
                                }
                            }
                        } catch (JedisException e) {
                            String msg = "Error processing key '" + key + "' from " + ip + ": " + e.getMessage();
                            System.err.println(msg);
                            failedDeletions.add(msg);
                        }
                    }

                    cursor = scanResult.getCursor();
                    if ("0".equals(cursor)) {
                        done = true;
                    }
                }
                System.out.println("Finished scanning " + ip);

            } catch (JedisException e) {
                String msg = "Error connecting to " + ip + ": " + e.getMessage();
                System.err.println(msg);
                failedDeletions.add(msg);
            }
        }

        System.out.println("\nCleanup complete.");
        System.out.println("Total keys deleted: " + deletedKeys.size());
        
        writeToFile("uid_deleted_keys.txt", deletedKeys);
        writeToFile("uid_failed_deletions.txt", failedDeletions);
    }

    private static void writeToFile(String fileName, List<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Log written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing to file '" + fileName + "': " + e.getMessage());
        }
    }
}
