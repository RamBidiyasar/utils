package in.wynk.secret.manager.tokens.dto;

import java.util.Map;

public class TokenCreateResponse {
    private Map<String, TokenData> tokens;

    public TokenCreateResponse() {}

    public TokenCreateResponse(Map<String, TokenData> tokens) {
        this.tokens = tokens;
    }

    public Map<String, TokenData> getTokens() { return tokens; }
    public void setTokens(Map<String, TokenData> tokens) { this.tokens = tokens; }
}