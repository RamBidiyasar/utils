package in.wynk.secret.manager.utils.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Duration;

public class DateDifferenceExample {
    public static void main(String[] args) {
        String scheduledDate = "2025-11-11";
        int ttlDays = 30;

        LocalDate localDate = LocalDate.parse(scheduledDate);
        LocalDateTime expiryDateTime = localDate.plusDays(ttlDays).atStartOfDay();
        LocalDateTime now = LocalDateTime.now(); // current date and time

        // Calculate duration between now and expiryDateTime
        Duration duration = Duration.between(now, expiryDateTime);
        long seconds = duration.getSeconds();

        System.out.println("Seconds between now and expiryDateTime: " + seconds);
    }
}
