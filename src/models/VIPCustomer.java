package models;

public class VIPCustomer extends Customer {
    private final double discount = 35.0;

    public VIPCustomer(String customerId, String name, String address, String phone, String email) {
        super(customerId, name, address, phone, email, "VIP");
    }

    @Override
    public double calculateTotalAmount(double orderTotal) {
        if (orderTotal > 300.0) {
            return orderTotal - discount;
        }
        return orderTotal;
    }
}