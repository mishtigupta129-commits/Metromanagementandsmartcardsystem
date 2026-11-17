
package com.metromanagement.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StaffDashboard extends JFrame {

    private final String staffName;

    private final Color darkBlue = new Color(15, 52, 91);
    private final Color blue = new Color(20, 105, 220);
    private final Color background = new Color(245, 248, 252);
    private final Color textColor = new Color(35, 54, 77);
    private final Color borderColor = new Color(220, 230, 241);

    private JPanel contentPanel;
    private CardLayout cardLayout;

    public StaffDashboard(String staffName) {
        this.staffName = (staffName == null || staffName.trim().isEmpty())
                ? "Staff" : staffName.trim();

        setTitle("Metro & Smart Card Management System - Staff");
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(background);

        // ================= HEADER =================

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel brand = new JLabel(
                "METRO & SMART CARD MANAGEMENT SYSTEM");
        brand.setFont(new Font("Arial", Font.BOLD, 18));
        brand.setForeground(darkBlue);

        JLabel portal = new JLabel("STAFF PORTAL");
        portal.setFont(new Font("Arial", Font.BOLD, 11));
        portal.setForeground(blue);

        header.add(brand, BorderLayout.WEST);
        header.add(portal, BorderLayout.EAST);

        // ================= SIDEBAR =================

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(darkBlue);

        JPanel sidebarTop = new JPanel();
        sidebarTop.setBackground(darkBlue);
        sidebarTop.setLayout(
                new BoxLayout(sidebarTop, BoxLayout.Y_AXIS));
        sidebarTop.setBorder(new EmptyBorder(25, 12, 15, 12));

        JLabel logo = new JLabel("METRO SYSTEM");
        logo.setFont(new Font("Arial", Font.BOLD, 19));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel role = new JLabel("STAFF");
        role.setFont(new Font("Arial", Font.PLAIN, 12));
        role.setForeground(new Color(190, 211, 235));
        role.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel user = new JLabel(this.staffName);
        user.setFont(new Font("Arial", Font.BOLD, 14));
        user.setForeground(Color.WHITE);
        user.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebarTop.add(logo);
        sidebarTop.add(Box.createVerticalStrut(8));
        sidebarTop.add(role);
        sidebarTop.add(Box.createVerticalStrut(22));
        sidebarTop.add(user);
        sidebarTop.add(Box.createVerticalStrut(28));

        JPanel nav = new JPanel(new GridLayout(0, 1, 0, 5));
        nav.setBackground(darkBlue);

        String[] pages = {
            "Dashboard",
            "Passengers",
            "Smart Cards",
            "Stations & Routes",
            "Tickets",
            "Transaction History",
            "Reports",
            "Profile"
        };

        for (String page : pages) {
            JButton button = createNavButton(page);
            button.addActionListener(e -> showPage(page));
            nav.add(button);
        }

        sidebarTop.add(nav);

        JButton logout = createNavButton("Logout");
        logout.addActionListener(e -> logout());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(darkBlue);
        bottom.setBorder(new EmptyBorder(10, 10, 20, 10));
        bottom.add(logout, BorderLayout.CENTER);

        sidebar.add(sidebarTop, BorderLayout.NORTH);
        sidebar.add(bottom, BorderLayout.SOUTH);

        // ================= CONTENT =================

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(background);

        contentPanel.add(createHomePanel(), "Dashboard");

        contentPanel.add(createModulePanel(
                "Passenger Management",
                "View and search registered passenger records."
        ), "Passengers");

        contentPanel.add(createModulePanel(
                "Smart Card Management",
                "Manage virtual cards, card status and balances."
        ), "Smart Cards");

        contentPanel.add(createModulePanel(
                "Stations & Routes",
                "Manage metro stations, routes and fare information."
        ), "Stations & Routes");

        contentPanel.add(createModulePanel(
                "Ticket Management",
                "View passenger bookings and ticket status."
        ), "Tickets");

        contentPanel.add(createModulePanel(
                "Transaction History",
                "View recharge, fare deduction and refund records."
        ), "Transaction History");

        contentPanel.add(createModulePanel(
                "Reports & Revenue",
                "Review transaction totals and revenue reports."
        ), "Reports");

        contentPanel.add(createProfilePanel(), "Profile");

        root.add(header, BorderLayout.NORTH);
        root.add(sidebar, BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);
    }

    // ================= HOME =================

    private JPanel createHomePanel() {
        JPanel home = new JPanel(new BorderLayout(18, 22));
        home.setBackground(background);
        home.setBorder(new EmptyBorder(28, 28, 28, 28));

        JPanel heading = new JPanel();
        heading.setLayout(
                new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(background);

        JLabel title = new JLabel("Staff Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(darkBlue);

        JLabel welcome = new JLabel(
                "Welcome back, " + staffName + "!");
        welcome.setFont(new Font("Arial", Font.PLAIN, 15));
        welcome.setForeground(new Color(100, 115, 135));

        heading.add(title);
        heading.add(Box.createVerticalStrut(8));
        heading.add(welcome);

        JPanel features = new JPanel(new GridLayout(3, 2, 15, 15));
        features.setBackground(background);

        features.add(createFeatureTile(
                "Passenger Management",
                "View and search passenger records.",
                () -> showPage("Passengers")));

        features.add(createFeatureTile(
                "Smart Cards",
                "Manage cards and check their status.",
                () -> showPage("Smart Cards")));

        features.add(createFeatureTile(
                "Stations & Routes",
                "Manage stations and metro routes.",
                () -> showPage("Stations & Routes")));

        features.add(createFeatureTile(
                "Ticket Management",
                "View tickets and booking records.",
                () -> showPage("Tickets")));

        features.add(createFeatureTile(
                "Transaction History",
                "Review card transactions.",
                () -> showPage("Transaction History")));

        features.add(createFeatureTile(
                "Reports & Revenue",
                "View transaction and collection reports.",
                () -> showPage("Reports")));

        home.add(heading, BorderLayout.NORTH);
        home.add(features, BorderLayout.CENTER);

        return home;
    }

    // ================= FEATURE TILE =================

    private JPanel createFeatureTile(
            String titleText,
            String description,
            Runnable action) {

        JPanel tile = new JPanel(new BorderLayout(0, 12));
        tile.setBackground(Color.WHITE);
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(18, 18, 18, 18)));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(darkBlue);

        JLabel detail = new JLabel(
                "<html><div style='width:240px;'>"
                        + description + "</div></html>");
        detail.setFont(new Font("Arial", Font.PLAIN, 12));
        detail.setForeground(textColor);

        JButton open = new JButton("Open");
        stylePrimaryButton(open);
        open.addActionListener(e -> action.run());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(open);

        tile.add(title, BorderLayout.NORTH);
        tile.add(detail, BorderLayout.CENTER);
        tile.add(buttonPanel, BorderLayout.SOUTH);

        return tile;
    }

    // ================= MODULE PAGE =================

    private JPanel createModulePanel(
            String heading,
            String description) {

        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(background);

        JPanel card = new JPanel();
        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(32, 32, 32, 32)));

        JLabel title = new JLabel(heading);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(darkBlue);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detail = new JLabel(
                "<html><div style='width:360px;'>"
                        + description + "</div></html>");
        detail.setFont(new Font("Arial", Font.PLAIN, 14));
        detail.setForeground(textColor);
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel status = new JLabel(
                "Module ready for implementation.");
        status.setFont(new Font("Arial", Font.PLAIN, 12));
        status.setForeground(new Color(100, 115, 135));
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(18));
        card.add(detail);
        card.add(Box.createVerticalStrut(22));
        card.add(status);

        page.add(card);
        return page;
    }

    // ================= PROFILE =================

    private JPanel createProfilePanel() {
        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(background);

        JPanel card = new JPanel();
        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(30, 35, 30, 35)));

        JLabel title = new JLabel("Staff Profile");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(darkBlue);

        JLabel name = new JLabel("Staff Name: " + staffName);
        name.setFont(new Font("Arial", Font.PLAIN, 15));
        name.setForeground(textColor);

        JLabel description = new JLabel(
                "Role: Staff");
        description.setFont(new Font("Arial", Font.PLAIN, 15));
        description.setForeground(textColor);

        card.add(title);
        card.add(Box.createVerticalStrut(25));
        card.add(name);
        card.add(Box.createVerticalStrut(15));
        card.add(description);

        page.add(card);
        return page;
    }

    // ================= NAVIGATION BUTTON =================

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(darkBlue);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(12, 15, 12, 8));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(25, 78, 128));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(darkBlue);
            }
        });

        return button;
    }

    // ================= PRIMARY BUTTON =================

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(blue);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(110, 36));
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ================= NAVIGATION =================

    private void showPage(String page) {
        cardLayout.show(contentPanel, page);
    }

    // ================= LOGOUT =================

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }

    // ================= OPEN DASHBOARD =================

    public static void showDashboard(String staffName) {
        SwingUtilities.invokeLater(
                () -> new StaffDashboard(staffName));
    }
}
