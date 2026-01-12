package in.wynk.secret.manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
public class UrlCheckerController {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @PostMapping("/check-urls")
    public ResponseEntity<UrlCheckSummary> checkUrls(@RequestBody List<String> urls) {
        List<Future<UrlCheckResult>> futures = urls.stream()
                .map(url -> executorService.submit(() -> checkUrl(url)))
                .toList();

        List<UrlCheckResult> results = futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                return new UrlCheckResult("", false, "Exception during execution: " + e.getMessage());
            }
        }).collect(Collectors.toList());

        int successCount = (int) results.stream().filter(UrlCheckResult::isSuccess).count();
        int failureCount = results.size() - successCount;

        List<String> failedUrls = results.stream()
                .filter(result -> !result.isSuccess())
                .map(UrlCheckResult::getUrl)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new UrlCheckSummary(successCount, failureCount, failedUrls, results));
    }

    private UrlCheckResult checkUrl(String url) {
        UrlCheckResult result = new UrlCheckResult(url);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            result.setSuccess(true);
            result.setMessage("Connection successful, response code: " + responseCode);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Exception: " + e.getClass().getName() + " - " + e.getMessage() + ", here is detailed exception: " + e);
        }

        return result;
    }

    private static class UrlCheckResult {
        private String url;
        private boolean success;
        private String message;

        public UrlCheckResult() {}

        public UrlCheckResult(String url) {
            this.url = url;
        }

        public UrlCheckResult(String url, boolean success, String message) {
            this.url = url;
            this.success = success;
            this.message = message;
        }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    private static class UrlCheckSummary {
        private int successCount;
        private int failureCount;
        private List<String> failedUrls;
        private List<UrlCheckResult> results;

        public UrlCheckSummary() {}

        public UrlCheckSummary(int successCount, int failureCount, List<String> failedUrls, List<UrlCheckResult> results) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.failedUrls = failedUrls;
            this.results = results;
        }

        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public List<String> getFailedUrls() { return failedUrls; }
        public void setFailedUrls(List<String> failedUrls) { this.failedUrls = failedUrls; }
        public List<UrlCheckResult> getResults() { return results; }
        public void setResults(List<UrlCheckResult> results) { this.results = results; }
    }
}