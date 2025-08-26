package in.wynk.secret.manager.tokens.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenIntent {
    private String type;     // e.g. "claim"
    private String partner;  // e.g. "netflix"
}
