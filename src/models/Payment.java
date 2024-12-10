package models;

import java.io.*;
import java.util.Date;

public abstract class Payment {
    public String paymentId;
    public String customerId;
    public Date paymentDate;
    public int amount;

    public Payment(String paymentId, String customerId, Date paymentDate, double amount2) {
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.paymentDate = paymentDate;
        this.amount = (int) amount2;
    }

    public abstract void processPayment() throws IOException;

    protected void savePaymentToFile(String data) throws IOException {
        String filePath = "src/database/payments.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            writer.newLine();
        }
    }
}
