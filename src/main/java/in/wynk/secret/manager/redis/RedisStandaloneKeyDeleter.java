package in.wynk.secret.manager.redis;

import redis.clients.jedis.*;

import java.util.*;

public class RedisStandaloneKeyDeleter {

    public static void main(String[] args) {

//        List<String> listIps = List.of(
//            "10.161.24.25", "10.161.24.26", "10.161.24.27", "10.161.24.28",
//            "10.161.24.29", "10.161.24.30", "10.161.24.31", "10.161.24.32",
//            "10.161.24.33", "10.161.24.34", "10.161.24.35", "10.161.24.36",
//            "10.161.24.37", "10.161.24.38", "10.161.24.39", "10.161.24.40",
//            "10.161.24.41", "10.161.24.42", "10.161.24.43", "10.161.24.44",
//            "10.161.24.45", "10.161.24.46", "10.161.24.47", "10.161.24.48",
//            "10.161.24.49"
//        );



//        List<String> listIps = List.of("10.161.24.66","10.161.24.67");

//        List<String> listIps = List.of("10.161.24.25");

        List<String> listIps = List.of("10.169.24.15","10.169.24.20");
        String password = "MysTr0ngP@ssw0rd@123"; // if no password, remove jedis.auth line

        String prefix = "user-activation-*";   // prefix to delete
        int scanCount = 50000;       // batch size

        long totalDeleted = 0;

        for (String ip : listIps) {
            try (Jedis jedis = new Jedis(ip, 6379)) {
                jedis.auth(password);

                String cursor = ScanParams.SCAN_POINTER_START;
                ScanParams scanParams = new ScanParams().match(prefix).count(scanCount);

                do {
                    ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                    List<String> keys = scanResult.getResult();

                    if (!keys.isEmpty()) {
                        jedis.unlink(keys.toArray(new String[0])); // async delete
                        totalDeleted += keys.size();
                        System.out.printf("Unlinked %d keys from %s%n", keys.size(), ip);
                    }

                    cursor = scanResult.getCursor();
                } while (!cursor.equals(ScanParams.SCAN_POINTER_START));

            } catch (Exception e) {
                System.err.printf("Error connecting to Redis at %s: %s%n", ip, e.getMessage());
            }
        }

        System.out.println("====================================================");
        System.out.printf("Total keys deleted across all nodes: %d%n", totalDeleted);
        System.out.println("====================================================");
    }
}
