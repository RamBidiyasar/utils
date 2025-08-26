package in.wynk.secret.manager.rules.basic.service;

import in.wynk.secret.manager.rules.basic.config.RuleConfig;
import in.wynk.secret.manager.rules.basic.evaluator.RuleEvaluator;
import org.springframework.stereotype.Service;

/**
 * Service to compare objects using the configured rule engine.
 */
@Service
public class MatchService {
    private final RuleConfig ruleConfig;
    private final RuleEvaluator ruleEvaluator;

    public MatchService(RuleConfig ruleConfig, RuleEvaluator ruleEvaluator) {
        this.ruleConfig = ruleConfig;
        this.ruleEvaluator = ruleEvaluator;
    }

    /**
     * Compares two objects using defined rules and returns the matching result.
     */
    public boolean compareObjects(Object obj1, Object obj2) throws Exception {
        return ruleEvaluator.evaluate(ruleConfig, obj1, obj2);
    }
}