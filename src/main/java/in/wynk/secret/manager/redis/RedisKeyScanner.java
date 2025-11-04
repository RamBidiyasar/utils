package in.wynk.secret.manager.redis;

import redis.clients.jedis.Jedis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class RedisKeyScanner {

    public static void main(String[] args) {
        // List of Redis IPs to scan
//        List<String> listIps = List.of(
//                "10.161.24.25", "10.161.24.26", "10.161.24.27", "10.161.24.28",
//                "10.161.24.29", "10.161.24.30", "10.161.24.31", "10.161.24.32",
//                "10.161.24.33", "10.161.24.34", "10.161.24.35", "10.161.24.36",
//                "10.161.24.37", "10.161.24.38", "10.161.24.39", "10.161.24.40",
//                "10.161.24.41", "10.161.24.42", "10.161.24.43", "10.161.24.44",
//                "10.161.24.45", "10.161.24.46", "10.161.24.47", "10.161.24.48",
//                "10.161.24.49"
//        );


         List<String> listIps = List.of("10.169.24.15","10.169.24.20");


        String password = "MysTr0ngP@ssw0rd@123";
        String keyPrefix = "user-subs-info-*"; // The prefix for the keys you want to find

        List<String> matchingKeys = new ArrayList<>();

        for (String ip : listIps) {
            try (Jedis jedis = new Jedis(ip, 6379)) {
                jedis.auth(password);

                ScanParams scanParams = new ScanParams().match(keyPrefix).count(50000); // count is a hint for SCAN
                String cursor = ScanParams.SCAN_POINTER_START;
                boolean done = false;

                System.out.println("Scanning Redis at " + ip + " for keys with prefix '" + keyPrefix + "'...");

                while (!done) {
                    ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                    List<String> keys = scanResult.getResult();

                    for (String key : keys) {
                        String result = "IP: " + ip + ", Key: " + key;
                        System.out.println("Found key -> " + result);
                        matchingKeys.add(result);
                    }

                    cursor = scanResult.getCursor();
                    if ("0".equals(cursor)) {
                        done = true;
                    }
                }
                System.out.println("Finished scanning Redis at " + ip);

            } catch (Exception e) {
                String errorMessage = "Error connecting to or scanning Redis at " + ip + ": " + e.getMessage();
                System.err.println(errorMessage);
                matchingKeys.add("IP: " + ip + " Error: " + e.getMessage());
            }
        }

        System.out.println("\nScan complete. Found " + matchingKeys.size() + " matching keys in total.");
        writeToFile("matching_keys_found.txt", matchingKeys);
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
