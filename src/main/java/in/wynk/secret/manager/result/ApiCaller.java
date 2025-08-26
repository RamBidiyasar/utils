package in.wynk.secret.manager.result;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiCaller {
    private static final String URL = "https://interbiharboard.com/Result.aspx";
    private static final int THREADS = 50; // Adjust based on system capability
    private static final int REQUESTS_PER_SECOND = 200;
    private static final long REQUEST_INTERVAL = 1000 / REQUESTS_PER_SECOND; // Milliseconds
    
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        HttpClient client = HttpClient.newHttpClient();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("results.csv"))) {
            writer.println("Roll Number,Name,Father's Name,Aggregate Marks, Division, Faculty");
            
            for (int password = 25010003; password <= 25010200; password++) {
                final int currentPassword = password;
                executor.execute(() -> {
                    try {
                        String response = sendRequest(client, currentPassword);
                        String[] extractedData = extractData(response);
                        if (extractedData != null) {
                            System.out.println(currentPassword + "," + extractedData[0] + "," + extractedData[1] + "," + extractedData[2] + "," +  extractedData[3]+ ","+ extractedData[4]);
                            synchronized (writer) {
                                writer.println(currentPassword + "," + extractedData[0] + "," + extractedData[1] + "," + extractedData[2] + "," +  extractedData[3]+ ","+ extractedData[4]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                Thread.sleep(REQUEST_INTERVAL);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    private static String sendRequest(HttpClient client, int password) throws IOException, InterruptedException {
        String formData = "__EVENTTARGET=" + encode("") +
                "&__EVENTARGUMENT=" + encode("") +
                "&__VIEWSTATE=" + encode("/wEPDwUJNzU4MzIzMDY2ZGRW8tQXWVV81UJLmkDxCDveFR/GA/Ml/iVA7QLsA8NZaA==") +
                "&__VIEWSTATEGENERATOR=" + encode("297DB184") +
                "&__EVENTVALIDATION=" + encode("/wEdAAS8vn+T6DXqVwmf5ns8lqgstqlIn+GhjYSB46TZvIEz9+4sciJO3Hoc68xTFtZGQEh0OWarNtHZvueomBCnXS0aPOUeFSCPQeyMWKGWNcRseOXzvc9/3YXyuwSnXVm4y5Y=") +
                "&mobile=" + encode("24054") +
                "&password=" + encode(String.valueOf(password)) +
                "&btn_login=" + encode("View Result");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("content-type", "application/x-www-form-urlencoded")
                .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static String[] extractData(String html) {
        Pattern namePattern = Pattern.compile("Student's Name</td>\\s*<td[^>]*>(.*?)</td>");
        Pattern facultyPattern = Pattern.compile("Faculty</td>\\s*<td[^>]*>(.*?)</td>");
        Pattern fatherPattern = Pattern.compile("Father's Name</td>\\s*<td[^>]*>(.*?)</td>");
        Pattern aggregatePattern = Pattern.compile("Aggregate Marks:</td>\\s*<td[^>]*>(\\d+)");
        Pattern divisionPattern = Pattern.compile("Result/Division:</td>\\s*<td[^>]*>(.*?)</td>");


        Matcher nameMatcher = namePattern.matcher(html);
        Matcher fatherMatcher = fatherPattern.matcher(html);
        Matcher aggregateMatcher = aggregatePattern.matcher(html);
        Matcher divisionMatcher = divisionPattern.matcher(html);
        Matcher facultyMatcher = facultyPattern.matcher(html);


        if (nameMatcher.find() && fatherMatcher.find() && aggregateMatcher.find() && divisionMatcher.find() && facultyMatcher.find()) {
            return new String[]{nameMatcher.group(1), fatherMatcher.group(1), aggregateMatcher.group(1), divisionMatcher.group(1), facultyMatcher.group(1)};
        }
        return null;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}