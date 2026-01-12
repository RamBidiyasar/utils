package in.wynk.secret.manager.tokens.dto;

public class TokenIntent {
    private String type;     // e.g. "claim"
    private String partner;  // e.g. "netflix"

    public TokenIntent() {}

    public TokenIntent(String type, String partner) {
        this.type = type;
        this.partner = partner;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPartner() { return partner; }
    public void setPartner(String partner) { this.partner = partner; }
}