package in.wynk.secret.manager.aerospike.utils;

import com.aerospike.client.Record;
import java.nio.charset.StandardCharsets;

public class AerospikeUtil {
    public static int getApproxSize(Record record) {
        return record.bins.entrySet().stream()
            .mapToInt(e -> e.getKey().getBytes(StandardCharsets.UTF_8).length +
                (e.getValue() instanceof String ? ((String) e.getValue()).getBytes(StandardCharsets.UTF_8).length :
                e.getValue() instanceof byte[] ? ((byte[]) e.getValue()).length :
                8))
            .sum();
    }
}
