package in.wynk.secret.manager.curl;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;

// Data class for curl structure
class CurlRequest {
    String method;
    String url;
    Map<String, String> headers;
    String body;

    public CurlRequest(String method, String url, Map<String, String> headers, String body) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
    }

    public HttpRequest toHttpRequest() {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                                                 .uri(URI.create(url))
                                                 .timeout(Duration.ofSeconds(10));

        headers.forEach(builder::header);

        if (body != null) {
            builder.method(method, HttpRequest.BodyPublishers.ofString(body));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return builder.build();
    }
}
