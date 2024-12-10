package models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Staff {
    @SuppressWarnings("unused")
    private String staffId;
    private String name;
    @SuppressWarnings("unused")
    private String phone;
    @SuppressWarnings("unused")
    private String email;

    public Staff(String staffId, String name, String phone, String email) {
        this.staffId = staffId;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public void addMedicine(String medicineId, String name, String description, int unitPrice, int stockQuantity)
            throws IOException {
        String filePath = "src/database/medicines.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(String.join(",", medicineId, name, description, String.valueOf(unitPrice),
                    String.valueOf(stockQuantity)));
            writer.newLine();
        }
    }

    public void updateMedicine(String medicineId, String updatedName, String updatedDescription, int updatedUnitPrice,
            int updatedStockQuantity) throws IOException {
        String filePath = "src/database/medicines.txt";
        StringBuilder updatedContent = new StringBuilder();
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(medicineId)) {
                    updatedContent.append(String.join(",", medicineId, updatedName, updatedDescription,
                            String.valueOf(updatedUnitPrice), String.valueOf(updatedStockQuantity)))
                            .append(System.lineSeparator());
                    updated = true;
                } else {
                    updatedContent.append(line).append(System.lineSeparator());
                }
            }
        }

        if (updated) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(updatedContent.toString());
            }
        }
    }

    public void deleteMedicine(String medicineId) throws IOException {
        String filePath = "src/database/medicines.txt";
        StringBuilder updatedContent = new StringBuilder();
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (!parts[0].equals(medicineId)) {
                    updatedContent.append(line).append(System.lineSeparator());
                } else {
                    deleted = true;
                }
            }
        }

        if (deleted) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(updatedContent.toString());
            }
        }
    }

    public List<String> listAllMedicines() throws IOException {
        List<String> medicines = new ArrayList<>();
        String filePath = "src/database/medicines.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    medicines.add(String.format("ID: %s, Name: %s, Description: %s, Unit Price: %s, Stock: %s",
                            parts[0], parts[1], parts[2], parts[3], parts[4]));
                }
            }
        }
        return medicines;
    }

    public List<String> searchMedicineByName(String nameKeyword) throws IOException {
        List<String> results = new ArrayList<>();
        String filePath = "src/database/medicines.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(nameKeyword.toLowerCase())) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        results.add(String.format("ID: %s, Name: %s, Description: %s, Unit Price: %s, Stock: %s",
                                parts[0], parts[1], parts[2], parts[3], parts[4]));
                    }
                }
            }
        }
        return results;
    }

    public List<Order> getPendingOrders() throws IOException {
        List<Order> pendingOrders = new ArrayList<>();
        String ordersFilePath = "src/database/orders.txt";
        String customersFilePath = "src/database/customers.txt";
        String medicinesFilePath = "src/database/medicines.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(ordersFilePath))) {
            String line;
            Order currentOrder = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;

                if (!line.startsWith("M")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5 && parts[2].equalsIgnoreCase("Pending")) {
                        String customerId = parts[4];

                        Customer customer = loadCustomerById(customersFilePath, customerId);

                        currentOrder = new Order(parts[0], null, parts[2], customer);
                        currentOrder.setTotalAmount(Double.parseDouble(parts[3]));
                        pendingOrders.add(currentOrder);
                    } else {
                        currentOrder = null;
                    }
                } else if (currentOrder != null) {
                    String[] itemParts = line.split(",");
                    if (itemParts.length >= 3) {
                        String medId = itemParts[0];
                        int quantity = Integer.parseInt(itemParts[1]);
                        int unitPrice = Integer.parseInt(itemParts[2]);

                        Medicine medicine = loadMedicineById(medicinesFilePath, medId);
                        OrderItem item = new OrderItem(quantity, unitPrice, medicine);
                        currentOrder.addItem(item);
                    }
                }
            }
        }

        return pendingOrders;
    }

    public String processOrder(Order selectedOrder) throws IOException {
        if (selectedOrder == null) {
            return "Order not found or already processed.";
        }

        StringBuilder details = new StringBuilder();

        details.append("Processing Order:\n");
        details.append("Order ID: ").append(selectedOrder.getOrderId()).append("\n");
        details.append("Status: ").append(selectedOrder.getOrderStatus()).append("\n");
        details.append(String.format("Total Amount (Before Shipping): %.2f%n", selectedOrder.getTotalAmount()));

        Customer customer = selectedOrder.getCustomer();
        if (customer != null) {
            details.append("Customer Name: ").append(customer.getName()).append("\n");
            details.append("Address: ").append(customer.getAddress()).append("\n");
            details.append("Phone: ").append(customer.getPhone()).append("\n");
        } else {
            details.append("Customer details not found.\n");
        }

        details.append("\nOrder Items:\n");
        details.append(String.format("%-12s %-20s %-10s %-10s %-10s%n", "Medicine ID", "Name", "Quantity", "Unit Price",
                "Subtotal"));
        details.append("----------------------------------------------------------\n");
        for (OrderItem item : selectedOrder.getItems()) {
            Medicine med = item.getMedicine();
            if (med != null) {
                double subtotal = item.calculateSubtotal();
                details.append(String.format("%-12s %-20s %-10d %-10d %-10.2f%n",
                        med.getMedicineId(), med.getName(), item.getQuantity(), item.getUnitPrice(), subtotal));
            }
        }

        selectedOrder.setOrderStatus("Confirmed");

        updateOrderStatusInFile(selectedOrder);

        details.append("\nOrder has been confirmed successfully.");
        return details.toString();
    }

    private void updateOrderStatusInFile(Order order) throws IOException {
        String filePath = "src/database/orders.txt";
        StringBuilder updatedContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(order.getOrderId())) {
                    parts[2] = order.getOrderStatus();
                    updatedContent.append(String.join(",", parts)).append(System.lineSeparator());
                } else {
                    updatedContent.append(line).append(System.lineSeparator());
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(updatedContent.toString());
        }
    }

    private Customer loadCustomerById(String filePath, String customerId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(customerId)) {
                    return customerId.startsWith("CUS")
                            ? new VIPCustomer(parts[0], parts[1], parts[2], parts[3], parts[4])
                            : new GuestCustomer(parts[0], parts[1], parts[2], parts[3], parts[4]);
                }
            }
        }
        return new GuestCustomer(customerId, "Unknown", "Unknown", "Unknown", "Unknown");
    }

    private Medicine loadMedicineById(String filePath, String medicineId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(medicineId)) {
                    return new Medicine(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4]));
                }
            }
        }
        return new Medicine(medicineId, "Unknown", "Unknown", 0, 0);
    }

    public String getName() {
        return name;
    }
}
