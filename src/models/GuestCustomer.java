package models;

public class GuestCustomer extends Customer {
    private final int shippingFee = 50;

    public GuestCustomer(String customerId, String name, String address, String phone, String email) {
        super(customerId, name, address, phone, email, "Guest");
    }

    @Override
    public double calculateTotalAmount(double orderTotal) {
        return orderTotal + shippingFee;
    }
}