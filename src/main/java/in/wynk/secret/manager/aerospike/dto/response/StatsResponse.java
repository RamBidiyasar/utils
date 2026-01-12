package in.wynk.secret.manager.aerospike.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

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

    public long getRecordCount() { return recordCount; }
    public void setRecordCount(long recordCount) { this.recordCount = recordCount; }
    public long getMemoryUsedBytes() { return memoryUsedBytes; }
    public void setMemoryUsedBytes(long memoryUsedBytes) { this.memoryUsedBytes = memoryUsedBytes; }
    public long getDeviceUsedBytes() { return deviceUsedBytes; }
    public void setDeviceUsedBytes(long deviceUsedBytes) { this.deviceUsedBytes = deviceUsedBytes; }
}
