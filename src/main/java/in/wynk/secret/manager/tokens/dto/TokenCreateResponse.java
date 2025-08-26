package in.wynk.secret.manager.tokens.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenCreateResponse {
    private Map<String, TokenData> tokens;
}
