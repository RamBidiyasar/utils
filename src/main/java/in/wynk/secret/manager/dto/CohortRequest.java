package in.wynk.secret.manager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public CohortRequest() {}

    public CohortRequest(String transactionId, String si, String uid, String rtn, String lob, String subLob, String segment, String oldSegment, String transactionTime, String thanksExpiry) {
        this.transactionId = transactionId;
        this.si = si;
        this.uid = uid;
        this.rtn = rtn;
        this.lob = lob;
        this.subLob = subLob;
        this.segment = segment;
        this.oldSegment = oldSegment;
        this.transactionTime = transactionTime;
        this.thanksExpiry = thanksExpiry;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String transactionId;
        private String si;
        private String uid;
        private String rtn;
        private String lob;
        private String subLob;
        private String segment;
        private String oldSegment;
        private String transactionTime;
        private String thanksExpiry;

        public Builder transactionId(String transactionId) { this.transactionId = transactionId; return this; }
        public Builder si(String si) { this.si = si; return this; }
        public Builder uid(String uid) { this.uid = uid; return this; }
        public Builder rtn(String rtn) { this.rtn = rtn; return this; }
        public Builder lob(String lob) { this.lob = lob; return this; }
        public Builder subLob(String subLob) { this.subLob = subLob; return this; }
        public Builder segment(String segment) { this.segment = segment; return this; }
        public Builder oldSegment(String oldSegment) { this.oldSegment = oldSegment; return this; }
        public Builder transactionTime(String transactionTime) { this.transactionTime = transactionTime; return this; }
        public Builder thanksExpiry(String thanksExpiry) { this.thanksExpiry = thanksExpiry; return this; }

        public CohortRequest build() {
            return new CohortRequest(transactionId, si, uid, rtn, lob, subLob, segment, oldSegment, transactionTime, thanksExpiry);
        }
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getSi() { return si; }
    public void setSi(String si) { this.si = si; }
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getRtn() { return rtn; }
    public void setRtn(String rtn) { this.rtn = rtn; }
    public String getLob() { return lob; }
    public void setLob(String lob) { this.lob = lob; }
    public String getSubLob() { return subLob; }
    public void setSubLob(String subLob) { this.subLob = subLob; }
    public String getSegment() { return segment; }
    public void setSegment(String segment) { this.segment = segment; }
    public String getOldSegment() { return oldSegment; }
    public void setOldSegment(String oldSegment) { this.oldSegment = oldSegment; }
    public String getTransactionTime() { return transactionTime; }
    public void setTransactionTime(String transactionTime) { this.transactionTime = transactionTime; }
    public String getThanksExpiry() { return thanksExpiry; }
    public void setThanksExpiry(String thanksExpiry) { this.thanksExpiry = thanksExpiry; }
}