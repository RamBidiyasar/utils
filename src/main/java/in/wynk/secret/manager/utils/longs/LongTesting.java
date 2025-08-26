package in.wynk.secret.manager.utils.longs;


import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class LongTesting {
    public static void main(String[] args) {
        System.out.println(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - Instant.now().getEpochSecond()));
    }
}