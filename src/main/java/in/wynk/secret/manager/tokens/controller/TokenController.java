package in.wynk.secret.manager.tokens.controller;

import in.wynk.secret.manager.tokens.dto.TokenCreateRequest;
import in.wynk.secret.manager.tokens.dto.TokenCreateResponse;
import in.wynk.secret.manager.tokens.dto.TokenValidateRequest;
import in.wynk.secret.manager.tokens.dto.TokenValidateResponse;
import in.wynk.secret.manager.tokens.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/v1/create")
    public ResponseEntity<TokenCreateResponse> createTokens(@RequestBody TokenCreateRequest request) {
        return ResponseEntity.ok(tokenService.createTokens(request));
    }

    @PostMapping("/v1/validate")
    public ResponseEntity<TokenValidateResponse> validateToken(@RequestBody TokenValidateRequest request) {
        return ResponseEntity.ok(tokenService.validateToken(request.getToken()));
    }
}