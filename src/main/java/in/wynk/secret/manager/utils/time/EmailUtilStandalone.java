package in.wynk.secret.manager.utils.time;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailUtilStandalone {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String EMAIL_API_URL =
            "http://notification-management.internal.airtel.tv/notification/v1/send/instant";

    private static final String AUTH_USERNAME = "9rqpmxtra2uq3321j";
    private static final String AUTH_PASSWORD = "k61g5humgga7a6orkchn7ezmz6yjgfx";
    private static final String BASIC_AUTH_CLIENT = "notify-manager-msp";

    public static void main(String[] args) {

        String subject = "PSVM Email Test";
        String body = "Hello Team,\nThis email is sent from a single Java class.";
        String from = "streamready@airtel.com";
        List<String> to = List.of("ram.bidiyasar@airtel.com");

        try {
            sendEmail(subject, body, from, to);
            System.out.println("Email sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send email");
        }
    }

    public static void sendEmail(String subject, String body, String from, List<String> to) throws Exception {

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

            System.out.println("Response: " + response.getBody());

        } catch (HttpStatusCodeException ex) {
            System.err.println("Error Response: " + ex.getResponseBodyAsString());
            throw ex;
        }
    }

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = AUTH_USERNAME + ":" + AUTH_PASSWORD;
        String authHeader = "Basic " + new String(Base64Utils.encode(auth.getBytes()));

        headers.set("Authorization", authHeader);
        headers.set("x-basic-auth-client", BASIC_AUTH_CLIENT);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
