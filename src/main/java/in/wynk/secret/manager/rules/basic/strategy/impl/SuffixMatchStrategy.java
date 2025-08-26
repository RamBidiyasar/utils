package in.wynk.secret.manager.rules.basic.strategy.impl;

import in.wynk.secret.manager.rules.basic.enums.MatchType;
import in.wynk.secret.manager.rules.basic.strategy.MatchStrategy;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Suffix match strategy.
 */
@Component
public class SuffixMatchStrategy implements MatchStrategy {
    @Override
    public boolean match(Object value1, Object value2, Map<String, Object> params) {
        if (value1 instanceof String && value2 instanceof String) {
            return ((String) value1).endsWith((String) value2);
        }
        return false;
    }

    @Override
    public MatchType getType() {
        return MatchType.SUFFIX;
    }
}
