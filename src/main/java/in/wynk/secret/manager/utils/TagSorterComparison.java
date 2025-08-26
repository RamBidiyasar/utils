package in.wynk.secret.manager.utils;

import java.util.*;

public class TagSorterComparison {

    // Original implementation using indexOf
    public static List<String> originalMethod(Set<String> displayTagsAsSet, List<String> updatedTagsHierarchy) {
        List<String> displayTagList = new ArrayList<>(displayTagsAsSet);
        displayTagList.sort(Comparator.comparingInt(tag -> {
            int index = updatedTagsHierarchy.indexOf(tag);
            return (index == -1) ? Integer.MAX_VALUE : index;
        }));
        return displayTagList;
    }

    // Optimized implementation using a precomputed index map
    public static List<String> optimizedMethod(Set<String> displayTagsAsSet, List<String> updatedTagsHierarchy) {
        Map<String, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < updatedTagsHierarchy.size(); i++) {
            indexMap.put(updatedTagsHierarchy.get(i), i);
        }

        List<String> displayTagList = new ArrayList<>(displayTagsAsSet);
        displayTagList.sort(Comparator.comparingInt(tag -> indexMap.getOrDefault(tag, Integer.MAX_VALUE)));
        return displayTagList;
    }

    public static void main(String[] args) {
        // Sample data
        Set<String> displayTagsAsSet = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            displayTagsAsSet.add("tag" + i);
        }

        List<String> updatedTagsHierarchy = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            updatedTagsHierarchy.add("tag" + i); // first 100 intersect with displayTagsAsSet
        }

        // Test original method
        long start1 = System.nanoTime();
        List<String> result1 = originalMethod(displayTagsAsSet, updatedTagsHierarchy);
        long end1 = System.nanoTime();

        // Test optimized method
        long start2 = System.nanoTime();
        List<String> result2 = optimizedMethod(displayTagsAsSet, updatedTagsHierarchy);
        long end2 = System.nanoTime();

        // Output time results
        System.out.println("Original Method Time (ms): " + (end1 - start1) / 1_000_000);
        System.out.println("Optimized Method Time (ms): " + (end2 - start2) / 1_000_000);

        // Optional: Check results are same (order-wise)
        boolean sameOrder = result1.equals(result2);
        System.out.println("Both methods produce same result: " + sameOrder);
    }
}
