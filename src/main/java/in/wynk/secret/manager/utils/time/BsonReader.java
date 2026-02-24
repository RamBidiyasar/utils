package in.wynk.secret.manager.utils.time;


import org.bson.BasicBSONDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class BsonReader {
    public static void main(String[] args) {
        File bsonFile = new File("/Users/B0296099/Documents/BE_REPOS/utils/src/main/java/in/wynk/secret/manager/utils/time/cp_config.bson");

        try (InputStream inputStream = new FileInputStream(bsonFile)) {
            BasicBSONDecoder decoder = new BasicBSONDecoder();
            
            while (inputStream.available() > 0) {
                // Read one document at a time
                org.bson.BSONObject bsonObject = decoder.readObject(inputStream);
                
                // Convert to a more usable Map-like structure if needed
                String json = bsonObject.toString();
                System.out.println(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}