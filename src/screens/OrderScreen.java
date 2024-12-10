package screens;

import models.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class OrderScreen extends JFrame {

    private DefaultListModel<OrderItem> cartModel;
    private JList<OrderItem> cartList;
    private JLabel subtotalLabel;
    private Font kanitFont, kanitBoldFont;
    private Color primaryColor = new Color(41, 128, 185);  
    private Color secondaryColor = new Color(236, 240, 241); 
    private Color accentColor = new Color(231, 76, 60);     

    private static final String MEDICINE_FILE = "src/database/medicines.txt";

    private Customer customer;
    private Order currentOrder;

    public OrderScreen(Customer customer) {
        this.customer = customer;
        initializeOrder();
        loadFonts();
        initUI();
        setVisible(true);
    }

    private void initializeOrder() {
        String orderId = "ORD" + System.currentTimeMillis();
        currentOrder = new Order(orderId, new Date(), "Pending", customer);
    }

    private void loadFonts() {
        try {
            kanitFont = Font.createFont(Font.TRUETYPE_FONT, 
                new File("src/fonts/Kanit-Regular.ttf")).deriveFont(14f);
            kanitBoldFont = Font.createFont(Font.TRUETYPE_FONT, 
                new File("src/fonts/Kanit-Bold.ttf")).deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(kanitFont);
            ge.registerFont(kanitBoldFont);
        } catch (Exception e) {
            kanitFont = new Font("Tahoma", Font.PLAIN, 14);
            kanitBoldFont = new Font("Tahoma", Font.BOLD, 24);
        }
    }

    @SuppressWarnings("unused")
    private void initUI() {
        setTitle("สั่งซื้อยา");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel headerLabel = new JLabel("สั่งซื้อยา", SwingConstants.CENTER);
        headerLabel.setFont(kanitBoldFont);
        headerLabel.setForeground(primaryColor);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        JPanel leftPanel = createStyledPanel("รายการยาที่มี");
        DefaultListModel<Medicine> medicineModel = new DefaultListModel<>();
        try {
            List<Medicine> medicines = loadMedicines();
            medicines.forEach(medicineModel::addElement);
        } catch (IOException e) {
            showError("ไม่สามารถโหลดรายการยาได้", e.getMessage());
        }

        JList<Medicine> medicineList = new JList<>(medicineModel);
        medicineList.setFont(kanitFont);
        medicineList.setCellRenderer(new ModernMedicineCellRenderer());
        JScrollPane medicineScrollPane = new JScrollPane(medicineList);
        medicineScrollPane.setBorder(BorderFactory.createEmptyBorder());
        leftPanel.add(medicineScrollPane, BorderLayout.CENTER);
        JButton addToCartButton = createStyledButton("เพิ่มลงตะกร้า", primaryColor);
        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        leftButtonPanel.setOpaque(false);
        leftButtonPanel.add(addToCartButton);
        leftPanel.add(leftButtonPanel, BorderLayout.SOUTH);
        JPanel rightPanel = createStyledPanel("ตะกร้าสินค้า");
        cartModel = new DefaultListModel<>();
        cartList = new JList<>(cartModel);
        cartList.setFont(kanitFont);
        cartList.setCellRenderer(new ModernOrderItemCellRenderer());
        JScrollPane cartScrollPane = new JScrollPane(cartList);
        cartScrollPane.setBorder(BorderFactory.createEmptyBorder());
        rightPanel.add(cartScrollPane, BorderLayout.CENTER);
        JPanel cartButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cartButtonPanel.setOpaque(false);
        JButton removeFromCartButton = createStyledButton("ลบออกจากตะกร้า", accentColor);
        cartButtonPanel.add(removeFromCartButton);
        rightPanel.add(cartButtonPanel, BorderLayout.SOUTH);
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        add(contentPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        subtotalLabel = new JLabel("ยอดรวม: 0.00 บาท", SwingConstants.RIGHT);
        subtotalLabel.setFont(kanitBoldFont.deriveFont(20f));
        subtotalLabel.setForeground(primaryColor);
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        JButton proceedToPaymentButton = createStyledButton("ชำระเงิน", primaryColor);
        JButton cancelOrderButton = createStyledButton("ยกเลิก", accentColor);
        actionPanel.add(proceedToPaymentButton);
        actionPanel.add(cancelOrderButton);
        bottomPanel.add(subtotalLabel, BorderLayout.CENTER);
        bottomPanel.add(actionPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        addToCartButton.addActionListener(e -> addMedicineToCart(medicineList.getSelectedValue()));
        removeFromCartButton.addActionListener(e -> removeFromCart(cartList.getSelectedValue()));
        proceedToPaymentButton.addActionListener(e -> proceedToPayment());
        cancelOrderButton.addActionListener(e -> cancelOrder());
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(secondaryColor, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(kanitBoldFont.deriveFont(18f));
        titleLabel.setForeground(primaryColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(kanitFont);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private class ModernMedicineCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            Medicine medicine = (Medicine) value;
            JPanel panel = new JPanel(new BorderLayout(10, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            JPanel leftPanel = new JPanel(new GridLayout(2, 1, 2, 2));
            leftPanel.setOpaque(false);
            
            JLabel nameLabel = new JLabel(medicine.getName());
            nameLabel.setFont(kanitFont);
            
            JLabel priceLabel = new JLabel(String.format("%d บาท", medicine.getUnitPrice()));
            priceLabel.setFont(kanitFont.deriveFont(12f));
            
            leftPanel.add(nameLabel);
            leftPanel.add(priceLabel);
            
            JLabel stockLabel = new JLabel("คงเหลือ: " + medicine.getStockQuantity());
            stockLabel.setFont(kanitFont);
            stockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            
            panel.add(leftPanel, BorderLayout.CENTER);
            panel.add(stockLabel, BorderLayout.EAST);
            
            if (isSelected) {
                panel.setBackground(primaryColor);
                nameLabel.setForeground(Color.WHITE);
                priceLabel.setForeground(Color.WHITE);
                stockLabel.setForeground(Color.WHITE);
            } else {
                panel.setBackground(Color.WHITE);
                nameLabel.setForeground(Color.BLACK);
                priceLabel.setForeground(Color.GRAY);
                stockLabel.setForeground(Color.GRAY);
            }
            
            return panel;
        }
    }

    private class ModernOrderItemCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            OrderItem orderItem = (OrderItem) value;
            JPanel panel = new JPanel(new BorderLayout(10, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            JPanel leftPanel = new JPanel(new GridLayout(2, 1, 2, 2));
            leftPanel.setOpaque(false);
            JLabel nameLabel = new JLabel(orderItem.getMedicine().getName());
            nameLabel.setFont(kanitFont);
            JLabel quantityLabel = new JLabel("จำนวน: " + orderItem.getQuantity());
            quantityLabel.setFont(kanitFont.deriveFont(12f));
            leftPanel.add(nameLabel);
            leftPanel.add(quantityLabel);
            JLabel subtotalLabel = new JLabel(String.format("%.2f บาท", orderItem.calculateSubtotal()));
            subtotalLabel.setFont(kanitFont);
            subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            panel.add(leftPanel, BorderLayout.CENTER);
            panel.add(subtotalLabel, BorderLayout.EAST);
            
            if (isSelected) {
                panel.setBackground(primaryColor);
                nameLabel.setForeground(Color.WHITE);
                quantityLabel.setForeground(Color.WHITE);
                subtotalLabel.setForeground(Color.WHITE);
            } else {
                panel.setBackground(Color.WHITE);
                nameLabel.setForeground(Color.BLACK);
                quantityLabel.setForeground(Color.GRAY);
                subtotalLabel.setForeground(Color.GRAY);
            }
            
            return panel;
        }
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private List<Medicine> loadMedicines() throws IOException {
        List<Medicine> medicines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(MEDICINE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    medicines.add(new Medicine(
                            parts[0], parts[1], parts[2],
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4])));
                }
            }
        }
        return medicines;
    }

    private void addMedicineToCart(Medicine medicine) {
        if (medicine == null) {
            JOptionPane.showMessageDialog(this, "Please select a medicine.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String quantityStr = JOptionPane.showInputDialog(this, "Enter quantity:");
        if (quantityStr == null || quantityStr.trim().isEmpty())
            return;

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid quantity.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!medicine.checkStock(quantity)) {
                JOptionPane.showMessageDialog(this, "Insufficient stock.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            OrderItem item = new OrderItem(quantity, medicine.getUnitPrice(), medicine);
            currentOrder.addItem(item);
            cartModel.addElement(item);

            updateSubtotal();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a number.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void removeFromCart(OrderItem item) {
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentOrder.removeItem(item);
        cartModel.removeElement(item);
        updateSubtotal();
    }

    private void updateSubtotal() {
        double subtotal = currentOrder.calculateTotalAmount();
        subtotalLabel.setText(String.format("Subtotal: %.2f", subtotal));
    }

    private void proceedToPayment() {
        if (cartModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalAmount = currentOrder.calculateTotalAmount();
        Payment payment = null;

        String[] options = { "Cash Payment", "Credit Card Payment", "Cancel" };
        int choice = JOptionPane.showOptionDialog(this,
                String.format("Total amount: %.2f\nSelect payment method.", totalAmount),
                "Payment Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            payment = new CashPayment(generatePaymentId(), customer.getCustomerId(), new Date(), totalAmount, 0);
        } else if (choice == 1) {
            String cardNumber = JOptionPane.showInputDialog(this, "Enter card number (16 digits):");
            if (cardNumber == null || cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid card number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String cvvStr = JOptionPane.showInputDialog(this, "Enter CVV (3 digits):");
            if (cvvStr == null || cvvStr.length() != 3 || !cvvStr.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Invalid CVV.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, 3);
            payment = new CreditCardPayment(generatePaymentId(), customer.getCustomerId(), new Date(),
                    (int) totalAmount, cardNumber, cal.getTime(), Integer.parseInt(cvvStr));
        }

        if (payment != null) {
            try {
                payment.processPayment();
                currentOrder.saveToFile();
                updateMedicineStock();
                displayReceipt(payment, currentOrder.calculateTotalAmount());
                dispose();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error processing payment: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelOrder() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel the order?", "Confirm",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    private void updateMedicineStock() throws IOException {
        List<Medicine> allMedicines = loadMedicines();

        for (OrderItem item : currentOrder.getItems()) {
            Medicine orderMedicine = item.getMedicine();
            for (Medicine med : allMedicines) {
                if (med.getMedicineId().equals(orderMedicine.getMedicineId())) {
                    med.reduceStock(item.getQuantity());
                    break;
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEDICINE_FILE))) {
            for (Medicine med : allMedicines) {
                writer.write(String.format("%s,%s,%s,%d,%d%n",
                        med.getMedicineId(),
                        med.getName(),
                        med.getDescription(),
                        med.getUnitPrice(),
                        med.getStockQuantity()));
            }
        }
    }

    private String generatePaymentId() {
        return "PAY" + System.currentTimeMillis();
    }

    @SuppressWarnings("unused")
    private void displayReceipt(Payment payment, double subtotal) {
    StringBuilder receipt = new StringBuilder();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    receipt.append("=== PHARMACY RECEIPT ===\n\n");
    receipt.append(String.format("Date: %s\n", sdf.format(new Date())));
    receipt.append(String.format("Order ID: %s\n", currentOrder.getOrderId()));
    receipt.append(String.format("Payment ID: %s\n\n", payment.paymentId));
    receipt.append("=== CUSTOMER INFORMATION ===\n");
    receipt.append(String.format("Name: %s\n", customer.getName()));
    receipt.append(String.format("Address: %s\n", customer.getAddress()));
    receipt.append(String.format("Phone: %s\n", customer.getPhone()));
    receipt.append(String.format("Customer Type: %s\n\n", customer.type));
    receipt.append("=== ORDER DETAILS ===\n");
    receipt.append("------------------------------------------------\n");
    receipt.append(String.format("%-10s %-20s %8s %8s %12s\n", 
        "ID", "Item", "Qty", "Price", "Subtotal"));
    receipt.append("------------------------------------------------\n");

    for (OrderItem item : currentOrder.getItems()) {
        receipt.append(String.format("%-10s %-20s %8d %8d %12.2f\n",
                item.getMedicine().getMedicineId(),
                item.getMedicine().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.calculateSubtotal()));
    }
    receipt.append("------------------------------------------------\n");
    double itemsTotal = currentOrder.getItems().stream()
            .mapToDouble(OrderItem::calculateSubtotal)
            .sum();
    receipt.append(String.format("Subtotal:%41.2f\n", itemsTotal));

    double finalAmount = currentOrder.calculateTotalAmount();
    if (customer instanceof VIPCustomer && itemsTotal > 300.0) {
        receipt.append(String.format("VIP Discount:%36s%.2f\n", "-", 35.0));
        receipt.append(String.format("Final Amount:%36s%.2f\n", "", finalAmount));
    } else if (customer instanceof GuestCustomer) {
        receipt.append(String.format("Shipping Fee:%36s%.2f\n", "+", 50.0));
        receipt.append(String.format("Final Amount:%36s%.2f\n", "", finalAmount));
    } else {
        receipt.append(String.format("Final Amount:%36s%.2f\n", "", finalAmount));
    }

    receipt.append(String.format("Payment Method: %s\n", 
        payment.getClass().getSimpleName().replace("Payment", "")));
    receipt.append("------------------------------------------------\n");
    receipt.append("Thank you for your purchase!\n");
    receipt.append("Please come again!\n");
    JDialog receiptDialog = new JDialog((Frame)null, "Receipt", true);
    receiptDialog.setLayout(new BorderLayout(10, 10));
    receiptDialog.setSize(500, 700);
    receiptDialog.setLocationRelativeTo(null);
    JTextArea receiptArea = new JTextArea(receipt.toString());
    receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    receiptArea.setEditable(false);
    receiptArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    JButton printButton = new JButton("Print Receipt");
    printButton.setFont(kanitFont);
    printButton.addActionListener(event -> printReceiptAsImage(receipt.toString()));
    JButton closeButton = new JButton("Close");
    closeButton.setFont(kanitFont);
    closeButton.addActionListener(event -> receiptDialog.dispose());
    buttonPanel.add(printButton);
    buttonPanel.add(closeButton);
    receiptDialog.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
    receiptDialog.add(buttonPanel, BorderLayout.SOUTH);
    receiptDialog.setVisible(true);
}

private void printReceiptAsImage(String receiptContent) {
    try {
        BufferedImage image = new BufferedImage(600, 800, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

        int y = 20;
        for (String line : receiptContent.split("\n")) {
            g2d.drawString(line, 20, y);
            y += 20;
        }

        g2d.dispose();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Receipt");
        fileChooser.setSelectedFile(new File("receipt_" + currentOrder.getOrderId() + ".png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ImageIO.write(image, "png", file);
            JOptionPane.showMessageDialog(this, 
                "Receipt saved successfully!\nLocation: " + file.getAbsolutePath(),
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this,
            "Error saving receipt: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
}
