package in.wynk.secret.manager.cdn.akamai;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class ConcurrentRequests {
    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://iptv-live-akcdn.streamready.in/live/med1/aaj_tak/vdashd/aaj_tak.mpd"))
                .build();

        CompletableFuture<?>[] futures = IntStream.range(0, 500)
                .mapToObj(i -> client.sendAsync(request, HttpResponse.BodyHandlers.discarding()))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
        System.out.println("All requests sent!");
    }
}
