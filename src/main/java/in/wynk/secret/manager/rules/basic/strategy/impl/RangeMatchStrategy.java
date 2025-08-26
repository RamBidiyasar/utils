package in.wynk.secret.manager.rules.basic.strategy.impl;

import in.wynk.secret.manager.rules.basic.enums.MatchType;
import in.wynk.secret.manager.rules.basic.strategy.MatchStrategy;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Range-based match strategy.
 */
@Component
public class RangeMatchStrategy implements MatchStrategy {
    @Override
    public boolean match(Object value1, Object value2, Map<String, Object> params) {
        if (value1 instanceof Integer && value2 instanceof Integer) {
            int range = (int) params.getOrDefault("range", 0);
            return Math.abs((Integer) value1 - (Integer) value2) <= range;
        }
        return false;
    }

    @Override
    public MatchType getType() {
        return MatchType.RANGE;
    }
}
