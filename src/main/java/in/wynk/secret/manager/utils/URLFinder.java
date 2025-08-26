package in.wynk.secret.manager.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLFinder {

    // Function to recursively get all files in a directory
    private static List<File> listFilesForFolder(final File folder) {
        List<File> fileList = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                fileList.addAll(listFilesForFolder(fileEntry));
            } else {
                fileList.add(fileEntry);
            }
        }
        return fileList;
    }

    // Function to extract URLs from a file
    private static List<String> extractUrlsFromFile(File file) {
        List<String> urls = new ArrayList<>();
        String content = "";

        try {
            content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Regular expression to find URLs starting with "https://"
        String regex = "https://\\S+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            urls.add(matcher.group());
        }

        return urls;
    }

    // Function to find all URLs in the project directory
    public static List<String> findUrlsInProject(String projectDirectoryPath) {
        File projectDir = new File(projectDirectoryPath);
        List<String> allUrls = new ArrayList<>();

        if (projectDir.exists() && projectDir.isDirectory()) {
            List<File> files = listFilesForFolder(projectDir);

            for (File file : files) {
                allUrls.addAll(extractUrlsFromFile(file));
            }
        } else {
            System.out.println("The specified path is not a valid directory.");
        }

        return allUrls;
    }

    public static void main(String[] args) {
        String projectPath = "/Users/B0296099/Documents/BE_REPOS/xstream_backend_package"; // Replace with your project directory path
        List<String> urls = findUrlsInProject(projectPath);

        System.out.println("Found URLs:");
        for (String url : urls) {
            System.out.println(url);
        }
    }
}

