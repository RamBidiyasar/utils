package in.wynk.secret.manager.redis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import redis.clients.jedis.*;

public class RedisUniquePrefixScanner {

    private static final Pattern DIGIT_PATTERN = Pattern.compile("(\\D+).*");

    public static void main(String[] args) {
        // List of IPs to scan
        List<String> listIps = List.of("10.169.24.15", "10.169.24.20");
        String password = "wcfMsisdn";

        Set<String> uniquePrefixes = Collections.synchronizedSet(new HashSet<>());
        int scanCount = 10000;

        for (String ip : listIps) {
            System.out.println("Connecting to " + ip + "...");
            try (Jedis jedis = new Jedis(ip, 6379)) {
                jedis.auth(password);

                String cursor = ScanParams.SCAN_POINTER_START;
                ScanParams scanParams = new ScanParams().match("*").count(scanCount);

                do {
                    ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                    List<String> keys = scanResult.getResult();

                    if (!keys.isEmpty()) {
                        processKeys(jedis, keys, uniquePrefixes);
                    }

                    cursor = scanResult.getCursor();
                } while (!cursor.equals(ScanParams.SCAN_POINTER_START));

                System.out.println("Finished scanning " + ip);

            } catch (Exception e) {
                System.err.printf("Error connecting/scanning Redis at %s: %s%n", ip, e.getMessage());
            }
        }

        System.out.println("====================================================");
        System.out.println("Unique Prefixes Found:");
        List<String> sortedPrefixes = new ArrayList<>(uniquePrefixes);
        Collections.sort(sortedPrefixes);
        for (String prefix : sortedPrefixes) {
            System.out.println(prefix);
        }
        System.out.println("====================================================");
        System.out.println("Total Unique Prefixes: " + uniquePrefixes.size());

        writeToFile("unique_prefixes.txt", sortedPrefixes);
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

    private static void processKeys(Jedis jedis, List<String> keys, Set<String> uniquePrefixes) {
        // Use pipeline to get types of all keys in batch
        Pipeline p = jedis.pipelined();
        for (String key : keys) {
            p.type(key);
        }
        List<Object> types = p.syncAndReturnAll();

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String type = (String) types.get(i);

            // Extract prefix for the key itself
            uniquePrefixes.add(getPrefix(key));

            // If hash, scan fields
            if ("hash".equalsIgnoreCase(type)) {
                processHashFields(jedis, key, uniquePrefixes);
            }
        }
    }

    private static void processHashFields(Jedis jedis, String key, Set<String> uniquePrefixes) {
        try {
            String cursor = ScanParams.SCAN_POINTER_START;
            ScanParams scanParams = new ScanParams().count(1000); // Scan fields in chunks

            do {
                ScanResult<Map.Entry<String, String>> scanResult = jedis.hscan(key, cursor, scanParams);
                List<Map.Entry<String, String>> fields = scanResult.getResult();

                for (Map.Entry<String, String> field : fields) {
                    uniquePrefixes.add(getPrefix(field.getKey()) + " (field)");
                }

                cursor = scanResult.getCursor();
            } while (!cursor.equals(ScanParams.SCAN_POINTER_START));
        } catch (Exception e) {
            System.err.println("Error scanning hash fields for key " + key + ": " + e.getMessage());
        }
    }

    private static String getPrefix(String str) {
        if (str == null || str.isEmpty()) return "[empty]";

        // 1. If contains ':', take substring before first ':'
        int colonIndex = str.indexOf(':');
        if (colonIndex != -1) {
            return str.substring(0, colonIndex + 1);
        }

        String prefix = str;

        // 2. If contains digits, take substring before first digit (Standard ID pattern)
        Matcher matcher = DIGIT_PATTERN.matcher(prefix);
        if (matcher.matches()) {
            prefix = matcher.group(1);
        }

        // 3. Handle CamelCase transitions (e.g., "syncCglb" -> "sync")
        // We look for a lowercase letter followed immediately by an uppercase letter.
        // We only apply this if it happens AFTER the last hyphen/underscore (to avoid breaking CamelCase prefixes)
        // or if there are no separators.
        int lastSep = Math.max(prefix.lastIndexOf('-'), prefix.lastIndexOf('_'));
        
        Matcher camelMatcher = Pattern.compile("([a-z])([A-Z])").matcher(prefix);
        if (camelMatcher.find()) {
            int splitIndex = camelMatcher.start() + 1; // Split after the lowercase letter
            if (splitIndex > lastSep) {
                // Check if we are splitting a legitimate prefix or an appended ID
                // Simple heuristic: if the part after split is "variable-looking", assume it's an ID.
                String suffix = prefix.substring(splitIndex);
                // "Cglb" -> variable? "MyKey" -> "My" "Key"?
                // For "user-recent-fav-syncCglb", split is at sync|Cglb.
                // We take the part before.
                prefix = prefix.substring(0, splitIndex);
            }
        }

        // 4. Handle Hyphen/Underscore separators with variable suffixes
        // e.g. "user-free-playback-znAFxBa" -> "user-free-playback-"
        // We re-calculate lastSep because prefix might have changed in steps 2 or 3.
        lastSep = Math.max(prefix.lastIndexOf('-'), prefix.lastIndexOf('_'));
        
        if (lastSep > 0 && lastSep < prefix.length() - 1) {
            String suffix = prefix.substring(lastSep + 1);
            if (isVariable(suffix)) {
                prefix = prefix.substring(0, lastSep + 1);
            }
        }

        return prefix;
    }

    private static boolean isVariable(String s) {
        // Contains digit?
        if (s.chars().anyMatch(Character::isDigit)) return true;
        // Length < 3? (Short suffixes like -A, -B, -en, -zs) -> Often variable enumerations/IDs.
        if (s.length() < 3) return true;
        // Mixed case? (Base64-like or high entropy) -> e.g. "znAFxBa"
        boolean hasUpper = s.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = s.chars().anyMatch(Character::isLowerCase);
        if (hasUpper && hasLower) return true;

        return false;
    }
}
