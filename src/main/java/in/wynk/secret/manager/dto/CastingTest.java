package in.wynk.secret.manager.dto;

import java.util.Map;
import lombok.Data;

public class CastingTest {

    public static void main(String[] args) {
        TestClass testClass = new TestClass();
        testClass.setName("Test Name");
        testClass.setValue("Test Value");
        testClass.setType("Test Type");

        process(Map.of("obj", testClass));

        System.out.println("Name: " + testClass.getName());
    }

    private static void process(final Map<String, Object> obj) {
        TestClass testClass = (TestClass) obj.get("obj");
        testClass.setName("Updated Name");
    }


    @Data
    static class  TestClass{
        String name;
        String value;
        String type;
    }
}
