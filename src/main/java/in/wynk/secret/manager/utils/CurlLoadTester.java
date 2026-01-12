package in.wynk.secret.manager.utils;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CurlLoadTester {

    private static final int TOTAL_REQUESTS = 500;
    private static final double TPS = 50.0;
    private static final String URL = "https://events.streamready.in/tv/events/v1/event?appId=IPTV";
    private static final Gson gson = new Gson();

    private enum TestMode {
        CHECK_BODY,
        CHECK_HEADER
    }

    private static final TestMode CURRENT_MODE = TestMode.CHECK_BODY;

    public static void main(String[] args) throws InterruptedException {
        // Create HttpClient with a thread pool
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .executor(Executors.newFixedThreadPool(20))
                .build();

        // RateLimiter to control TPS
        RateLimiter rateLimiter = RateLimiter.create(TPS);
        ExecutorService executor = Executors.newCachedThreadPool();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger dummyMessageCount = new AtomicInteger(0);
        AtomicInteger cloudFrontCount = new AtomicInteger(0);
        AtomicInteger processedCount = new AtomicInteger(0);

        // JSON Body from the curl command
        String jsonBody = "{\"events\":[{\"appid\":\"WEB\",\"app_session_id\":\"39737142-1768157459243\",\"av\":\"75.0.14\",\"bn\":\"87\",\"brand\":\"chrome\",\"did\":\"1c64aa98-fd75-452b-86f5-b1c4b036dbf3\",\"dname\":\"mac\",\"dt\":\"BROWSER\",\"event_type\":\"trailer_end\",\"lc\":\"mozilla/5.0 (macintosh; intel mac os x 10_15_7) applewebkit/537.36 (khtml, like gecko) chrome/143.0.0.0 safari/537.36\",\"meta\":{\"app_status\":\"\",\"action\":\"trailer_end\",\"source_name\":\"content_detail_page_SONYLIV_VOD_TVSHOW_1700000741\",\"content_name\":\"Shark Tank India\",\"content_id\":\"SONYLIV_VOD_TVSHOW_1700000741\",\"cp_name\":\"SONYLIV_VOD\",\"signal_strength_info\":0.15},\"model\":143,\"nq\":\"0\",\"nt\":\"1\",\"os\":\"macosx\",\"ov\":\"10.15.7\",\"ts\":1768157509893,\"uid\":\"yYwkSd6eqbVZ0UcevDPytiVMA0o2\",\"msisdnNo\":\"\",\"user_agent\":\"mozilla/5.0 (macintosh; intel mac os x 10_15_7) applewebkit/537.36 (khtml, like gecko) chrome/143.0.0.0 safari/537.36\",\"platform\":\"macintel\",\"thanks_app_session_id\":null,\"ab_test\":\"35983:0|93375:0|100609:0|129437:0|52428:0|38359:0|54715:0|76312:0|129342:1|121389:0|65949:1|101095:0|100372:0|61995:0|41069:1|87853:0|117812:1|35593:0|77284:1|58441:2|124204:0|99282:1|95252:1|70595:1|43451:1|55412:1|64054:0|123038:1|56685:1|39555:0|83830:0|99561:0|46902:2|94103:0|51791:2|83847:1|116474:0|90344:1|129297:1|55366:1|120232:1|63638:1|39965:0|120676:1|113632:1|39725:1\",\"city\":\"\",\"country\":\"IN\",\"state\":\"\"},{\"appid\":\"WEB\",\"app_session_id\":\"39737142-1768157459243\",\"av\":\"75.0.14\",\"bn\":\"87\",\"brand\":\"chrome\",\"did\":\"1c64aa98-fd75-452b-86f5-b1c4b036dbf3\",\"dname\":\"mac\",\"dt\":\"BROWSER\",\"event_type\":\"banner_visible\",\"lc\":\"mozilla/5.0 (macintosh; intel mac os x 10_15_7) applewebkit/537.36 (khtml, like gecko) chrome/143.0.0.0 safari/537.36\",\"meta\":{\"app_status\":\"\",\"content_id\":\"SONYLIV_VOD_TVSHOW_1700001090\",\"tile_type\":\"X_INFINITY_BANNER\",\"asset_position\":\"1\",\"deeplink_url_artwork\":\"\",\"tile_id\":\"SONYLIV_VOD_TVSHOW_1700001090\",\"content_name\":\"MasterChef India\",\"is_preview_video\":\"false\",\"rail_type\":\"X_INFINITY_BANNER\",\"source_page\":\"null\",\"rail_position\":\"0\",\"rail_title\":\"Banner\",\"page_id\":\"homepage2\",\"rail_id\":\"686cec081d2e8875a42034f7\",\"package_id\":\"axaut_vniu25981724991437909\",\"source_name\":\"home\",\"collection_id\":\"axaut_vniu25981724991437909\",\"render_reason\":\"686cc968b28b736040f5f029\",\"cp_name\":\"SONYLIV_VOD\",\"banner_tag_type\":\"layout_tag\",\"banner_tile_tag\":\"NEW SEASON\",\"content_Type\":\"TVSHOW\",\"signal_strength_info\":0.15},\"model\":143,\"nq\":\"0\",\"nt\":\"1\",\"os\":\"macosx\",\"ov\":\"10.15.7\",\"ts\":1768157510439,\"uid\":\"yYwkSd6eqbVZ0UcevDPytiVMA0o2\",\"msisdnNo\":\"\",\"user_agent\":\"mozilla/5.0 (macintosh; intel mac os x 10_15_7) applewebkit/537.36 (khtml, like gecko) chrome/143.0.0.0 safari/537.36\",\"platform\":\"macintel\",\"thanks_app_session_id\":null,\"ab_test\":\"35983:0|93375:0|100609:0|129437:0|52428:0|38359:0|54715:0|76312:0|129342:1|121389:0|65949:1|101095:0|100372:0|61995:0|41069:1|87853:0|117812:1|35593:0|77284:1|58441:2|124204:0|99282:1|95252:1|70595:1|43451:1|55412:1|64054:0|123038:1|56685:1|39555:0|83830:0|99561:0|46902:2|94103:0|51791:2|83847:1|116474:0|90344:1|129297:1|55366:1|120232:1|63638:1|39965:0|120676:1|113632:1|39725:1\",\"city\":\"\",\"country\":\"IN\",\"state\":\"\"}],\"id\":\"18f0d3fa-b09b-4d41-b1f8-dbf77fecea26\",\"ts\":1768157518524}";

        System.out.println("Starting load test: " + TOTAL_REQUESTS + " requests at " + TPS + " TPS...");
        System.out.println("Test Mode: " + CURRENT_MODE);

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            rateLimiter.acquire(); // Blocks until a permit is available
            executor.submit(() -> {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(URL))
                            .header("x-atv-utkn", "yYwkSd6eqbVZ0UcevDPytiVMA0o2:Ba3PjjYnbjlpOHDDoZpBLW3cRWQ=")
                            .header("sec-ch-ua-platform", "\"macOS\"")
                            .header("Referer", "https://www.airtelxstream.in/")
                            .header("sec-ch-ua", "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
                            .header("sec-ch-ua-mobile", "?0")
                            .header("x-atv-ab", "35983:0|93375:0|100609:0|129437:0|52428:0|38359:0|54715:0|76312:0|129342:1|121389:0|65949:1|101095:0|100372:0|61995:0|41069:1|87853:0|117812:1|35593:0|77284:1|58441:2|124204:0|99282:1|95252:1|70595:1|43451:1|55412:1|64054:0|123038:1|56685:1|39555:0|83830:0|99561:0|46902:2|94103:0|51791:2|83847:1|116474:0|90344:1|129297:1|55366:1|120232:1|63638:1|39965:0|120676:1|113632:1|39725:1")
                            .header("x-atv-did", "1c64aa98-fd75-452b-86f5-b1c4b036dbf3")
                            .header("x-atv-traceid", "a159182d-1d94-46fd-bd1d-7f91e84d854e")
                            .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36")
                            .header("Accept", "application/json, text/plain, */*")
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        successCount.incrementAndGet();

                        if (CURRENT_MODE == TestMode.CHECK_BODY) {
                            String body = response.body();
                            if (body != null && !body.isEmpty()) {
                                try {
                                    JsonObject jsonResponse = gson.fromJson(body, JsonObject.class);
                                    if (jsonResponse.has("message") && "Dummy message Nginx GATEWAY".equals(jsonResponse.get("message").getAsString())) {
                                        dummyMessageCount.incrementAndGet();
                                    }
                                } catch (JsonSyntaxException e) {
                                    System.err.println("Invalid JSON response: " + body);
                                }
                            }
                        } else if (CURRENT_MODE == TestMode.CHECK_HEADER) {
                            String serverHeader = response.headers().firstValue("Server").orElse("");
                            if ("CloudFront".equals(serverHeader)) {
                                cloudFrontCount.incrementAndGet();
                            }
                        }

                    } else {
                          System.out.println("Status: " + response.statusCode());
                    }
                } catch (Exception e) {
                    System.err.println("Request failed: " + e.getMessage());
                } finally {
                    processedCount.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        try {
            // Wait a bit longer than theoretically needed to account for network latency
            if (!executor.awaitTermination(20, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        System.out.println("Load Test Completed.");
        System.out.println("Total Requests Sent: " + TOTAL_REQUESTS);
        System.out.println("Total 200 OK Responses: " + successCount.get());
        
        if (CURRENT_MODE == TestMode.CHECK_BODY) {
            System.out.println("Total Responses with 'message': 'Dummy message': " + dummyMessageCount.get());
        } else if (CURRENT_MODE == TestMode.CHECK_HEADER) {
            long cfCount = cloudFrontCount.get();
            long totalOk = successCount.get();
            double percentage = totalOk > 0 ? (cfCount * 100.0 / totalOk) : 0.0;
            System.out.println("Total Responses with Server: CloudFront: " + cfCount);
            System.out.println("Percentage of CloudFront responses among 200 OK: " + String.format("%.2f", percentage) + "%");
        }
    }
}
