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

public class RedisKeyDeleter {

    public static void main(String[] args) {
        List<String> listIps = List.of("10.169.24.15", "10.169.24.20");
        String password = "MysTr0ngP@ssw0rd@123";
        String keySubstring = "R2kQe9eiOWzVwEpX40";

        List<String> deletedKeys = new ArrayList<>();
        List<String> failedDeletions = new ArrayList<>();

        for (String ip : listIps) {
            try (Jedis jedis = new Jedis(ip, 6379)) {
                jedis.auth(password);

                ScanParams scanParams = new ScanParams().match("*" + keySubstring + "*").count(1000);
                String cursor = ScanParams.SCAN_POINTER_START;
                boolean done = false;

                System.out.println("Scanning Redis at " + ip + " for keys containing '" + keySubstring + "'...");

                while (!done) {
                    ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                    List<String> keys = scanResult.getResult();

                    for (String key : keys) {
                        try {
                            // Check if the key exists before deleting to avoid WRONGTYPE errors
                            if (jedis.exists(key)) {
                                long delResult = jedis.del(key);
                                if (delResult > 0) {
                                    String successMessage = "Deleted key '" + key + "' from " + ip;
                                    System.out.println(successMessage);
                                    deletedKeys.add(successMessage);
                                } else {
                                    String errorMessage = "Failed to delete key '" + key + "' from " + ip + " (jedis.del returned 0).";
                                    System.err.println(errorMessage);
                                    failedDeletions.add(errorMessage);
                                }
                            } else {
                                String skippedMessage = "Skipped key '" + key + "' from " + ip + " because it does not exist.";
                                System.out.println(skippedMessage);
                            }
                        } catch (JedisException e) {
                            String errorMessage = "Error processing key '" + key + "' from " + ip + ": " + e.getMessage();
                            System.err.println(errorMessage);
                            failedDeletions.add(errorMessage);
                        }
                    }

                    cursor = scanResult.getCursor();
                    if ("0".equals(cursor)) {
                        done = true;
                    }
                }
                System.out.println("Finished scanning and deleting on Redis at " + ip);

            } catch (JedisException e) {
                String errorMessage = "Error connecting to or scanning Redis at " + ip + ": " + e.getMessage();
                System.err.println(errorMessage);
                failedDeletions.add("IP: " + ip + " Error: " + e.getMessage());
            }
        }

        System.out.println("\nDeletion process complete.");
        System.out.println("Total keys deleted: " + deletedKeys.size());
        System.out.println("Total failed deletions: " + failedDeletions.size());

        writeToFile("deleted_keys.txt", deletedKeys);
        writeToFile("failed_deletions.txt", failedDeletions);
    }

    private static void writeToFile(String fileName, List<String> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Results have been written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing to file '" + fileName + "': " + e.getMessage());
        }
    }
}
