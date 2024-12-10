package models;

import java.io.*;

public class Medicine {
    private String medicineId;
    private String name;
    private String description;
    private int unitPrice;
    private int stockQuantity;

    public Medicine(String medicineId, String name, String description, int unitPrice, int stockQuantity) {
        this.medicineId = medicineId;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
    }

    public boolean checkStock(int quantity) {
        return stockQuantity >= quantity;
    }

    public void reduceStock(int quantity) {
        if (checkStock(quantity)) {
            stockQuantity -= quantity;
        } else {
            throw new IllegalArgumentException("Insufficient stock for medicine: " + name);
        }
    }

    public String getMedicineId() {
        return medicineId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public static Medicine loadMedicineById(String filePath, String medicineId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && parts[0].equals(medicineId)) {
                    return new Medicine(medicineId, parts[1], parts[2],
                            Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                }
            }
        }
        throw new IllegalArgumentException("Medicine with ID " + medicineId + " not found.");
    }

    public static void updateMedicineStock(String filePath, Medicine medicine) throws IOException {
        File tempFile = new File(filePath + ".tmp");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(medicine.getMedicineId())) {
                    writer.write(String.format("%s,%s,%s,%d,%d",
                            medicine.getMedicineId(), medicine.getName(),
                            medicine.getDescription(), medicine.getUnitPrice(),
                            medicine.getStockQuantity()));
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        }

        if (!tempFile.renameTo(new File(filePath))) {
            throw new IOException("Failed to update the medicine file.");
        }
    }
}
