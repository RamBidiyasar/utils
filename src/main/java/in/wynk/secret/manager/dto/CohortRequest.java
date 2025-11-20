package in.wynk.secret.manager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CohortRequest {

    @JsonProperty("TransactionID")
    private String transactionId;

    @JsonProperty("si")
    private String si;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("rtn")
    private String rtn;

    @JsonProperty("lob")
    private String lob;

    @JsonProperty("sub_lob")
    private String subLob;

    @JsonProperty("segment")
    private String segment;

    @JsonProperty("oldSegment")
    private String oldSegment;

    @JsonProperty("transaction_time")
    private String transactionTime;

    @JsonProperty("thanks_expiry")
    private String thanksExpiry;
}
