package in.wynk.secret.manager.utils.rate.limitter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleSyncRequest {

    public static void main(String[] args) {
        try {

            ExecutorService executor = Executors.newFixedThreadPool(100);

            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            String json = """
                    {"recents":{"add":[],"remove":[]},"favourites":{"add":[{"contentId":"TIMESPLAY_MOVIE_movie_245","lastUpdatedTimeStamp":1749561071101,"lastWatchedPosition":0,"langId":"","subtitle":""},{"contentId":"TIMESPLAY_MOVIE_movie_240","lastUpdatedTimeStamp":1749561072669,"lastWatchedPosition":0,"langId":"","subtitle":""}],"remove":[]}}
                    """;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://sync-preprod.wynk.in/v4/user/content/sync?appId=WEB&diff=true"))
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .header("x-atv-utkn", "gUlq9_NWl24XoyJGT0:lnYQ7YEdzD1ZAsXiE92sQjAntQ0=")
                    .header("sec-ch-ua-platform", "macOS")
                    .header("Referer", "https://preprod.airtelxstream.in/")
                    .header("sec-ch-ua", "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"")
                    .header("sec-ch-ua-mobile", "?0")
                    .header("x-atv-ab", "57924:1|36795:1|63817:1|94798:1|31112:1|104526:1|35626:0|60640:0|41993:0|63696:1")
                    .header("x-atv-did", "89bc163f-e3cd-4e79-949b-1b3d285b0ded|BROWSER|WEBOS|10.15.7|74|74.2.5|mac|mac")
                    .header("x-atv-traceid", "fb1391c6-6785-4cff-9c77-8a465a10c067")
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36")
                    .header("Accept", "application/json, text/plain, */*")
                    .build();

            for (int i = 0; i < 1000000; i++) {
                executor.submit(() -> {
                    HttpResponse<String> response;
                    try {
                        response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Status: " + response.statusCode());
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
