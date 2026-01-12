package in.wynk.secret.manager.rules.basic.evaluator;

import in.wynk.secret.manager.rules.basic.config.RuleConfig;
import in.wynk.secret.manager.rules.basic.strategy.MatchStrategyFactory;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Evaluates rules dynamically using the match strategy pattern.
 */
@Component
public class RuleEvaluator {
    private final MatchStrategyFactory matchStrategyFactory;

    public RuleEvaluator(MatchStrategyFactory matchStrategyFactory) {
        this.matchStrategyFactory = matchStrategyFactory;
    }

    public boolean evaluate(RuleConfig rule, Object obj1, Object obj2) throws Exception {
        if (rule.getRules() != null && !rule.getRules().isEmpty()) {
            List<Boolean> results = rule.getRules().stream()
                .map(r -> {
                    try {
                        return evaluate(r, obj1, obj2);
                    } catch (Exception e) {
                        return false;
                    }
                }).collect(Collectors.toList());

            return rule.getCondition().evaluate(results);
        } else {
            return evaluateSingleRule(rule, obj1, obj2);
        }
    }

    private boolean evaluateSingleRule(RuleConfig rule, Object obj1, Object obj2) throws Exception {
        Field field1 = obj1.getClass().getDeclaredField(rule.getSourceField());
        field1.setAccessible(true);
        Object value1 = field1.get(obj1);

        Field field2 = obj2.getClass().getDeclaredField(rule.getTargetField());
        field2.setAccessible(true);
        Object value2 = field2.get(obj2);

        return matchStrategyFactory.getStrategy(rule.getType()).match(value1, value2, rule.getParams());
    }
}