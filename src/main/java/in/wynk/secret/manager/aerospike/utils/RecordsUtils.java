package in.wynk.secret.manager.aerospike.utils;

import com.aerospike.client.Record;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RecordsUtils {
    public static int getApproxSize(Record record) {
        return record.bins.entrySet().stream()
            .mapToInt(e -> e.getKey().getBytes(StandardCharsets.UTF_8).length +
                (e.getValue() instanceof String ? ((String) e.getValue()).getBytes(StandardCharsets.UTF_8).length :
                e.getValue() instanceof byte[] ? ((byte[]) e.getValue()).length :
                8))
            .sum();
    }


    public static Map<String, Object> toMap(Record record) {
       return Map.of(
           "values", record.bins,
           "ttl", record.getTimeToLive(),
           "size", RecordsUtils.getApproxSize(record)
        );
    }
}
