package in.wynk.secret.manager.aerospike.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Data;

@Data
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
}