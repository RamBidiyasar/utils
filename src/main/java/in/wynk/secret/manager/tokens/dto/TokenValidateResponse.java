package in.wynk.secret.manager.tokens.dto;

import java.util.List;
import java.util.Map;

public class TokenValidateResponse {
    private String uid;
    private List<Map<String, String>> intents;
    private long expiry;

    public TokenValidateResponse() {}

    public TokenValidateResponse(String uid, List<Map<String, String>> intents, long expiry) {
        this.uid = uid;
        this.intents = intents;
        this.expiry = expiry;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public List<Map<String, String>> getIntents() { return intents; }
    public void setIntents(List<Map<String, String>> intents) { this.intents = intents; }
    public long getExpiry() { return expiry; }
    public void setExpiry(long expiry) { this.expiry = expiry; }
}