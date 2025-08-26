package in.wynk.secret.manager.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class JsonFileCreator {

    public static void main(String[] args) {
        String urlString = "http://log-analyzer.internal.airtel.tv/ingestiondump/fetchFile?bucket=ingestiondump&file=prod/STAGE/09-01-2025/01:10:00PM/STAGE_SHOWS_2280.json";
        try {
            // Extract filename from URL
            String filename = extractFilenameFromUrl(urlString);
            
            // Fetch JSON response from the URL
            String jsonResponse = fetchJsonResponse(urlString);
            
            // Write the response to a file
            writeFile(jsonResponse, filename);

            System.out.println("File created successfully: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String extractFilenameFromUrl(String urlString) {
        return urlString.substring(urlString.lastIndexOf("/") + 1);
    }

    private static String fetchJsonResponse(String urlString) throws IOException {
        StringBuilder response = new StringBuilder();
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8)) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
        }
        return response.toString();
    }

    private static void writeFile(String content, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        }
    }
}
