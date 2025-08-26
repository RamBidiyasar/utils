package in.wynk.secret.manager.rules.basic.strategy;

import in.wynk.secret.manager.rules.basic.enums.MatchType;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Factory to get the correct match strategy dynamically.
 */
@Component
public class MatchStrategyFactory {
    private final Map<MatchType, MatchStrategy> strategyMap;

    public MatchStrategyFactory(Map<String, MatchStrategy> strategies) {
        this.strategyMap = strategies.values().stream()
            .collect(Collectors.toMap(MatchStrategy::getType, strategy -> strategy));
    }

    /**
     * Gets the appropriate match strategy for a given type.
     * @param type The match type as an enum
     * @return Corresponding MatchStrategy implementation
     */
    public MatchStrategy getStrategy(MatchType type) {
        return strategyMap.get(type);
    }
}
