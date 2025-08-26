package in.wynk.secret.manager.rules.basic.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Sample Customer class.
 */
@Getter
@Setter
public class Customer {
    private String customerName;
    private int clientAge;
    private String locationCity;

    public Customer(String customerName, int clientAge, String locationCity) {
        this.customerName = customerName;
        this.clientAge = clientAge;
        this.locationCity = locationCity;
    }
}