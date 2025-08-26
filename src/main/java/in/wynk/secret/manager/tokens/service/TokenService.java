package in.wynk.secret.manager.tokens.service;

import in.wynk.secret.manager.tokens.dto.TokenCreateRequest;
import in.wynk.secret.manager.tokens.dto.TokenCreateResponse;
import in.wynk.secret.manager.tokens.dto.TokenValidateResponse;

public interface TokenService {
    TokenCreateResponse createTokens(TokenCreateRequest request);
    TokenValidateResponse validateToken(String token);
}
