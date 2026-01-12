package in.wynk.secret.manager.rules.basic.model;

/**
 * Sample User class.
 */
public class User {
    private String fullName;
    private int userAge;
    private String addressCity;

    public User(String fullName, int userAge, String addressCity) {
        this.fullName = fullName;
        this.userAge = userAge;
        this.addressCity = addressCity;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public int getUserAge() { return userAge; }
    public void setUserAge(int userAge) { this.userAge = userAge; }
    public String getAddressCity() { return addressCity; }
    public void setAddressCity(String addressCity) { this.addressCity = addressCity; }
}
