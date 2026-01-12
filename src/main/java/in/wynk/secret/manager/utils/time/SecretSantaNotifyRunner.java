package in.wynk.secret.manager.utils.time;

import org.springframework.http.*;
import java.util.Base64;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class SecretSantaNotifyRunner {

    // ================= Notify Service Config =================
    private static final RestTemplate restTemplate = new RestTemplate();

    private static final String EMAIL_API_URL =
            "http://notification-management.internal.airtel.tv/notification/v1/send/instant";

    private static final String AUTH_USERNAME = "9rqpmxtra2uq3321j";
    private static final String AUTH_PASSWORD = "k61g5humgga7a6orkchn7ezmz6yjgfx";
    private static final String BASIC_AUTH_CLIENT = "notify-manager-msp";

    // ================= Secret Santa Config =================
    private static final boolean PRINT_RESULTS = true;
    private static final boolean EMAIL_RESULTS = true;

    private static class Person {
        String name;
        String email;

        Person(String name, String email) {
            this.name = name;
            this.email = email;
        }

        @Override
        public String toString() {
            return name + " <" + email + ">";
        }
    }

    // ================= PSVM =================
    public static void main(String[] args) {

        List<Person> pool = List.of(
                new Person("Sanket Arora", "sanket.arora@airtel.com"),
                new Person("Ram", "Ram.Bidiyasar@airtel.com"),
                new Person("Bharti", "Bharti.Sukhadiya@airtel.com")
        );

        List<Person> available = new ArrayList<>(pool);
        List<Map.Entry<Person, Person>> pairs = new ArrayList<>();

        Random random = new Random();

        // Build Secret Santa pairs
        for (Person santa : pool) {
            while (true) {
                Person recipient = available.get(random.nextInt(available.size()));
                if (!recipient.equals(santa)) {
                    available.remove(recipient);
                    pairs.add(new AbstractMap.SimpleEntry<>(santa, recipient));
                    break;
                }
            }
        }

        for (Map.Entry<Person, Person> pair : pairs) {
            Person secretSanta = pair.getKey();
            Person recipient = pair.getValue();

            String subject = "SECRET SANTA -- ho ho ho";
            String from = "streamready@airtel.com";

            String body =
                    "Hi" + secretSanta.name+ "\n\n" +
                    "Ho Ho Ho! 🎄✨ Christmas cheer is officially in the air, and guess what… " +
                    "Shhh 🤫 it’s a SECRET!\n\n" +
                    "🎁 What you need to do:\n\n" +
                    "Bring one wrapped gift for **" + recipient.name + "**, with a name tag\n\n" +
                    "Gift value: up to ₹500\n\n" +
                    "Dress Code as per theme – Red, Green, White\n\n" +
                    "Remember… it’s not about the price, it’s about the surprise!\n\n" +
                    "📍 Gift exchange on 18th December at 2:30 PM at Entertainment floor.\n\n" +
                    "Let’s jingle our way into the festive spirit with fun activities and snacks ✨\n\n" +
                    "Regards\n\n" +
                    "Team HR";

            if (EMAIL_RESULTS) {
                sendEmail(subject, body, from, List.of(secretSanta.email));
            }

            if (PRINT_RESULTS) {
                System.out.println(secretSanta + "  ==>  " + recipient);
            }
        }
    }

    // ================= SAME SENDING METHOD (Notify Service) =================
    private static void sendEmail(String subject, String body, String from, List<String> to) {

        body = body.replaceAll("\n", "<br/>");

        Map<String, Object> emailRequest = new HashMap<>();
        emailRequest.put("notificationType", "EMAIL");

        Map<String, Object> message = new HashMap<>();
        message.put("subject", subject);
        message.put("body", body);
        message.put("from", from);
        message.put("to", to);

        emailRequest.put("message", message);

        HttpEntity<Map<String, Object>> requestEntity =
                new HttpEntity<>(emailRequest, getHeaders());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    EMAIL_API_URL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            System.out.println("Email sent to " + to + " | Response: " + response.getBody());

        } catch (HttpStatusCodeException ex) {
            System.err.println("Failed for " + to + " | Error: " + ex.getResponseBodyAsString());
        }
    }

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = AUTH_USERNAME + ":" + AUTH_PASSWORD;
        String authHeader = "Basic " + new String(Base64.getEncoder().encode(auth.getBytes()));

        headers.set("Authorization", authHeader);
        headers.set("x-basic-auth-client", BASIC_AUTH_CLIENT);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
