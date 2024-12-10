package screens;

import models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.Font;
import javax.swing.border.*;

@SuppressWarnings("unused")
public class LoginScreen extends JFrame {
    private JPanel mainPanel, headerPanel, buttonPanel;
    private JButton customerButton, staffButton, exitButton;
    private JLabel titleLabel, subtitleLabel;
    private Font kanitFont, kanitBoldFont;
    private Color primaryColor = new Color(41, 128, 185); 
    private Color secondaryColor = new Color(236, 240, 241); 
    private Color accentColor = new Color(231, 76, 60);    
    public LoginScreen() {
        setTitle("Pharmacy Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
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

        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        add(mainPanel);
        headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.setOpaque(false);
        titleLabel = new JLabel("Online Pharmacy", JLabel.CENTER);
        titleLabel.setFont(kanitBoldFont);
        titleLabel.setForeground(primaryColor);
        subtitleLabel = new JLabel("ร้านขายยาออนไลน์", JLabel.CENTER);
        subtitleLabel.setFont(kanitFont);
        subtitleLabel.setForeground(Color.GRAY);
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        buttonPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        buttonPanel.setOpaque(false);
        customerButton = createStyledButton("เข้าสู่ระบบสำหรับลูกค้า", primaryColor);
        staffButton = createStyledButton("เข้าสู่ระบบสำหรับพนักงาน", primaryColor);
        exitButton = createStyledButton("ออกจากระบบ", accentColor);
        buttonPanel.add(customerButton);
        buttonPanel.add(staffButton);
        buttonPanel.add(exitButton);
        JPanel buttonWrapper = new JPanel(new GridBagLayout());
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(buttonPanel);
        mainPanel.add(buttonWrapper, BorderLayout.CENTER);
        customerButton.addActionListener(e -> showCustomerMenu());
        staffButton.addActionListener(e -> showStaffLogin());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(kanitFont);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(250, 45));
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

    private void showCustomerMenu() {
        JFrame customerFrame = new JFrame("ระบบสำหรับลูกค้า");
        customerFrame.setSize(400, 300);
        customerFrame.setLocationRelativeTo(null);
        customerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        JButton loginButton = createStyledButton("เข้าสู่ระบบ", primaryColor);
        JButton registerButton = createStyledButton("ลงทะเบียน", primaryColor);
        JButton backButton = createStyledButton("กลับ", accentColor);
        panel.add(loginButton);
        panel.add(registerButton);
        panel.add(backButton);

        loginButton.addActionListener(e -> {
            customerFrame.dispose();
            customerLogin();
        });

        registerButton.addActionListener(e -> {
            customerFrame.dispose();
            customerRegister();
        });

        backButton.addActionListener(e -> customerFrame.dispose());

        customerFrame.add(panel);
        customerFrame.setVisible(true);
    }

    @SuppressWarnings("deprecation")
    private void showStaffLogin() {
        String staffId = JOptionPane.showInputDialog(this, "Enter Staff ID:");

        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/database/staff.txt"));
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(staffId)) {
                    found = true;
                    JOptionPane.showMessageDialog(this, "Welcome, " + parts[1]);
                    Staff staff = new Staff(parts[0], parts[1], parts[2], parts[3]);
                    StaffHomeScreen staffScreen = new StaffHomeScreen(staff);
                    staffScreen.show();
                    break;
                }
            }
            reader.close();

            if (!found) {
                JOptionPane.showMessageDialog(this, "Invalid Staff ID. Please try again.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error accessing staff database: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    private void customerLogin() {
        String email = JOptionPane.showInputDialog(this, "Enter Email:");

        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/database/customers.txt"));
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[4].equals(email)) {
                    found = true;
                    JOptionPane.showMessageDialog(this, "Welcome back, " + parts[1]);
                    Customer customer;
                    if (parts[5].equals("VIP")) {
                        customer = new VIPCustomer(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    } else {
                        customer = new GuestCustomer(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    }
                    CustomerHomeScreen customerScreen = new CustomerHomeScreen(customer);
                    customerScreen.show(); 
                    break;
                }
            }
            reader.close();

            if (!found) {
                JOptionPane.showMessageDialog(this, "Email not found. Please register or try again.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error accessing customer database: " + e.getMessage());
        }
    }

    private void customerRegister() {
        JTextField emailField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        String[] options = { "Guest", "VIP" };
        JComboBox<String> typeComboBox = new JComboBox<>(options);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Type:"));
        panel.add(typeComboBox);
        int result = JOptionPane.showConfirmDialog(this, panel, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText();
            String name = nameField.getText();
            String address = addressField.getText();
            String phone = phoneField.getText();
            String type = (String) typeComboBox.getSelectedItem();
            String customerId = generateCustomerId();

            try {
                Customer.register(customerId, name, address, phone, email, type);
                JOptionPane.showMessageDialog(this, "Registration successful!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error during registration: " + e.getMessage());
            }
        }
    }

    private String generateCustomerId() {
        return "CUS" + System.currentTimeMillis();
    }
}
