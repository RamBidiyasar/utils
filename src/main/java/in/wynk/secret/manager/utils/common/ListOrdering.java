package in.wynk.secret.manager.utils.common;

import java.util.List;

public class ListOrdering {
    public static void main(String[] args) {
        List<String> items = List.of("banana", "apple", "orange", "grape", "kiwi");
        List<String> orderedItems = items.stream().map(String::toUpperCase)
                .toList();

        System.out.println(orderedItems);
    }
}
