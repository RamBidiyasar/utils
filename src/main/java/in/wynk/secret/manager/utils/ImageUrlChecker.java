package in.wynk.secret.manager.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImageUrlChecker {

    private static final String PREFIX = "https://image-gcp.airtel.tv";
    private static final int THREADS = 70; // To achieve 50 TPS
    private static int processedCount = 0;

    public static void main(String[] args) {
        String filePath = "/Users/B0296099/Documents/Learning/Secret Manager/fancode.txt"; // Path to the input txt file
        String successFilePath = "/Users/B0296099/Documents/Learning/Secret Manager/success.txt"; // Path to store successful hits
        String failureFilePath = "/Users/B0296099/Documents/Learning/Secret Manager/failure.txt"; // Path to store failed hits
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath));
             BufferedWriter successWriter = new BufferedWriter(new FileWriter(successFilePath));
             BufferedWriter failureWriter = new BufferedWriter(new FileWriter(failureFilePath))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+"); // Split the line to get the image path
                if (parts.length > 3) {
                    String imagePath = parts[4];
                    String urlToHit = PREFIX + "/" + imagePath;

                    // Submit tasks to the executor service to make the HTTP requests
                    executorService.submit(() -> checkImageUrl(urlToHit, successWriter, failureWriter));

                    // Print progress for each processed image
                 //   System.out.println("Processing image: " + imagePath);

                    // Sleep to maintain 50 TPS (1000 ms / 50 = 20 ms delay between requests)
                 // Thread.sleep(10);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            try {
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void checkImageUrl(String urlStr, BufferedWriter successWriter, BufferedWriter failureWriter) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            int responseCode = connection.getResponseCode();

            // Write to success or failure file based on response code
            //synchronized (successWriter) {
                if (responseCode == 200) {
                   // successWriter.write(urlStr + " - Success\n");
                } else {
                    System.out.println(urlStr + " :  " + responseCode);
                    //failureWriter.write(urlStr + " - Failed with response code: " + responseCode + "\n");
                }
//            }

            // Print progress update to console
            //incrementProcessedCount();
            Thread.sleep(20);

            connection.disconnect();
        } catch (Exception e) {
//            try {
                //synchronized (failureWriter) {
                System.out.println(urlStr + " :  " +  e.getMessage());
                  //  failureWriter.write(urlStr + " - Error: " + e.getMessage() + "\n");
                //}
//            } catch (IOException ioException) {
//                System.out.println(ioException.getMessage());
//                ioException.printStackTrace();
//            }
        }
    }

    private static synchronized void incrementProcessedCount() {
        processedCount++;
        System.out.println("Processed images count: " + processedCount);
    }
}
