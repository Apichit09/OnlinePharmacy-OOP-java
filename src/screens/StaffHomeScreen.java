package screens;

import models.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class StaffHomeScreen extends JFrame {
    private final Staff staff;
    private Font kanitFont, kanitBoldFont;
    private Color primaryColor = new Color(41, 128, 185);    // สีฟ้า
    @SuppressWarnings("unused")
    private Color secondaryColor = new Color(236, 240, 241); // สีเทาอ่อน
    private Color accentColor = new Color(231, 76, 60);      // สีแดง

    public StaffHomeScreen(Staff staff) {
        this.staff = staff;
        loadFonts();
        initUI();
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
        setTitle("Staff Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(Color.WHITE);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel headerLabel = new JLabel("ระบบจัดการร้านขายยา", SwingConstants.CENTER);
        headerLabel.setFont(kanitBoldFont);
        headerLabel.setForeground(primaryColor);
        JLabel staffInfoLabel = new JLabel("พนักงาน: " + staff.getName(), SwingConstants.RIGHT);
        staffInfoLabel.setFont(kanitFont);
        staffInfoLabel.setForeground(Color.GRAY);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.add(staffInfoLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        JButton[] buttons = {
            createStyledButton("เพิ่มยาใหม่", "add.png"),
            createStyledButton("อัปเดตข้อมูลยา", "update.png"),
            createStyledButton("ลบข้อมูลยา", "delete.png"),
            createStyledButton("จัดการคำสั่งซื้อ", "process.png"),
            createStyledButton("รายการยาทั้งหมด", "list.png"),
            createStyledButton("ค้นหายา", "search.png")
        };
        int row = 0, col = 0;
        for (JButton button : buttons) {
            gbc.gridx = col;
            gbc.gridy = row;
            gbc.weightx = 0.5;
            mainPanel.add(button, gbc);
            col++;
            if (col > 1) {
                col = 0;
                row++;
            }
        }

        add(mainPanel, BorderLayout.CENTER);
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        JButton logoutButton = createStyledButton("ออกจากระบบ", "logout.png");
        logoutButton.setBackground(accentColor);
        logoutButton.setForeground(Color.WHITE);
        footerPanel.add(logoutButton);
        add(footerPanel, BorderLayout.SOUTH);
        buttons[0].addActionListener(e -> addNewMedicine());
        buttons[1].addActionListener(e -> updateMedicine());
        buttons[2].addActionListener(e -> deleteMedicine());
        buttons[3].addActionListener(e -> processOrders());
        buttons[4].addActionListener(e -> listAllMedicines());
        buttons[5].addActionListener(e -> searchMedicine());
        logoutButton.addActionListener(e -> logout());
    }

    private JButton createStyledButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setFont(kanitFont);
        button.setPreferredSize(new Dimension(250, 100));
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(primaryColor);
            }
        });

        return button;
    }

    private void addNewMedicine() {
        JTextField medicineIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField unitPriceField = new JTextField();
        JTextField stockQuantityField = new JTextField();

        Object[] fields = {
                "Medicine ID:", medicineIdField,
                "Name:", nameField,
                "Description:", descriptionField,
                "Unit Price:", unitPriceField,
                "Stock Quantity:", stockQuantityField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add New Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                staff.addMedicine(
                        medicineIdField.getText(),
                        nameField.getText(),
                        descriptionField.getText(),
                        Integer.parseInt(unitPriceField.getText()),
                        Integer.parseInt(stockQuantityField.getText()));
                JOptionPane.showMessageDialog(this, "Medicine added successfully!");
            } catch (IOException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateMedicine() {
        JTextField medicineIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField unitPriceField = new JTextField();
        JTextField stockQuantityField = new JTextField();

        Object[] fields = {
                "Medicine ID to Update:", medicineIdField,
                "Updated Name (leave blank to keep current):", nameField,
                "Updated Description (leave blank to keep current):", descriptionField,
                "Updated Unit Price (leave blank to keep current):", unitPriceField,
                "Updated Stock Quantity (leave blank to keep current):", stockQuantityField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Update Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Medicine currentMedicine = Medicine.loadMedicineById("src/database/medicines.txt",
                        medicineIdField.getText());
                String updatedName = nameField.getText().isEmpty() ? currentMedicine.getName() : nameField.getText();
                String updatedDescription = descriptionField.getText().isEmpty() ? currentMedicine.getDescription()
                        : descriptionField.getText();
                int updatedUnitPrice = unitPriceField.getText().isEmpty() ? currentMedicine.getUnitPrice()
                        : Integer.parseInt(unitPriceField.getText());
                int updatedStockQuantity = stockQuantityField.getText().isEmpty() ? currentMedicine.getStockQuantity()
                        : Integer.parseInt(stockQuantityField.getText());

                staff.updateMedicine(medicineIdField.getText(), updatedName, updatedDescription, updatedUnitPrice,
                        updatedStockQuantity);
                JOptionPane.showMessageDialog(this, "Medicine updated successfully!");
            } catch (IOException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteMedicine() {
        String medicineId = JOptionPane.showInputDialog(this, "Enter Medicine ID to Delete:");
        if (medicineId != null) {
            try {
                staff.deleteMedicine(medicineId);
                JOptionPane.showMessageDialog(this, "Medicine deleted successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processOrders() {
        try {
            List<Order> pendingOrders = staff.getPendingOrders();
            if (pendingOrders.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No pending orders found.");
                return;
            }
            String[] orderOptions = new String[pendingOrders.size()];
            for (int i = 0; i < pendingOrders.size(); i++) {
                Order order = pendingOrders.get(i);
                orderOptions[i] = String.format("Order ID: %s | Total: %.2f", order.getOrderId(),
                        order.getTotalAmount());
            }

            String selectedOrderOption = (String) JOptionPane.showInputDialog(
                    this,
                    "Select an order to process:",
                    "Pending Orders",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    orderOptions,
                    null);

            if (selectedOrderOption != null) {
                int selectedIndex = -1;
                for (int i = 0; i < orderOptions.length; i++) {
                    if (orderOptions[i].equals(selectedOrderOption)) {
                        selectedIndex = i;
                        break;
                    }
                }
                Order selectedOrder = pendingOrders.get(selectedIndex);

                StringBuilder details = new StringBuilder("=== Order Details ===\n\n");
                details.append(String.format("Order ID: %s\n", selectedOrder.getOrderId()));
                details.append(String.format("Status: %s\n", selectedOrder.getOrderStatus()));
                details.append(String.format("Total Amount: %.2f\n\n", selectedOrder.getTotalAmount()));
                Customer customer = selectedOrder.getCustomer();
                details.append("=== Customer Details ===\n");
                details.append(String.format("Name: %s\n", customer.getName()));
                details.append(String.format("Address: %s\n", customer.getAddress()));
                details.append(String.format("Phone: %s\n\n", customer.getPhone()));
                details.append("=== Ordered Medicines ===\n");
                for (OrderItem item : selectedOrder.getItems()) {
                    Medicine med = item.getMedicine();
                    details.append(String.format(
                            "Medicine ID: %s\nName: %s\nQuantity: %d\nUnit Price: %d\nSubtotal: %.2f\n\n",
                            med.getMedicineId(),
                            med.getName(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.calculateSubtotal()));
                }

                JTextArea orderDetails = new JTextArea(details.toString());
                orderDetails.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(orderDetails);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        scrollPane,
                        "Confirm Order",
                        JOptionPane.OK_CANCEL_OPTION);

                if (confirm == JOptionPane.OK_OPTION) {
                    staff.processOrder(selectedOrder);
                    JOptionPane.showMessageDialog(this, "Order processed successfully!");
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listAllMedicines() {
        try {
            List<String> medicines = staff.listAllMedicines();
            JTextArea textArea = new JTextArea(String.join("\n", medicines));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "All Medicines", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchMedicine() {
        String keyword = JOptionPane.showInputDialog(this, "Enter medicine name to search:");
        if (keyword != null) {
            try {
                List<String> results = staff.searchMedicineByName(keyword);
                JTextArea textArea = new JTextArea(String.join("\n", results));
                textArea.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "Search Results", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        JOptionPane.showMessageDialog(this, "Logging out...");
        dispose();
    }
}
