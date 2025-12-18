package in.wynk.secret.manager.utils.time;

import java.util.concurrent.TimeUnit;
import java.time.Instant;

public class ValidityChecker {

    public static void main(String[] args) {
        long time = TimeUnit.HOURS.toMillis(47);
        long days = TimeUnit.MILLISECONDS.toDays(time);
        System.out.println(days);
    }
}