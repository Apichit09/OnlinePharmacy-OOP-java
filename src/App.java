import screens.*;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);

            } catch (Exception e) {
                System.err.println("Application Error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
