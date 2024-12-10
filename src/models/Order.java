package models;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private String orderId;
    private Date orderDate;
    private String orderStatus;
    private double totalAmount;
    private Customer customer;
    private List<OrderItem> items;

    public Order(String orderId, Date orderDate, String orderStatus, Customer customer) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.customer = customer;
        this.items = new ArrayList<>();
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
    }

    public double calculateTotalAmount() {
        double orderTotal = 0.0;
        for (OrderItem item : items) {
            orderTotal += item.calculateSubtotal();
        }
        totalAmount = customer.calculateTotalAmount(orderTotal);
        return totalAmount;
    }

    public void saveToFile() throws IOException {
        calculateTotalAmount();
        String filePath = "src/database/orders.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = sdf.format(orderDate);

            writer.write(orderId + "," + dateStr + "," + orderStatus + "," +
                    totalAmount + "," + customer.customerId);
            writer.newLine();

            for (OrderItem item : items) {
                writer.write("   " + item.getMedicine().getMedicineId() + "," +
                        item.getQuantity() + "," + item.getUnitPrice());
                writer.newLine();
            }
        }
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String status) {
        this.orderStatus = status;
    }

    public Customer getCustomer() {
        return this.customer;
    }
}
