package in.wynk.secret.manager.rules.basic.enums;

import org.springframework.util.ObjectUtils;
import java.util.List;

/**
 * Enum representing logical conditions (AND, OR) with behavior.
 */
public enum LogicalCondition {

    AND {
        @Override
        public boolean evaluate(List<Boolean> results) {
            return !ObjectUtils.isEmpty(results) && results.stream().allMatch(Boolean::booleanValue);
        }
    },

    OR {
        @Override
        public boolean evaluate(List<Boolean> results) {
            return !ObjectUtils.isEmpty(results) && results.stream().anyMatch(Boolean::booleanValue);
        }
    };

    /**
     * Abstract method to evaluate the condition on a list of boolean results.
     * @param results List of boolean values from rule evaluations.
     * @return true if condition is met, false otherwise.
     */
    public abstract boolean evaluate(List<Boolean> results);
}