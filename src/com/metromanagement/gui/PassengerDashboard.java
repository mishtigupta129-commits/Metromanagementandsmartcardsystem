package com.metromanagement.gui;

import com.metromanagement.model.Passenger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import com.metromanagement.service.PassengerService;

public class PassengerDashboard extends JFrame {

    private final Passenger passenger;

    private final Color navy = new Color(14, 43, 77);
    private final Color blue = new Color(22, 105, 230);
    private final Color background = new Color(244, 248, 253);
    private final Color text = new Color(18, 37, 79);
    private final Color muted = new Color(99, 116, 146);
    private final Color border = new Color(219, 231, 245);

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private static final int SIDEBAR_WIDTH = 220;
    private static final int NAV_WIDTH = 204;
    private static final int NAV_HEIGHT = 42;

    public PassengerDashboard(Passenger passenger) {
        this.passenger = passenger;

        setTitle("Metro & Smart Card Management System");
        setSize(1500, 900);
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(background);

        // SIDEBAR
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(navy);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));

        JPanel sidebarContent = new JPanel();
        sidebarContent.setBackground(navy);
        sidebarContent.setLayout(
                new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setBorder(new EmptyBorder(20, 6, 10, 6));

        JLabel logoIcon = new JLabel("\u25CE", SwingConstants.CENTER);
        logoIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 35));
        logoIcon.setForeground(Color.WHITE);
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoIcon.setMaximumSize(new Dimension(200, 42));

        JLabel logo = new JLabel("METRO SYSTEM", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setMaximumSize(new Dimension(200, 28));

        JLabel role = new JLabel(
                "P A S S E N G E R", SwingConstants.CENTER);
        role.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        role.setForeground(new Color(190, 211, 235));
        role.setAlignmentX(Component.CENTER_ALIGNMENT);
        role.setMaximumSize(new Dimension(200, 22));

        sidebarContent.add(logoIcon);
        sidebarContent.add(Box.createVerticalStrut(5));
        sidebarContent.add(logo);
        sidebarContent.add(Box.createVerticalStrut(4));
        sidebarContent.add(role);
        sidebarContent.add(Box.createVerticalStrut(22));

        JPanel nav = new JPanel();
        nav.setBackground(navy);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setAlignmentX(Component.LEFT_ALIGNMENT);
        nav.setBorder(BorderFactory.createEmptyBorder());
        nav.setPreferredSize(new Dimension(NAV_WIDTH, 480));
        nav.setMaximumSize(new Dimension(NAV_WIDTH, 480));

        addNav(nav, "\u2302", "Dashboard", true,
                () -> showPage("Dashboard"));

        addNav(nav, "\u25A3", "Smart Cards", false,
                () -> SmartCardFrame.showManagement());

        addNav(nav, "\u21C4", "Plan Journey", false,
                () -> showPage("Plan Journey"));

        addNav(nav, "\u25CE", "Station Details", false,
                () -> showPage("Station Details"));

        // NEW METRO MAP SIDEBAR OPTION
        addNav(nav, "\u25A6", "Metro Map", false,
                () -> MetroMapFrame.showManagement());

        addNav(nav, "\u25C8", "Tickets", false,
                () -> TicketFrame.showManagement());

        addNav(nav, "\u2637", "Transaction History", false,
                () -> TransactionHistoryFrame.showManagement());

        // LOST & FOUND SIDEBAR OPTION
        addNav(nav, "\u2691", "Lost & Found", false,
                () -> LostFoundFrame.showManagement());

        addNav(nav, "\u263A", "Profile", false,
                () -> showPage("Profile"));

        sidebarContent.add(nav);

        JButton logout = makeNavButton("\u21AA", "Logout", false);
        logout.addActionListener(e -> logout());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(navy);
        bottom.setBorder(new EmptyBorder(8, 12, 14, 12));

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(75, 109, 145));

        bottom.add(separator, BorderLayout.NORTH);
        bottom.add(logout, BorderLayout.CENTER);

        sidebar.add(sidebarContent, BorderLayout.NORTH);
        sidebar.add(bottom, BorderLayout.SOUTH);

        // MAIN PAGES
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(background);

        contentPanel.add(createHomePanel(), "Dashboard");

        contentPanel.add(
                new JourneyFrame(() -> showPage("Dashboard")),
                "Plan Journey");
        

        contentPanel.add(new StationDetailsPanel(), "Station Details");
     
        contentPanel.add(createProfilePanel(), "Profile");

        root.add(sidebar, BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);
    }

    // SIDEBAR NAVIGATION
    private void addNav(
            JPanel parent,
            String icon,
            String label,
            boolean active,
            Runnable action) {

        JButton button = makeNavButton(icon, label, active);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(e -> action.run());

        parent.add(button);
        parent.add(Box.createVerticalStrut(3));
    }

    private JButton makeNavButton(
            String icon,
            String label,
            boolean active) {

        JButton button = new JButton();
        button.setLayout(new BorderLayout(3, 0));

        button.setPreferredSize(new Dimension(NAV_WIDTH, NAV_HEIGHT));
        button.setMinimumSize(new Dimension(NAV_WIDTH, NAV_HEIGHT));
        button.setMaximumSize(new Dimension(NAV_WIDTH, NAV_HEIGHT));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.setBackground(active ? blue : navy);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 0, 10, 0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon, SwingConstants.LEFT);
        iconLabel.setPreferredSize(new Dimension(20, 20));
        iconLabel.setMinimumSize(new Dimension(20, 20));
        iconLabel.setFont(
                new Font("Segoe UI Symbol", Font.PLAIN, 15));
        iconLabel.setForeground(Color.WHITE);

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textLabel.setForeground(Color.WHITE);
        textLabel.setHorizontalAlignment(SwingConstants.LEFT);

        button.add(iconLabel, BorderLayout.WEST);
        button.add(textLabel, BorderLayout.CENTER);

        if (!active) {
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setBackground(new Color(27, 69, 111));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setBackground(navy);
                }
            });
        }

        return button;
    }

    // DASHBOARD HOME
    private JPanel createHomePanel() {
        JPanel home = new JPanel(new BorderLayout(0, 22));
        home.setBackground(background);
        home.setBorder(new EmptyBorder(27, 30, 25, 30));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Passenger Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(text);

        String name = passenger == null || passenger.getName() == null
                ? "Passenger" : passenger.getName();

        JLabel welcome = new JLabel("Welcome back, " + name + "!");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        welcome.setForeground(muted);

        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(welcome);

        JLabel avatar = new JLabel(initials(name), SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(blue);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        avatar.setPreferredSize(new Dimension(44, 44));

        JPanel user = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        user.setOpaque(false);
        user.add(avatar);

        header.add(heading, BorderLayout.WEST);
        header.add(user, BorderLayout.EAST);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 18, 0);

        gbc.gridy = 0;
        body.add(createJourneyBanner(), gbc);

        // TWO-COLUMN FEATURE CARD LAYOUT
        JPanel quick = new JPanel(new GridLayout(0, 2, 16, 15));
        quick.setOpaque(false);

        quick.add(quickCard(
                "\u25A3",
                "Smart Cards",
                "Issue or manage your cards and recharge balance.",
                new Color(224, 239, 255),
                () -> SmartCardFrame.showManagement()));

        quick.add(quickCard(
                "\u25C8",
                "Tickets",
                "Book, view and cancel eligible metro tickets.",
                new Color(220, 248, 241),
                () -> TicketFrame.showManagement()));

        quick.add(quickCard(
                "\u25CE",
                "Station Details",
                "Explore metro lines and their terminal stations.",
                new Color(255, 240, 216),
                () -> showPage("Station Details")));

        // NEW METRO MAP DASHBOARD CARD
        quick.add(quickCard(
                "\u25A6",
                "Metro Map",
                "Open the official Delhi Metro network map and zoom in or out.",
                new Color(226, 239, 255),
                () -> MetroMapFrame.showManagement()));

        quick.add(quickCard(
                "\u2637",
                "Transaction History",
                "View your smart card transaction records.",
                new Color(239, 232, 255),
                () -> TransactionHistoryFrame.showManagement()));

        // LOST & FOUND DASHBOARD CARD
        quick.add(quickCard(
                "\u2691",
                "Lost & Found",
                "Report lost or found items and view reports.",
                new Color(224, 247, 250),
                () -> LostFoundFrame.showManagement()));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        body.add(quick, gbc);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(background);

        home.add(header, BorderLayout.NORTH);
        home.add(scroll, BorderLayout.CENTER);

        return home;
    }

    // PLAN JOURNEY BANNER
    private JPanel createJourneyBanner() {
        JPanel banner = new RoundedPanel(
                new Color(226, 241, 255),
                new Color(195, 222, 250));

        banner.setLayout(new BorderLayout(15, 0));
        banner.setBorder(new EmptyBorder(22, 25, 22, 20));
        banner.setPreferredSize(new Dimension(800, 265));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("P L A N   Y O U R   J O U R N E Y");
        eyebrow.setFont(new Font("Segoe UI", Font.BOLD, 11));
        eyebrow.setForeground(new Color(52, 83, 137));

        JLabel title = new JLabel("Plan Your Journey");
        title.setFont(new Font("Segoe UI", Font.BOLD, 29));
        title.setForeground(text);

        JLabel subtitle = new JLabel(
                "Choose source and destination stations.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(76, 99, 139));

        JButton plan = new JButton("\u25A4    Plan Journey    \u2192");
        stylePrimaryButton(plan);
        plan.setAlignmentX(Component.LEFT_ALIGNMENT);
        plan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        plan.setPreferredSize(new Dimension(300, 46));
        plan.addActionListener(e -> showPage("Plan Journey"));

        left.add(eyebrow);
        left.add(Box.createVerticalStrut(10));
        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(subtitle);
        left.add(Box.createVerticalStrut(24));
        left.add(plan);

        banner.add(left, BorderLayout.CENTER);
        banner.add(new MetroIllustration(), BorderLayout.EAST);

        return banner;
    }

    // FEATURE CARDS
    private JPanel quickCard(
            String symbol,
            String heading,
            String description,
            Color iconBackground,
            Runnable action) {

        JPanel card = new RoundedPanel(Color.WHITE, border);
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(16, 16, 16, 14));

        JLabel icon = new JLabel(symbol, SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Symbol", Font.BOLD, 27));
        icon.setForeground(blue);
        icon.setOpaque(true);
        icon.setBackground(iconBackground);
        icon.setPreferredSize(new Dimension(68, 68));

        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(heading);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(text);

        JLabel detail = new JLabel(
                "<html><div style='width:190px;'>"
                        + description + "</div></html>");
        detail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detail.setForeground(muted);

        labels.add(title);
        labels.add(Box.createVerticalStrut(7));
        labels.add(detail);

        JButton open = new JButton("Open  \u2192");
        open.setFont(new Font("Segoe UI", Font.BOLD, 12));
        open.setForeground(blue);
        open.setBackground(new Color(232, 243, 255));
        open.setFocusPainted(false);
        open.setBorderPainted(false);
        open.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        open.addActionListener(e -> action.run());

        card.add(icon, BorderLayout.WEST);
        card.add(labels, BorderLayout.CENTER);
        card.add(open, BorderLayout.EAST);

        return card;
    }


    // MODERN PROFILE PAGE
    private JPanel createProfilePanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(background);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));
        

        String name = passenger == null
                || passenger.getName() == null
                ? "Passenger" : passenger.getName();

        String email = passenger == null
                || passenger.getEmail() == null
                ? "Not provided" : passenger.getEmail();

        String phone = passenger == null
                || passenger.getPhone() == null
                ? "Not provided" : passenger.getPhone();

        // Main profile card
        JPanel mainCard = new RoundedPanel(Color.WHITE, border);
        mainCard.setMinimumSize(new Dimension(850, 600));
        mainCard.setLayout(new BorderLayout(0, 28));
        mainCard.setBorder(new EmptyBorder(30, 32, 30, 32));
        mainCard.setPreferredSize(new Dimension(1100, 650));

        // Page heading
        JPanel pageHeading = new JPanel();
        pageHeading.setOpaque(false);
        pageHeading.setLayout(
                new BoxLayout(pageHeading, BoxLayout.Y_AXIS));

        JLabel pageTitle = new JLabel("My Profile");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        pageTitle.setForeground(text);

        JLabel pageSubtitle = new JLabel(
                "Manage your personal information and account details.");
        pageSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pageSubtitle.setForeground(muted);

        pageHeading.add(pageTitle);
        pageHeading.add(Box.createVerticalStrut(7));
        pageHeading.add(pageSubtitle);

        // Profile header
        JPanel profileHeader = new JPanel(new BorderLayout(20, 0));
        profileHeader.setBackground(new Color(242, 247, 255));
        profileHeader.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel avatar = new JLabel(
                initials(name), SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(blue);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 27));
        avatar.setPreferredSize(new Dimension(84, 84));

        JPanel userDetails = new JPanel();
        userDetails.setOpaque(false);
        userDetails.setLayout(
                new BoxLayout(userDetails, BoxLayout.Y_AXIS));

        JLabel userName = new JLabel(name);
        userName.setFont(new Font("Segoe UI", Font.BOLD, 23));
        userName.setForeground(text);

        JLabel userRole = new JLabel("Metro Passenger");
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userRole.setForeground(muted);

        JLabel status = new JLabel("  ACTIVE ACCOUNT  ");
        status.setFont(new Font("Segoe UI", Font.BOLD, 11));
        status.setForeground(new Color(20, 130, 80));
        status.setBackground(new Color(220, 247, 234));
        status.setOpaque(true);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        userDetails.add(userName);
        userDetails.add(Box.createVerticalStrut(6));
        userDetails.add(userRole);
        userDetails.add(Box.createVerticalStrut(12));
        userDetails.add(status);

        JButton editButton = new JButton("Edit Profile");
        stylePrimaryButton(editButton);
        editButton.setPreferredSize(new Dimension(135, 42));

        editButton.addActionListener(e -> showEditProfileDialog(
                name, email, phone));

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(editButton);

        profileHeader.add(avatar, BorderLayout.WEST);
        profileHeader.add(userDetails, BorderLayout.CENTER);
        profileHeader.add(buttonPanel, BorderLayout.EAST);

        // Personal information
        JLabel infoTitle = new JLabel("Personal Information");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        infoTitle.setForeground(text);

        JLabel infoSubtitle = new JLabel(
                "Your registered contact information");
        infoSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoSubtitle.setForeground(muted);

        JPanel infoHeading = new JPanel();
        infoHeading.setOpaque(false);
        infoHeading.setLayout(
                new BoxLayout(infoHeading, BoxLayout.Y_AXIS));

        infoHeading.add(infoTitle);
        infoHeading.add(Box.createVerticalStrut(5));
        infoHeading.add(infoSubtitle);

        JPanel infoGrid = new JPanel(new GridLayout(0, 2, 16, 16));
        infoGrid.setOpaque(false);

        infoGrid.add(createProfileInfo(
                "\u263A", "FULL NAME", name));

        infoGrid.add(createProfileInfo(
                "\u2709", "EMAIL ADDRESS", email));

        infoGrid.add(createProfileInfo(
                "\u260E", "PHONE NUMBER", phone));

        infoGrid.add(createProfileInfo(
                "\u25CE", "ACCOUNT TYPE", "Passenger"));

        JPanel infoSection = new JPanel(new BorderLayout(0, 18));
        infoSection.setOpaque(false);
        infoSection.add(infoHeading, BorderLayout.NORTH);
        infoSection.add(infoGrid, BorderLayout.CENTER);

        // Arrange content
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(
                new BoxLayout(content, BoxLayout.Y_AXIS));

        pageHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(pageHeading);
        content.add(Box.createVerticalStrut(25));
        content.add(profileHeader);
        content.add(Box.createVerticalStrut(30));
        content.add(infoSection);

        mainCard.add(content, BorderLayout.NORTH);

        panel.add(mainCard);
        return panel;
    }

    // PROFILE INFORMATION CARD
    private JPanel createProfileInfo(
            String symbol, String label, String value) {

        JPanel card = new RoundedPanel(Color.WHITE, border);
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(new EmptyBorder(20, 18, 20, 18));
        card.setPreferredSize(new Dimension(420, 125));

        JLabel icon = new JLabel(symbol, SwingConstants.CENTER);
        icon.setOpaque(true);
        icon.setBackground(new Color(229, 240, 255));
        icon.setForeground(blue);
        icon.setFont(new Font("Segoe UI Symbol", Font.BOLD, 25));
        icon.setPreferredSize(new Dimension(65, 65));

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(
                new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel(label);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 11));
        heading.setForeground(muted);

        JLabel valueLabel = new JLabel(
                "<html><div style='width:220px;'>"
                        + escapeHtml(value)
                        + "</div></html>");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(text);

        details.add(Box.createVerticalGlue());
        details.add(heading);
        details.add(Box.createVerticalStrut(8));
        details.add(valueLabel);
        details.add(Box.createVerticalGlue());

        card.add(icon, BorderLayout.WEST);
        card.add(details, BorderLayout.CENTER);

        return card;
    }

    // EDIT PROFILE FORM

private void showEditProfileDialog(
        String currentName,
        String currentEmail,
        String currentPhone) {

    JTextField nameField = new JTextField(currentName, 25);
    JTextField emailField = new JTextField(currentEmail, 25);
    JTextField phoneField = new JTextField(currentPhone, 25);

    JPanel form = new JPanel(new GridLayout(0, 1, 5, 6));

    form.add(new JLabel("Full Name:"));
    form.add(nameField);

    form.add(new JLabel("Email Address:"));
    form.add(emailField);

    form.add(new JLabel("Phone Number:"));
    form.add(phoneField);

    int choice = JOptionPane.showConfirmDialog(
            this,
            form,
            "Edit Profile",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (choice != JOptionPane.OK_OPTION) {
        return;
    }

    String newName = nameField.getText().trim();
    String newEmail = emailField.getText().trim();
    String newPhone = phoneField.getText().trim();

    if (newName.isEmpty()
            || newEmail.isEmpty()
            || newPhone.isEmpty()) {

        JOptionPane.showMessageDialog(
                this,
                "Please fill in all fields.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
        JOptionPane.showMessageDialog(
                this,
                "Please enter a valid email address.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    if (!newPhone.matches("[0-9]{10}")) {
        JOptionPane.showMessageDialog(
                this,
                "Phone number must contain exactly 10 digits.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    boolean updated = PassengerService.updatePassengerProfile(
            currentEmail,
            newName,
            newEmail,
            newPhone
    );

    if (updated) {
        passenger.setName(newName);
        passenger.setEmail(newEmail);
        passenger.setUsername(newEmail);
        passenger.setPhone(newPhone);

        JOptionPane.showMessageDialog(
                this,
                "Profile updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Refresh the profile screen using updated passenger details.
        contentPanel.add(createProfilePanel(), "Profile");
        cardLayout.show(contentPanel, "Profile");
        contentPanel.revalidate();
        contentPanel.repaint();

    } else {
        JOptionPane.showMessageDialog(
                this,
                "Profile update failed.\n"
                + "Please check your database connection and "
                + "ensure the current email exists.",
                "Update Failed",
                JOptionPane.ERROR_MESSAGE
        );
    }
}


    // HELPERS
    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(blue);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private String initials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "P";
        }

        String[] parts = name.trim().split("\\s+");
        String result = parts[0].substring(0, 1).toUpperCase();

        if (parts.length > 1) {
            result += parts[parts.length - 1]
                    .substring(0, 1).toUpperCase();
        }

        return result;
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private void showPage(String page) {
        cardLayout.show(contentPanel, page);
    }

    // LOGOUT
    private void logout() {
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (answer == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }

    // ROUNDED PANEL
    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color outline;

        RoundedPanel(Color fill, Color outline) {
            this.fill = fill;
            this.outline = outline;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth() - 2;
            int h = getHeight() - 2;

            g.setColor(fill);
            g.fill(new RoundRectangle2D.Float(1, 1, w, h, 20, 20));

            g.setColor(outline);
            g.setStroke(new BasicStroke(1f));
            g.draw(new RoundRectangle2D.Float(1, 1, w, h, 20, 20));

            g.dispose();
            super.paintComponent(graphics);
        }
    }

    // METRO ILLUSTRATION
    private static class MetroIllustration extends JPanel {
        MetroIllustration() {
            setOpaque(false);
            setPreferredSize(new Dimension(250, 190));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g.setStroke(new BasicStroke(4f));
            g.setColor(new Color(160, 210, 250));
            g.drawLine(8, 35, w / 2, 35);
            g.drawLine(w / 2, 35, w - 15, 72);

            g.setColor(new Color(36, 131, 239));
            g.fillOval(22, 29, 12, 12);
            g.fillOval(w / 2 - 6, 29, 12, 12);
            g.fillOval(w - 24, 66, 12, 12);

            g.setColor(new Color(185, 218, 248));
            g.fillRect(w / 2 - 10, h - 85, 25, 85);
            g.fillRect(w / 2 + 18, h - 110, 30, 110);
            g.fillRect(w / 2 + 52, h - 65, 25, 65);
            g.fillRect(w / 2 + 81, h - 95, 28, 95);
            g.fillRect(w / 2 + 113, h - 78, 24, 78);

            g.setColor(new Color(89, 152, 211));
            g.setStroke(new BasicStroke(7f));
            g.drawLine(0, h - 35, w, h - 35);

            int tx = 6;
            int ty = h - 88;
            int tw = w - 12;

            g.setColor(new Color(35, 108, 189));
            g.fillRoundRect(tx, ty, tw, 48, 18, 18);

            g.setColor(new Color(244, 250, 255));
            g.fillRoundRect(tx + 8, ty + 7, tw - 16, 23, 8, 8);

            g.setColor(new Color(61, 139, 216));
            int count = Math.max(2, (tw - 30) / 31);

            for (int i = 0; i < count; i++) {
                int x = tx + 14 + i * ((tw - 25) / count);
                g.fillRoundRect(x, ty + 11, 18, 14, 4, 4);
            }

            g.setColor(Color.WHITE);
            g.fillOval(tx + 17, ty + 38, 12, 12);
            g.fillOval(tx + tw - 29, ty + 38, 12, 12);

            g.dispose();
        }
    }
}