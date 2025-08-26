package in.wynk.secret.manager.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpLoadTest {

    private static final String URL = "http://cms-api.internal.airtel.tv/branch/event/push/";
    private static final String JSON_BODY = """
            {
                  "name": "CLICK",
                  "user_data": {
                    "os": "ANDROID",
                    "os_version": "14",
                    "environment": "FULL_WEB",
                    "platform": "ANDROID_WEB",
                    "limit_ad_tracking": false,
                    "user_agent": "Mozilla/5.0 (Linux; Android 10; REA-NX9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36",
                    "ip": "2401:4900:7dd2:4a6:7cfa:74ff:fed8:e219",
                    "language": "EN",
                    "brand": "Huawei",
                    "model": "REA-NX9",
                    "geo_continent_code": "AS",
                    "geo_region_code": "MH",
                    "geo_region_en": "Maharashtra",
                    "geo_city_code": 1275339,
                    "geo_city_en": "Mumbai",
                    "geo_lat": 19.0748,
                    "geo_lon": 72.8856,
                    "geo_country_code": "IN",
                    "geo_country_en": "India",
                    "browser": "Chrome",
                    "device_type": "UNKNOWN",
                    "opted_in": false,
                    "private_relay": false
                  },
                  "last_attributed_touch_data": {
                    "$android_passive_deepview": "branch_passive_default",
                    "$marketing_title": "free Fab8hrs",
                    "~creation_source": 1,
                    "+click_timestamp": 1728018026,
                    "~referring_browser": "Chrome",
                    "+alias": "fab8hr",
                    "link_id": 1349947905917729084,
                    "~campaign": "Xstream2.0_AVODCONTENT4_Wynk_FAB_AVOD",
                    "~channel": "FAB",
                    "+domain": "mzjjn.app.link",
                    "+url": "https://open.airtelxstream.in/fab8hr?userid=8fHnE-H3PgRvUiKvq0"
                  },
                  "timestamp": 1728018026732
                },
                {
                  "name": "CLICK",
                  "user_data": {
                    "os": "ANDROID",
                    "os_version": "14",
                    "environment": "FULL_WEB",
                    "platform": "ANDROID_WEB",
                    "limit_ad_tracking": false,
                    "user_agent": "Mozilla/5.0 (Linux; Android 10; RMX3998) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36",
                    "ip": "2409:40c4:13c:8ea:8000::",
                    "language": "EN",
                    "brand": "Realme",
                    "model": "RMX3998",
                    "geo_continent_code": "AS",
                    "geo_region_code": "MP",
                    "geo_region_en": "Madhya Pradesh",
                    "geo_city_code": 1275841,
                    "geo_city_en": "Bhopal",
                    "geo_lat": 23.2487,
                    "geo_lon": 77.4066,
                    "geo_country_code": "IN",
                    "geo_country_en": "India",
                    "browser": "Chrome",
                    "device_type": "UNKNOWN",
                    "opted_in": false,
                    "private_relay": false
                  },
                  "last_attributed_touch_data": {
                    "$marketing_title": "free thin xstream - mwebfab",
                    "~creation_source": 1,
                    "+click_timestamp": 1728018026,
                    "~referring_browser": "Chrome",
                    "+alias": "mwebfab",
                    "link_id": 1358020097146496885,
                    "~campaign": "Xstream",
                    "~channel": "FAB",
                    "+domain": "mzjjn.app.link",
                    "+url": "https://open.airtelxstream.in/mwebfab"
                  },
                  "timestamp": 1728018026741
                },
                {
                  "name": "CLICK",
                  "user_data": {
                    "os": "ANDROID",
                    "os_version": "10",
                    "environment": "FULL_WEB",
                    "platform": "ANDROID_WEB",
                    "limit_ad_tracking": false,
                    "user_agent": "Mozilla/5.0 (Linux; Android 10; TECNO KE5k) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Mobile Safari/537.36",
                    "ip": "2401:4900:7046:f5dd:e887:ce35:160c:b507",
                    "language": "EN",
                    "brand": "TECNO",
                    "model": "KE5k",
                    "geo_continent_code": "AS",
                    "geo_region_code": "BR",
                    "geo_region_en": "Bihar",
                    "geo_lat": 25.6005,
                    "geo_lon": 85.1147,
                    "geo_country_code": "IN",
                    "geo_country_en": "India",
                    "browser": "Chrome",
                    "device_type": "UNKNOWN",
                    "opted_in": false,
                    "private_relay": false
                  },
                  "last_attributed_touch_data": {
                    "$marketing_title": "free pushnoti",
                    "~creation_source": 1,
                    "+click_timestamp": 1728018026,
                    "~referring_browser": "Chrome",
                    "+alias": "freepush",
                    "link_id": 1349948106527073726,
                    "~campaign": "Xstream2.0_AVODCONTENT4_Wynk_AuodioAds_AVOD",
                    "~channel": "push notification",
                    "+domain": "mzjjn.app.link",
                    "+url": "https://open.airtelxstream.in/freepush"
                  },
                  "timestamp": 1728018026815
            }
            """;
    private static final int REQUESTS_PER_SECOND = 200;
    private static final int DURATION_MINUTES = 30;

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(REQUESTS_PER_SECOND);

        Runnable sendRequest = () -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JSON_BODY))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::statusCode)
                    .thenAccept(statusCode -> {
                        if (statusCode == 200) {
                            System.out.println("Request succeeded.");
                        } else {
                            System.out.println("Request failed with status: " + statusCode);
                        }
                    })
                    .exceptionally(ex -> {
                        System.out.println("Request failed: " + ex.getMessage());
                        return null;
                    });
        };

        // Schedule the requests at 10 TPS
        long initialDelay = 0;
        long period = 1000 / REQUESTS_PER_SECOND; // 100ms per request to achieve 10 TPS

        executorService.scheduleAtFixedRate(sendRequest, initialDelay, period, TimeUnit.MILLISECONDS);

        // Schedule to stop after 30 minutes
        executorService.schedule(() -> {
            executorService.shutdown();
            System.out.println("Test completed.");
        }, DURATION_MINUTES, TimeUnit.MINUTES);
    }
}
