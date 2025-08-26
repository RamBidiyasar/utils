package in.wynk.secret.manager.cdn.qwilt;

import java.net.HttpURLConnection;
import java.net.URL;



import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.time.Instant;
import java.time.Duration;

public class MpdChecker {

    private static String lastPublishTime = null;
    private static Instant lastChangeTime = null;

    public static void main(String[] args) {
        String url = "https://iptv-prd-new-main.dlt.qwilted-cds.cqloud.com/live/med15/sony_max_hd/vdashhd/sony_max_hd.mpd";

//        String url = "https://iptv-prd-new-main.streamready.in/live/med15/sony_max_hd/vdashhd/sony_max_hd.mpd";

        Runnable task = () -> {
            try {
                String currentPublishTime = fetchPublishTime(url);
                if (currentPublishTime != null && !currentPublishTime.equals(lastPublishTime)) {
                    Instant now = Instant.now();
                    if (lastChangeTime != null) {
                        long interval = Duration.between(lastChangeTime, now).toMillis();
                        System.out.println("Publish time changed! Interval since last change: " + interval + " ms");
                    }
                    lastPublishTime = currentPublishTime;
                    lastChangeTime = now;
                }
            } catch (Exception e) {
                System.err.println("Error fetching MPD: " + e.getMessage());
            }
        };

        // Schedule the task at regular intervals (every 2 seconds)
        int intervalMillis = 10;
        while (true) {
            task.run();
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted: " + e.getMessage());
                break;
            }
        }
    }

    private static String fetchPublishTime(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed to fetch MPD: HTTP error code " + connection.getResponseCode());
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(connection.getInputStream());

        Node publishTimeNode = document.getDocumentElement().getAttributes().getNamedItem("publishTime");
        return (publishTimeNode != null) ? publishTimeNode.getNodeValue() : null;
    }
}
