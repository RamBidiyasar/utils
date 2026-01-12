package in.wynk.secret.manager.tokens.dto;

public class TokenValidateRequest {
    private String token;

    public TokenValidateRequest() {}

    public TokenValidateRequest(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}