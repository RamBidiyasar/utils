package in.wynk.secret.manager.rules.basic.config;

import in.wynk.secret.manager.rules.basic.enums.LogicalCondition;
import in.wynk.secret.manager.rules.basic.enums.MatchType;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to hold rule-based matching settings.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "matching-rules")
public class RuleConfig {
    private LogicalCondition condition;
    private List<RuleConfig> rules;
    private String sourceField;
    private String targetField;
    private MatchType type;
    private Map<String, Object> params;
}