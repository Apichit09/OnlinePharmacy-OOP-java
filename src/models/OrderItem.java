package models;

public class OrderItem {
    private int quantity;
    private int unitPrice;
    private Medicine medicine;

    public OrderItem(int quantity, int unitPrice, Medicine medicine) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.medicine = medicine;
    }

    public OrderItem(Medicine medicine2, int quantity2) {
    }

    public double calculateSubtotal() {
        return quantity * unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public Medicine getMedicine() {
        return medicine;
    }
}
