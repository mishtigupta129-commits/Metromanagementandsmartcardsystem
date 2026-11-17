
package com.metromanagement.main;

import javax.swing.*;
import java.awt.*;

import com.metromanagement.model.Passenger;
import com.metromanagement.service.PassengerService;
import com.metromanagement.gui.PassengerDashboard;

public class PassengerLogin {

    public static void showLogin() {

        JFrame frame = new JFrame("Passenger Login");
        frame.setSize(450, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(
                BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton clearButton = new JButton("Clear");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(clearButton);

        loginButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password =
                    new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter username and password.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Passenger passenger =
                    PassengerService.login(username, password);

            if (passenger != null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Login Successful!\nWelcome "
                                + passenger.getName(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                frame.dispose();
                new PassengerDashboard(passenger);

            } else {
                JOptionPane.showMessageDialog(
                        frame,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}
