package in.wynk.secret.manager.utils.shuffle;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ShuffleExample {
    public static void main(String[] args) {
        List<String> items = Arrays.asList("A", "B", "C");
        int trials = 100_000;

        Map<String, Integer> countMap = new HashMap<>();
        for (int i = 0; i < trials; i++) {
            List<String> copy = new ArrayList<>(items);
            Collections.shuffle(copy, ThreadLocalRandom.current());
            String top = copy.get(0);
            countMap.merge(top, 1, Integer::sum);
        }

        // Print percentages
        System.out.println("Results after " + trials + " shuffles:");
        for (String item : items) {
            int count = countMap.getOrDefault(item, 0);
            double percentage = (count * 100.0) / trials;
            System.out.printf("%s -> %d times (%.2f%%)%n", item, count, percentage);
        }
    }
}
