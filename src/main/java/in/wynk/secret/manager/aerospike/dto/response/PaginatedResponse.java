package in.wynk.secret.manager.aerospike.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginatedResponse {
    private Map<String, Object> records;
    private int currentPage;
    private long totalPages;
    private long totalRecords;

    public static PaginatedResponse of(Map<String, Object> records, int currentPage, long totalPages, long totalRecords) {
        PaginatedResponse response = new PaginatedResponse();
        response.setRecords(records);
        response.setCurrentPage(currentPage);
        response.setTotalPages(totalPages);
        response.setTotalRecords(totalRecords);
        return response;
    }

    public Map<String, Object> getRecords() { return records; }
    public void setRecords(Map<String, Object> records) { this.records = records; }
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    public long getTotalPages() { return totalPages; }
    public void setTotalPages(long totalPages) { this.totalPages = totalPages; }
    public long getTotalRecords() { return totalRecords; }
    public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }
}
