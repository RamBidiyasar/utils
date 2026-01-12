package in.wynk.secret.manager.tokens.dto;

import java.util.List;

public class TokenCreateRequest {
    private List<TokenCreateUserRequest> users;

    public TokenCreateRequest() {}

    public List<TokenCreateUserRequest> getUsers() { return users; }
    public void setUsers(List<TokenCreateUserRequest> users) { this.users = users; }
}