package in.wynk.secret.manager.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CpContentIdValidator {

    private static final Pattern PATTERN = Pattern.compile("^[\\p{L}\\p{N} _.,:?/\\-…]+$");

    public static void main(String[] args) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<CpContent> contentList = objectMapper.readValue(
                    new File("/Users/B0296099/Documents/BE_REPOS/utils/src/main/java/in/wynk/secret/manager/utils/cp_content_ids.json"),
                    new TypeReference<List<CpContent>>() {}
            );

            int total = contentList.size();
            int validCount = 0;
            int invalidCount = 0;

            List<String> invalidEntries = new ArrayList<>();

            for (int i = 0; i < contentList.size(); i++) {
                CpContent content = contentList.get(i);
                String value = content.getCpContentId();

                if (value == null || !PATTERN.matcher(value).matches()) {
                    invalidCount++;
                    invalidEntries.add("Index: " + i + ", Value: " + value + ", cp :" + content.getCp());
                } else {
                    validCount++;
                }
            }

            // ===== Summary =====
            System.out.println("\n========= VALIDATION SUMMARY =========");
            System.out.println("Total Records  : " + total);
            System.out.println("Valid Records  : " + validCount);
            System.out.println("Invalid Records: " + invalidCount);

            if (!invalidEntries.isEmpty()) {
                System.out.println("\nInvalid Entries:");
                invalidEntries.forEach(System.out::println);
            } else {
                System.out.println("\nAll entries are valid ✅");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CpContent {
        private String cpContentId;
        private String cp;

        public String getCp() {
            return cp;
        }

        public String getCpContentId() {
            return cpContentId;
        }

        public void setCp(String cp) {
            this.cp = cp;
        }

        public void setCpContentId(String cpContentId) {
            this.cpContentId = cpContentId;
        }
    }
}
