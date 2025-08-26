package in.wynk.secret.manager.tokens.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidateResponse {
    private String uid;
    private List<Map<String, String>> intents;
    private long expiry;
}
