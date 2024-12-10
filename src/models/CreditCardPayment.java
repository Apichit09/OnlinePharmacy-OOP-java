package models;

import java.util.Date;
import java.io.*;
import java.text.SimpleDateFormat;

public class CreditCardPayment extends Payment {
    private String cardNumber;
    private Date expiryDate;
    private int cvv;

    public CreditCardPayment(String paymentId, String customerId, Date paymentDate, int amount,
            String cardNumber, Date expiryDate, int cvv) {
        super(paymentId, customerId, paymentDate, amount);
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    @Override
    public void processPayment() throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String expiryDateStr = sdf.format(expiryDate);

        String data = paymentId + "," + sdf.format(paymentDate) + "," + amount + "," +
                customerId + ",CreditCard," + cardNumber + "," + expiryDateStr + "," + cvv;

        savePaymentToFile(data);
        System.out.println("Credit card payment completed. Total amount: " + amount);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }
}
