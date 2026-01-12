package in.wynk.secret.manager.rules.basic.model;

/**
 * Sample Customer class.
 */
public class Customer {
    private String customerName;
    private int clientAge;
    private String locationCity;

    public Customer(String customerName, int clientAge, String locationCity) {
        this.customerName = customerName;
        this.clientAge = clientAge;
        this.locationCity = locationCity;
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public int getClientAge() { return clientAge; }
    public void setClientAge(int clientAge) { this.clientAge = clientAge; }
    public String getLocationCity() { return locationCity; }
    public void setLocationCity(String locationCity) { this.locationCity = locationCity; }
}
