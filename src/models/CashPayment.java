package models;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.*;

public class CashPayment extends Payment {
    private double discount;

    public CashPayment(String paymentId, String customerId, Date paymentDate, double amount, double discount) {
        super(paymentId, customerId, paymentDate, (int) amount);
        this.discount = discount;
    }

    @Override
    public void processPayment() throws IOException {
        double finalAmount = amount - discount;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String data = paymentId + "," + sdf.format(paymentDate) + "," +
                finalAmount + "," + customerId + ",Cash," + discount;

        savePaymentToFile(data);
        System.out.println("Cash payment completed. Total amount after discount: " + finalAmount);
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
