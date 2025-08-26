package in.wynk.secret.manager.rules.basic.strategy;

import in.wynk.secret.manager.rules.basic.enums.MatchType;
import java.util.Map;

/**
 * Interface for different match strategies.
 */
public interface MatchStrategy {
    /**
     * Compares two values based on a specific match strategy.
     * @param value1 First value
     * @param value2 Second value
     * @param params Additional parameters for the strategy (key-value pairs)
     * @return true if values match based on the strategy, false otherwise
     */
    boolean match(Object value1, Object value2, Map<String, Object> params);

    /**
     * Returns the match type for this strategy.
     * @return MatchType enum value
     */
    MatchType getType();
}
