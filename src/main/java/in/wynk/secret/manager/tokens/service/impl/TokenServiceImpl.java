package in.wynk.secret.manager.tokens.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.wynk.secret.manager.tokens.dto.TimeUnitDetails;
import in.wynk.secret.manager.tokens.dto.TokenCreateRequest;
import in.wynk.secret.manager.tokens.dto.TokenCreateResponse;
import in.wynk.secret.manager.tokens.dto.TokenCreateUserRequest;
import in.wynk.secret.manager.tokens.dto.TokenData;
import in.wynk.secret.manager.tokens.dto.TokenValidateResponse;
import in.wynk.secret.manager.tokens.service.TokenService;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {


    private static final String SECRET_KEY = "b3a8ee7c2b7d45d7a99bda4c38e0ad11"; // 32-char = 32-byte (256-bit)
    private static final int IV_LENGTH = 12;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public TokenCreateResponse createTokens(TokenCreateRequest request) {
        Map<String, TokenData> tokens = new HashMap<>();

        for (TokenCreateUserRequest user : request.getUsers()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("uid", user.getUid());
            payload.put("intents", user.getIntents());
            payload.put("exp", calculateExpiry(user.getTtl()));

            String token = encryptPayload(payload);
            tokens.put(user.getUid(), new TokenData(token));
        }

        return new TokenCreateResponse(tokens);
    }

    @Override
    public TokenValidateResponse validateToken(String token) {
        Map<String, Object> data = decryptPayload(token);

        long exp = (long) data.get("exp");
        if (System.currentTimeMillis() > exp) {
            throw new RuntimeException("Token expired");
        }

        return new TokenValidateResponse(
            (String) data.get("uid"),
            (List<Map<String, String>>) data.get("intents"),
            exp
        );
    }

    private long calculateExpiry(TimeUnitDetails ttl) {
        return System.currentTimeMillis() + ttl.toMillis();
    }

    private String encryptPayload(Map<String, Object> payload) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            byte[] ciphertext = cipher.doFinal(new ObjectMapper().writeValueAsBytes(payload));
            byte[] tokenBytes = ByteBuffer.allocate(iv.length + ciphertext.length)
                    .put(iv)
                    .put(ciphertext)
                    .array();

            return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    private Map<String, Object> decryptPayload(String token) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(token);
            ByteBuffer buffer = ByteBuffer.wrap(decoded);

            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);

            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new ObjectMapper().readValue(plaintext, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
