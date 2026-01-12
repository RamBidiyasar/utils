package in.wynk.secret.manager.tokens.dto;

import java.util.List;

public class TokenCreateUserRequest {
    private String uid;
    private TimeUnitDetails ttl;
    private List<TokenIntent> intents;

    public TokenCreateUserRequest() {}

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public TimeUnitDetails getTtl() { return ttl; }
    public void setTtl(TimeUnitDetails ttl) { this.ttl = ttl; }
    public List<TokenIntent> getIntents() { return intents; }
    public void setIntents(List<TokenIntent> intents) { this.intents = intents; }
}