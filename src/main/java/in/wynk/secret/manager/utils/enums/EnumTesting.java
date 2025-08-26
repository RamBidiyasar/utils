package in.wynk.secret.manager.utils.enums;

public class EnumTesting {
    public static void main(String[] args) {
        String layoutType = "GRID";
        System.out.println("Layout Type: " + LayoutType.valueOf(layoutType));

        String test = null;

        switch (test){
            case "GRID" -> System.out.println("This is a grid layout");
            case "LIST" -> System.out.println("This is a list layout");
            case "CAROUSEL" -> System.out.println("This is a carousel layout");
            case "SLIDER" -> System.out.println("This is a slider layout");
            case "TABS" -> System.out.println("This is a tabs layout");
            case "ACCORDION" -> System.out.println("This is an accordion layout");
            case "MODAL" -> System.out.println("This is a modal layout");
            case "POPUP" -> System.out.println("This is a popup layout");
            case "FULL_SCREEN" -> System.out.println("This is a full screen layout");
            default -> System.out.println("Unknown layout type");
        }
    }
}
