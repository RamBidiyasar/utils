package in.wynk.secret.manager.rules.basic.strategy.impl;

import in.wynk.secret.manager.rules.basic.enums.MatchType;
import in.wynk.secret.manager.rules.basic.strategy.MatchStrategy;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Exact match strategy.
 */
@Component
public class ExactMatchStrategy implements MatchStrategy {
    @Override
    public boolean match(Object value1, Object value2, Map<String, Object> params) {
        return value1.equals(value2);
    }

    @Override
    public MatchType getType() {
        return MatchType.EXACT;
    }
}
