package in.wynk.secret.manager.aerospike.utils;

import com.aerospike.client.Record;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;

public final class RecordsUtils {

    private RecordsUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int getApproxSize(Record record) {
        if(MapUtils.isEmpty(record.bins)) {
            return 0;
        }
        return record.bins.entrySet().stream()
            .mapToInt(e -> e.getKey().getBytes(StandardCharsets.UTF_8).length +
                (e.getValue() instanceof String ? ((String) e.getValue()).getBytes(StandardCharsets.UTF_8).length :
                e.getValue() instanceof byte[] ? ((byte[]) e.getValue()).length :
                8))
            .sum();
    }

    public static Map<String, Object> toMap(Record record) {
       return Map.of(
           "values", MapUtils.isEmpty(record.bins) ? Map.of() : record.bins,
           "ttl", record.getTimeToLive(),
           "size", RecordsUtils.getApproxSize(record)
        );
    }
}