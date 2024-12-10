package models;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Customer {
    protected String customerId;
    public String name;
    protected String address;
    protected String phone;
    protected String email;
    public String type;

    public Customer(String customerId, String name, String address, String phone, String email, String type) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.type = type;
    }

    public static void register(String customerId, String name, String address, String phone, String email, String type)
            throws IOException {
        String filePath = "src/database/customers.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(customerId + "," + name + "," + address + "," + phone + "," + email + "," + type);
            writer.newLine();
        }
    }

    public Order[] getOrderHistory() throws IOException {
        List<Order> orders = new ArrayList<>();
        String filePath = "src/database/orders.txt";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Order currentOrder = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                if (!line.startsWith("M")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5 && parts[4].equals(this.customerId)) {
                        try {
                            Date orderDate = sdf.parse(parts[1]);
                            currentOrder = new Order(parts[0], orderDate, parts[2], this);
                            currentOrder.setTotalAmount(Double.parseDouble(parts[3]));
                            orders.add(currentOrder);
                        } catch (Exception e) {
                            System.out.println("Error parsing order: " + e.getMessage());
                        }
                    } else {
                        currentOrder = null;
                    }
                } else {
                    if (currentOrder != null) {
                        try {
                            String[] itemParts = line.split(",");
                            if (itemParts.length >= 3) {
                                String medId = itemParts[0];
                                int quantity = Integer.parseInt(itemParts[1]);
                                int unitPrice = Integer.parseInt(itemParts[2]);

                                Medicine medicine = null;
                                try {
                                    medicine = Medicine.loadMedicineById("src/database/medicines.txt", medId);
                                } catch (Exception e) {
                                    System.out.println("Error loading medicine: " + medId + " - " + e.getMessage());
                                }

                                if (medicine != null) {
                                    OrderItem item = new OrderItem(quantity, unitPrice, medicine);
                                    currentOrder.addItem(item);
                                } else {
                                    System.out.println("Medicine not found: " + medId);
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error parsing order item line: " + line + " - " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Error reading order history: " + e.getMessage());
        }
        return orders.toArray(new Order[0]);
    }

    public Payment[] getPaymentHistory() throws IOException {
        List<Payment> payments = new ArrayList<>();
        String filePath = "src/database/payments.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[3].equals(this.customerId)) {
                    String paymentType = parts[4];
                    double amount = Double.parseDouble(parts[2]);
                    Date paymentDate = new SimpleDateFormat("yyyy-MM-dd").parse(parts[1]);

                    if (paymentType.equals("Cash")) {
                        double discount = Double.parseDouble(parts[5]);
                        payments.add(new CashPayment(parts[0], this.customerId, paymentDate, amount, discount));
                    } else if (paymentType.equals("CreditCard")) {
                        String cardNumber = parts[5];
                        Date expiryDate = new SimpleDateFormat("yyyy-MM-dd").parse(parts[6]);
                        int cvv = Integer.parseInt(parts[7]);
                        payments.add(new CreditCardPayment(parts[0], this.customerId, paymentDate, (int) amount,
                                cardNumber, expiryDate, cvv));
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Error parsing payment history: " + e.getMessage());
        }
        return payments.toArray(new Payment[0]);
    }

    public void searchMedicine(String keyword) throws IOException {
        String filePath = "src/database/medicines.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(keyword.toLowerCase())) {
                    System.out.println("Found: " + line);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No medicines found for keyword: " + keyword);
            }
        }
    }

    public abstract double calculateTotalAmount(double orderTotal);

    public String getName() {
        return name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
}
