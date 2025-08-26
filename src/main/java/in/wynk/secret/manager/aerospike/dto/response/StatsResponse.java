package in.wynk.secret.manager.aerospike.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatsResponse {
    private long recordCount;
    private long memoryUsedBytes;
    private long deviceUsedBytes;

    public static StatsResponse of(long recordCount, long memoryUsedBytes, long deviceUsedBytes) {
        StatsResponse stats = new StatsResponse();
        stats.setRecordCount(recordCount);
        stats.setMemoryUsedBytes(memoryUsedBytes);
        stats.setDeviceUsedBytes(deviceUsedBytes);
        return stats;
    }
}