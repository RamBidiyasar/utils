package in.wynk.secret.manager.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RedisKeyFetcher {

    public static void main(String[] args) {
        List<String> listIps = List.of(
            "10.161.24.25", "10.161.24.26", "10.161.24.27", "10.161.24.28",
            "10.161.24.29", "10.161.24.30", "10.161.24.31", "10.161.24.32",
            "10.161.24.33", "10.161.24.34", "10.161.24.35", "10.161.24.36",
            "10.161.24.37", "10.161.24.38", "10.161.24.39", "10.161.24.40",
            "10.161.24.41", "10.161.24.42", "10.161.24.43", "10.161.24.44",
            "10.161.24.45", "10.161.24.46", "10.161.24.47", "10.161.24.48",
            "10.161.24.49"
        );

//        List<String> listIps = List.of("10.161.24.66","10.161.24.67");

//        List<String> listIps = List.of("10.169.24.15","10.169.24.20");
        String password = "MysTr0ngP@ssw0rd@123";
//        String key = "arsenal-complete-axsta_x00i64981739954868922";
//        String key = "user-recent-fav-sync4FenZf3OmeMC934S30";
        String key = "preview-CdyBLFvJVy8ursL6S0-SHEMAROOME_VIDEO_MAHAKUMBH";
        List<String> responses = new ArrayList<>();
//        String OPERATION = "GET";
        String OPERATION = "DEL";

        for (String ip : listIps) {
            try (Jedis jedis = new Jedis(ip, 6379)) {
                jedis.auth(password);

                Object response = null;
                switch (OPERATION){
                   case  "GET" -> response = jedis.hgetAll(key); // Fetch from string key
                   case  "DEL" -> response = jedis.del(key); // Fetch from string key otherwise
                }

                if (ObjectUtils.isNotEmpty(response)) {
                    System.out.println("**********************************************************************");
                    System.out.println("Redis IP : " + ip + " , Response : " + response);
                    System.out.println("**********************************************************************");
                }
                responses.add("IP: " + ip + " Response: " + response);

            } catch (JedisException e) {
                System.err.println("Error connecting to Redis at " + ip + ": " + e.getMessage());
                responses.add("IP: " + ip + " Error: " + e.getMessage());
            }
        }

       // System.out.println(responses);
        writeToFile("sync.txt", responses);
    }

    private static void writeToFile(String fileName, List<String> responses) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String response : responses) {
                writer.write(response);
                writer.newLine();
            }
            // System.out.println("Responses written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
