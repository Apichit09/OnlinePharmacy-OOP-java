package screens;

import models.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;


public class CustomerHomeScreen extends JFrame {
    private Customer customer;
    @SuppressWarnings("unused")
    private int currentPage = 1;
    @SuppressWarnings("unused")
    private static final int ITEMS_PER_PAGE = 10;
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private Font kanitFont, kanitBoldFont;
    private Color primaryColor = new Color(41, 128, 185);
    @SuppressWarnings("unused")
    private Color secondaryColor = new Color(236, 240, 241);
    private Color accentColor = new Color(231, 76, 60);

    public CustomerHomeScreen(Customer customer) {
        this.customer = customer;
        setTitle("Pharmacy System - Customer Home");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadFonts();
        initUI();
    }

    private void loadFonts() {
        try {
            kanitFont = Font.createFont(Font.TRUETYPE_FONT, 
                new File("src/fonts/Kanit-Regular.ttf")).deriveFont(14f);
            kanitBoldFont = Font.createFont(Font.TRUETYPE_FONT, 
                new File("src/fonts/Kanit-Bold.ttf")).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(kanitFont);
            ge.registerFont(kanitBoldFont);
        } catch (Exception e) {
            kanitFont = new Font("Tahoma", Font.PLAIN, 14);
            kanitBoldFont = new Font("Tahoma", Font.BOLD, 20);
        }
    }

    @SuppressWarnings("unused")
    private void initUI() {

        setLayout(new BorderLayout(10, 10));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel welcomeLabel = new JLabel("ยินดีต้อนรับ, " + customer.getName() + 
                                       " (" + customer.type + ")", SwingConstants.LEFT);
        welcomeLabel.setFont(kanitBoldFont);
        welcomeLabel.setForeground(primaryColor);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchField = new JTextField(20);
        searchField.setFont(kanitFont);
        searchField.setPreferredSize(new Dimension(200, 35));
        JButton searchButton = createStyledButton("ค้นหา", primaryColor);
        searchPanel.add(new JLabel("ค้นหายา : "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        String[] columns = {"รหัสยา", "ชื่อยา", "รายละเอียด", "ราคา", "จำนวนคงเหลือ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medicineTable = new JTable(tableModel);
        medicineTable.setFont(kanitFont);
        medicineTable.getTableHeader().setFont(kanitFont);
        medicineTable.setRowHeight(30);
        medicineTable.setSelectionBackground(primaryColor);
        medicineTable.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(medicineTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel sideMenuPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        sideMenuPanel.setPreferredSize(new Dimension(200, 0));
        sideMenuPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        sideMenuPanel.setBackground(Color.WHITE);
        JButton orderHistoryButton = createStyledButton("ประวัติการสั่งซื้อ", primaryColor);
        JButton paymentHistoryButton = createStyledButton("ประวัติการชำระเงิน", primaryColor);
        JButton createOrderButton = createStyledButton("สร้างคำสั่งซื้อ", primaryColor);
        JButton logoutButton = createStyledButton("ออกจากระบบ", accentColor);
        sideMenuPanel.add(orderHistoryButton);
        sideMenuPanel.add(paymentHistoryButton);
        sideMenuPanel.add(createOrderButton);
        sideMenuPanel.add(logoutButton);
        add(sideMenuPanel, BorderLayout.EAST);
        add(contentPanel, BorderLayout.CENTER);
        searchButton.addActionListener(e -> searchMedicine(searchField.getText()));
        searchField.addActionListener(e -> searchMedicine(searchField.getText()));
        orderHistoryButton.addActionListener(e -> showOrderHistory());
        paymentHistoryButton.addActionListener(e -> showPaymentHistory());
        createOrderButton.addActionListener(e -> createNewOrder());
        logoutButton.addActionListener(e -> logout());
        loadMedicineData();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(kanitFont);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void loadMedicineData() {
        tableModel.setRowCount(0);
        try {
            List<Medicine> medicines = loadMedicines();
            for (Medicine med : medicines) {
                tableModel.addRow(new Object[]{
                    med.getMedicineId(),
                    med.getName(),
                    med.getDescription(),
                    med.getUnitPrice(),
                    med.getStockQuantity()
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "เกิดข้อผิดพลาดในการโหลดข้อมูลยา: " + e.getMessage(),
                "ข้อผิดพลาด", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchMedicine(String keyword) {
        tableModel.setRowCount(0);
        try {
            List<Medicine> medicines = loadMedicines();
            for (Medicine med : medicines) {
                if (med.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    med.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    tableModel.addRow(new Object[]{
                        med.getMedicineId(),
                        med.getName(),
                        med.getDescription(),
                        med.getUnitPrice(),
                        med.getStockQuantity()
                    });
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "เกิดข้อผิดพลาดในการค้นหายา: " + e.getMessage(),
                "ข้อผิดพลาด", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Medicine> loadMedicines() throws IOException {
        List<Medicine> medicines = new ArrayList<>();
        String filePath = "src/database/medicines.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    medicines.add(new Medicine(
                            parts[0],
                            parts[1],
                            parts[2],
                            Integer.parseInt(parts[3]),
                            Integer.parseInt(parts[4])));
                }
            }
        }
        return medicines;
    }

    private void showOrderHistory() {
        try {
            Order[] orders = customer.getOrderHistory();
            if (orders.length == 0) {
                JOptionPane.showMessageDialog(this, "No order history found.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFrame orderFrame = new JFrame("Order History");
            orderFrame.setSize(600, 400);
            orderFrame.setLocationRelativeTo(this);

            JTextArea orderText = new JTextArea();
            orderText.setEditable(false);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Order order : orders) {
                orderText.append(String.format("Order ID: %s\nDate: %s\nStatus: %s\nTotal: %.2f\n",
                        order.getOrderId(),
                        sdf.format(order.getOrderDate()),
                        order.getOrderStatus(),
                        order.getTotalAmount()));

                orderText.append("\nItems:\n");
                for (OrderItem item : order.getItems()) {
                    Medicine med = item.getMedicine();
                    orderText.append(String.format("%s\t%s\t%d\t%d\t%.2f\n",
                            med.getMedicineId(),
                            med.getName(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.calculateSubtotal()));
                }
                orderText.append("-------------------------------------------------------------\n");
            }

            JScrollPane scrollPane = new JScrollPane(orderText);
            orderFrame.add(scrollPane);
            orderFrame.setVisible(true);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving order history: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPaymentHistory() {
        try {
            Payment[] payments = customer.getPaymentHistory();
            if (payments.length == 0) {
                JOptionPane.showMessageDialog(this, "No payment history found.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JFrame paymentFrame = new JFrame("Payment History");
            paymentFrame.setSize(600, 400);
            paymentFrame.setLocationRelativeTo(this);

            JTextArea paymentText = new JTextArea();
            paymentText.setEditable(false);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Payment payment : payments) {
                paymentText.append(String.format("Payment ID: %s\nDate: %s\nAmount: %d\nType: %s\n",
                        payment.paymentId,
                        sdf.format(payment.paymentDate),
                        payment.amount,
                        payment.getClass().getSimpleName().replace("Payment", "")));
                paymentText.append("-------------------------------------------------------------\n");
            }

            JScrollPane scrollPane = new JScrollPane(paymentText);
            paymentFrame.add(scrollPane);
            paymentFrame.setVisible(true);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading payment history: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createNewOrder() {
        SwingUtilities.invokeLater(() -> new OrderScreen(customer).setVisible(true)); 
    }

    private void logout() {
        dispose();
        new LoginScreen().setVisible(true);
    }
}
