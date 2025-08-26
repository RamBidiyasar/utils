package in.wynk.secret.manager.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BaseURLFinder {

    // Function to recursively get all .java and .properties files in a directory
    private static Set<File> listRelevantFiles(final File folder) {
        Set<File> fileSet = new HashSet<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                fileSet.addAll(listRelevantFiles(fileEntry));
            } else if (fileEntry.getName().endsWith(".yml")) {
                fileSet.add(fileEntry);
            }
//            } else if (fileEntry.getName().endsWith(".java") || fileEntry.getName().endsWith(".properties")) {
//                fileSet.add(fileEntry);
//            }
        }
        return fileSet;
    }

    // Function to extract base URLs from a file
    private static Set<String> extractBaseUrlsFromFile(File file) {
        Set<String> baseUrls = new HashSet<>();
        String content = "";

        try {
            content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Regular expression to find URLs starting with "http://" or "https://"
        String regex = "(http|https)://\\S+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String fullUrl = matcher.group();
            String baseUrl = getBaseUrl(fullUrl);
            baseUrls.add(baseUrl);
        }

        return baseUrls;
    }

    // Function to extract the base URL from a full URL
    private static String getBaseUrl(String url) {
        int endIndex = url.indexOf("/", 8); // 8 to skip "http://" or "https://"
        if (endIndex != -1) {
            return url.substring(0, endIndex);
        } else {
            return url;
        }
    }

    // Function to find all unique base URLs in the project directory
    public static Set<String> findBaseUrlsInProject(String projectDirectoryPath) {
        File projectDir = new File(projectDirectoryPath);
        Set<String> allBaseUrls = new HashSet<>();

        if (projectDir.exists() && projectDir.isDirectory()) {
            Set<File> files = listRelevantFiles(projectDir);

            for (File file : files) {
                allBaseUrls.addAll(extractBaseUrlsFromFile(file));
            }
        } else {
            System.out.println("The specified path is not a valid directory.");
        }

        return allBaseUrls;
    }

    public static void main(String[] args) {
        String projectPath = "/Users/B0296099/Documents/BE_REPOS/xstream_backend_package"; // Replace with your project directory path
        Set<String> baseUrls = findBaseUrlsInProject(projectPath);

        System.out.println("Found Base URLs:");

        List<String> list = baseUrls.stream().toList();

        String formattedList = list.stream()
                .map(url -> "\"" + url + "\"")  // Add quotes around each string
                .collect(Collectors.joining(", ", "[", "]")); // Join with commas and add square brackets

        // Print the formatted list
        System.out.println(formattedList);
    }
}
