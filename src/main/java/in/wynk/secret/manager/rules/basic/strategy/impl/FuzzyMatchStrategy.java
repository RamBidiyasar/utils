package in.wynk.secret.manager.rules.basic.strategy.impl;

import in.wynk.secret.manager.rules.basic.enums.MatchType;
import in.wynk.secret.manager.rules.basic.strategy.MatchStrategy;
import org.apache.lucene.search.spell.LevenshteinDistance;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Fuzzy match strategy using Levenshtein distance.
 */
@Component
public class FuzzyMatchStrategy implements MatchStrategy {

    public static final String MAX_DISTANCE = "maxDistance";

    @Override
    public boolean match(Object value1, Object value2, Map<String, Object> params) {
        if (value1 instanceof String && value2 instanceof String) {
            int maxDistance = (int) params.getOrDefault(MAX_DISTANCE, 1);
            LevenshteinDistance levenshtein = new LevenshteinDistance();
            int distance = (int) levenshtein.getDistance((String) value1, (String) value2);
            return distance <= maxDistance;
        }
        return false;
    }

    @Override
    public MatchType getType() {
        return MatchType.FUZZY;
    }
}
