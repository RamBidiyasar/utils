package in.wynk.secret.manager.rules.basic.config;

import in.wynk.secret.manager.rules.basic.enums.LogicalCondition;
import in.wynk.secret.manager.rules.basic.enums.MatchType;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to hold rule-based matching settings.
 */
@Configuration
@ConfigurationProperties(prefix = "matching-rules")
public class RuleConfig {
    private LogicalCondition condition;
    private List<RuleConfig> rules;
    private String sourceField;
    private String targetField;
    private MatchType type;
    private Map<String, Object> params;

    public LogicalCondition getCondition() { return condition; }
    public void setCondition(LogicalCondition condition) { this.condition = condition; }
    public List<RuleConfig> getRules() { return rules; }
    public void setRules(List<RuleConfig> rules) { this.rules = rules; }
    public String getSourceField() { return sourceField; }
    public void setSourceField(String sourceField) { this.sourceField = sourceField; }
    public String getTargetField() { return targetField; }
    public void setTargetField(String targetField) { this.targetField = targetField; }
    public MatchType getType() { return type; }
    public void setType(MatchType type) { this.type = type; }
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
