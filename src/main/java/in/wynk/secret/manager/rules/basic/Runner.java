package in.wynk.secret.manager.rules.basic;

import in.wynk.secret.manager.rules.basic.model.Customer;
import in.wynk.secret.manager.rules.basic.model.User;
import in.wynk.secret.manager.rules.basic.service.MatchService;
import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;


/**
 * CommandLineRunner to test rule-based matching on application startup.
 */
@Component
public class Runner implements CommandLineRunner {
    private final MatchService matchService;

    public Runner(MatchService matchService) {
        this.matchService = matchService;
    }

    @Override
    public void run(String... args) throws Exception {
//        User user = new User("John Doe", 30, "New York");
//        Customer customer = new Customer("Jon Doe", 32, "New York");
//
//        boolean result = matchService.compareObjects(user, customer);
//        System.out.println("Are they the same ? " + result);
    }
}



