package in.wynk.secret.manager.tokens;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class sfsdf {

    public static void main(String[] args) {
        Date date = new Date(Instant.now().toEpochMilli());
        System.out.println(date.toInstant().toString());

    }
}
