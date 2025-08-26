package in.wynk.secret.manager.tokens.dto;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenCreateUserRequest {
    private String uid;
    private TimeUnitDetails ttl;
    private List<TokenIntent> intents;
}
