
package com.metromanagement.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import com.metromanagement.model.Passenger;
import com.metromanagement.service.PassengerService;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMe;

    private final Color darkBlue = new Color(15, 52, 91);
    private final Color blue = new Color(20, 105, 220);
    private final Color textColor = new Color(35, 54, 77);
    private final Color placeholderColor = Color.GRAY;

    private static final String USER_PLACEHOLDER =
        "Email / Mobile Number";

    private static final String PASS_PLACEHOLDER =
        "Password";

    private boolean usernamePlaceholderActive = true;
    private boolean passwordPlaceholderActive = true;

    public LoginFrame() {

        setTitle("Metro & Smart Card Management System");
        setSize(1000, 590);
        setMinimumSize(new Dimension(850, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(Color.WHITE);

        // ================= LEFT PANEL =================

        JPanel leftPanel = new JPanel() {

            private Image backgroundImage;

            {
                java.net.URL imageURL = getClass().getResource(
                    "/com/metromanagement/gui/metro_background.png"
                );

                if (imageURL != null) {
                    backgroundImage =
                        new ImageIcon(imageURL).getImage();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                if (backgroundImage != null) {
                    g2.drawImage(
                        backgroundImage,
                        0, 0,
                        getWidth(), getHeight(),
                        this
                    );
                } else {
                    g2.setColor(darkBlue);
                    g2.fillRect(
                        0, 0, getWidth(), getHeight()
                    );
                }

                g2.setColor(new Color(5, 30, 58, 125));
                g2.fillRect(
                    0, 0, getWidth(), getHeight()
                );

                g2.dispose();
            }
        };

        leftPanel.setLayout(new GridBagLayout());

        JPanel branding = new JPanel();
        branding.setOpaque(false);
        branding.setLayout(
            new BoxLayout(branding, BoxLayout.Y_AXIS)
        );

        JLabel metroIcon = new JLabel("🚇");
        metroIcon.setFont(
            new Font("Segoe UI Emoji", Font.PLAIN, 60)
        );
        metroIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandTitle = new JLabel(
            "<html><div style='text-align:center;'>"
            + "Metro & Smart Card<br>"
            + "Management System"
            + "</div></html>"
        );

        brandTitle.setFont(
            new Font("Arial", Font.BOLD, 29)
        );
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel(
            "Travel Smart   |   Pay Smart   |   Go Green"
        );

        tagline.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );
        tagline.setForeground(Color.WHITE);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        branding.add(metroIcon);
        branding.add(Box.createVerticalStrut(18));
        branding.add(brandTitle);
        branding.add(Box.createVerticalStrut(22));
        branding.add(tagline);

        leftPanel.add(branding);

        // ================= RIGHT PANEL =================

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(242, 247, 253));
        rightPanel.setBorder(new EmptyBorder(25, 28, 25, 28));

        JPanel card = new JPanel();
        card.setLayout(
            new BoxLayout(card, BoxLayout.Y_AXIS)
        );
        card.setBackground(Color.WHITE);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(222, 231, 242)
                ),
                new EmptyBorder(28, 30, 28, 30)
            )
        );

        JLabel title = new JLabel("Login");
        title.setFont(
            new Font("Arial", Font.BOLD, 30)
        );
        title.setForeground(darkBlue);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(
            "Welcome back! Please login to your account."
        );
        subtitle.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );
        subtitle.setForeground(new Color(105, 120, 140));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email or mobile number
        usernameField = new JTextField();
        styleField(usernameField);
        setUsernamePlaceholder();

        // Password
        passwordField = new JPasswordField();
        styleField(passwordField);
        setPasswordPlaceholder();

        // Remember Me
        rememberMe = new JCheckBox("Remember Me");
        rememberMe.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );
        rememberMe.setBackground(Color.WHITE);
        rememberMe.setForeground(textColor);
        rememberMe.setFocusPainted(false);

        // Forgot Password
        JButton forgotButton =
            new JButton("Forgot Password?");

        forgotButton.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );
        forgotButton.setForeground(blue);
        forgotButton.setBorderPainted(false);
        forgotButton.setContentAreaFilled(false);
        forgotButton.setFocusPainted(false);
        forgotButton.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        JPanel options = new JPanel(new BorderLayout());
        options.setBackground(Color.WHITE);
        options.add(rememberMe, BorderLayout.WEST);
        options.add(forgotButton, BorderLayout.EAST);
        options.setMaximumSize(new Dimension(400, 35));

        // Login button
        JButton loginButton = new JButton("Login");
        stylePrimaryButton(loginButton);

        JLabel orLabel = new JLabel("OR");
        orLabel.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );
        orLabel.setForeground(new Color(110, 120, 135));
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Register and Admin buttons

        // Register button - full width
        JButton registerButton = new JButton("Register");
        styleSecondaryButton(registerButton);

        JPanel bottomButtons = new JPanel(new BorderLayout());
        bottomButtons.setBackground(Color.WHITE);
        bottomButtons.add(registerButton, BorderLayout.CENTER);
        bottomButtons.setMaximumSize(new Dimension(400, 42));
        bottomButtons.setPreferredSize(new Dimension(350, 42));


        // Add components
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(28));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(15));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(options);
        card.add(Box.createVerticalStrut(20));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(18));
        card.add(orLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(bottomButtons);

        rightPanel.add(card);

        root.add(leftPanel);
        root.add(rightPanel);

        setContentPane(root);

        // ================= BUTTON ACTIONS =================

        loginButton.addActionListener(e -> loginPassenger());

        passwordField.addActionListener(e -> loginPassenger());

        registerButton.addActionListener(e ->
            new RegisterFrame()
        );

      
        forgotButton.addActionListener(e ->
            JOptionPane.showMessageDialog(
                this,
                "Password recovery is not configured yet.",
                "Forgot Password",
                JOptionPane.INFORMATION_MESSAGE
            )
        );

        setVisible(true);
    }

    // ================= USERNAME PLACEHOLDER =================

    private void setUsernamePlaceholder() {

        usernameField.setText(USER_PLACEHOLDER);
        usernameField.setForeground(placeholderColor);
        usernamePlaceholderActive = true;

        usernameField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {

                if (usernamePlaceholderActive) {
                    usernameField.setText("");
                    usernameField.setForeground(textColor);
                    usernamePlaceholderActive = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {

                if (usernameField.getText().trim().isEmpty()) {
                    usernameField.setText(USER_PLACEHOLDER);
                    usernameField.setForeground(placeholderColor);
                    usernamePlaceholderActive = true;
                }
            }
        });
    }

    // ================= PASSWORD PLACEHOLDER =================

    private void setPasswordPlaceholder() {

        passwordField.setEchoChar((char) 0);
        passwordField.setText(PASS_PLACEHOLDER);
        passwordField.setForeground(placeholderColor);
        passwordPlaceholderActive = true;

        passwordField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {

                if (passwordPlaceholderActive) {
                    passwordField.setText("");
                    passwordField.setEchoChar('●');
                    passwordField.setForeground(textColor);
                    passwordPlaceholderActive = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {

                if (passwordField.getPassword().length == 0) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText(PASS_PLACEHOLDER);
                    passwordField.setForeground(placeholderColor);
                    passwordPlaceholderActive = true;
                }
            }
        });
    }

    // ================= FIELD STYLE =================

    private void styleField(JTextField field) {

        field.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );
        field.setForeground(textColor);
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(350, 44));
        field.setMaximumSize(new Dimension(400, 44));

        field.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(215, 225, 237)
                ),
                new EmptyBorder(8, 12, 8, 12)
            )
        );
    }

    // ================= BUTTON STYLES =================

    private void stylePrimaryButton(JButton button) {

        button.setFont(
            new Font("Arial", Font.BOLD, 15)
        );
        button.setForeground(Color.WHITE);
        button.setBackground(blue);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(400, 44));
        button.setPreferredSize(new Dimension(350, 44));
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    private void styleSecondaryButton(JButton button) {

        button.setFont(
            new Font("Arial", Font.BOLD, 12)
        );
        button.setForeground(blue);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);

        button.setBorder(
            BorderFactory.createLineBorder(
                new Color(170, 200, 232)
            )
        );

        button.setPreferredSize(new Dimension(170, 42));
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    // ================= LOGIN =================

    private void loginPassenger() {

        String emailOrPhone = usernamePlaceholderActive
            ? ""
            : usernameField.getText().trim();

        String password = passwordPlaceholderActive
            ? ""
            : new String(passwordField.getPassword());

        if (emailOrPhone.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter your email/mobile number and password.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Passenger passenger = PassengerService.login(
            emailOrPhone,
            password
        );

        if (passenger != null) {

            JOptionPane.showMessageDialog(
                this,
                "Login Successful!\nWelcome " + passenger.getName(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Open the GUI-package dashboard using its constructor
            new PassengerDashboard(passenger);
            dispose();

        } else {

            JOptionPane.showMessageDialog(
                this,
                "Invalid email/mobile number or password.",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
