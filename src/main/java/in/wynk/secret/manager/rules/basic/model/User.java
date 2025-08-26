package in.wynk.secret.manager.rules.basic.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Sample User class.
 */
@Getter
@Setter
public class User {
    private String fullName;
    private int userAge;
    private String addressCity;

    public User(String fullName, int userAge, String addressCity) {
        this.fullName = fullName;
        this.userAge = userAge;
        this.addressCity = addressCity;
    }
}