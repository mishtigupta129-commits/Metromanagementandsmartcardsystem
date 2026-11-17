package com.metromanagement.gui;

import javax.swing.*;
import java.awt.*;

public class AdminLogin {

    public static void showLogin() {

        JFrame frame = new JFrame("Admin Login");

        frame.setSize(450, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.setBorder(
            BorderFactory.createEmptyBorder(
                30, 40, 30, 40
            )
        );

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

            String username = usernameField.getText();
            String password =
                new String(passwordField.getPassword());

            if (username.equals("admin") &&
                password.equals("1234")) {

                frame.dispose();

                AdminDashboard.showDashboard();

            } else {

                JOptionPane.showMessageDialog(
                    frame,
                    "Invalid Username or Password!"
                );
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