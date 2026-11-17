
package com.metromanagement.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import com.metromanagement.model.Passenger;
import com.metromanagement.service.PassengerService;

public class RegisterFrame extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;

    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private final Color darkBlue = new Color(15, 52, 91);
    private final Color blue = new Color(20, 105, 220);
    private final Color textColor = new Color(35, 54, 77);
    private final Color background = new Color(242, 247, 253);

    public RegisterFrame() {

        setTitle("Passenger Registration");
        setSize(560, 680);
        setMinimumSize(new Dimension(520, 640));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(background);

        // ================= HEADER =================

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(darkBlue);
        header.setBorder(new EmptyBorder(22, 15, 22, 15));

        JLabel title = new JLabel("Passenger Registration");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "Create your account to get started"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 233, 248));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(subtitle);

        // ================= FORM =================

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 5));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(22, 35, 24, 35));

        nameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        styleField(nameField);
        styleField(emailField);
        styleField(phoneField);
        styleField(passwordField);
        styleField(confirmPasswordField);

        addField(form, "Full Name", nameField);
        addField(form, "Email Address", emailField);
        addField(form, "Phone Number", phoneField);
        addField(form, "Password", passwordField);
        addField(form, "Confirm Password", confirmPasswordField);

        // ================= BUTTONS =================

        JButton registerButton = new JButton("Create Account");
        stylePrimaryButton(registerButton);

        JButton clearButton = new JButton("Clear");
        styleSecondaryButton(clearButton);

        JPanel actionButtons = new JPanel(
            new GridLayout(1, 2, 12, 0)
        );
        actionButtons.setBackground(Color.WHITE);
        actionButtons.add(registerButton);
        actionButtons.add(clearButton);
        actionButtons.setPreferredSize(new Dimension(440, 48));

        JButton backButton = new JButton("Back to Login");
        styleSecondaryButton(backButton);
        backButton.setPreferredSize(new Dimension(440, 46));

        JPanel buttonsPanel = new JPanel(
            new GridLayout(2, 1, 0, 12)
        );
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.add(actionButtons);
        buttonsPanel.add(backButton);
        buttonsPanel.setPreferredSize(new Dimension(440, 106));

        form.add(Box.createVerticalStrut(5));
        form.add(buttonsPanel);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);

        setContentPane(root);

        // ================= ACTIONS =================

        registerButton.addActionListener(e -> registerPassenger());
        clearButton.addActionListener(e -> clearFields());
        backButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    // ================= ADD FORM FIELD =================

    private void addField(
            JPanel panel,
            String labelText,
            JComponent field) {

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(darkBlue);

        panel.add(label);
        panel.add(field);
    }

    // ================= FIELD STYLE =================

    private void styleField(JComponent field) {

        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(textColor);
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(400, 38));

        field.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(215, 225, 237)
                ),
                new EmptyBorder(7, 10, 7, 10)
            )
        );
    }

    // ================= PRIMARY BUTTON =================

    private void stylePrimaryButton(JButton button) {

        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(blue);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(210, 48));

        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    // ================= SECONDARY BUTTON =================

    private void styleSecondaryButton(JButton button) {

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(blue);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);

        button.setBorder(
            BorderFactory.createLineBorder(
                new Color(170, 200, 232), 1
            )
        );

        button.setPreferredSize(new Dimension(210, 46));

        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    // ================= REGISTRATION =================

    private void registerPassenger() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        String password =
            new String(passwordField.getPassword());

        String confirmPassword =
            new String(confirmPasswordField.getPassword());

        if (name.isEmpty()
                || email.isEmpty()
                || phone.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please fill all fields.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid email address.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!phone.matches("\\d{10}")) {

            JOptionPane.showMessageDialog(
                this,
                "Phone number must contain exactly 10 digits.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (password.length() < 8) {

            JOptionPane.showMessageDialog(
                this,
                "Password must contain at least 8 characters.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                this,
                "Passwords do not match.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Username field removed from the registration GUI.
        // Email is passed internally for existing model compatibility.
        Passenger passenger = new Passenger(
            name,
            email,
            phone,
            email,
            password
        );

        boolean registered =
            PassengerService.registerPassenger(passenger);

        if (registered) {

            JOptionPane.showMessageDialog(
                this,
                "Registration Successful!\n"
                    + "You can now login using your email or phone number.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();
            dispose();

        } else {

            JOptionPane.showMessageDialog(
                this,
                "Registration failed. Email or phone may already exist. "
                    + "Check the Eclipse Console for database errors.",
                "Registration Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= CLEAR FIELDS =================

    private void clearFields() {

        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }
}
