
package com.metromanagement.main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.metromanagement.model.Passenger;
import com.metromanagement.gui.LoginFrame;

public class PassengerDashboard {

    private static final Color NAVY = new Color(13, 43, 78);
    private static final Color BLUE = new Color(20, 105, 220);
    private static final Color BG = new Color(243, 248, 254);
    private static final Color TEXT = new Color(17, 36, 79);
    private static final Color MUTED = new Color(98, 116, 148);
    private static final Color BORDER = new Color(218, 231, 246);

    private static JFrame frame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;

    public static void showDashboard(Passenger passenger) {

        String passengerName = passenger == null
                || passenger.getName() == null
                ? "Passenger" : passenger.getName();

        frame = new JFrame("Metro & Smart Card Management System");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1450, 900);
        frame.setMinimumSize(new Dimension(1100, 720));
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // SIDEBAR
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(265, 0));
        sidebar.setBackground(NAVY);

        JPanel sideTop = new JPanel();
        sideTop.setOpaque(false);
        sideTop.setLayout(new BoxLayout(sideTop, BoxLayout.Y_AXIS));
        sideTop.setBorder(new EmptyBorder(28, 13, 20, 13));

        JLabel trainLogo = new JLabel("\u25CE");
        trainLogo.setFont(new Font("Arial", Font.BOLD, 48));
        trainLogo.setForeground(Color.WHITE);
        trainLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brand = new JLabel("METRO SYSTEM");
        brand.setFont(new Font("Arial", Font.BOLD, 22));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel role = new JLabel("P A S S E N G E R");
        role.setFont(new Font("Arial", Font.PLAIN, 12));
        role.setForeground(new Color(190, 211, 235));
        role.setAlignmentX(Component.CENTER_ALIGNMENT);

        sideTop.add(trainLogo);
        sideTop.add(Box.createVerticalStrut(8));
        sideTop.add(brand);
        sideTop.add(Box.createVerticalStrut(8));
        sideTop.add(role);
        sideTop.add(Box.createVerticalStrut(38));

        JPanel navigation = new JPanel();
        navigation.setOpaque(false);
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));

        String[][] navItems = {
            {"\u2302", "Dashboard"},
            {"\u25A3", "Smart Cards"},
            {"\u21C4", "Plan Journey"},
            {"\u25CE", "Station Details"},
            {"\u25C8", "Tickets"},
            {"\u2637", "Transaction History"},
            {"\u263A", "Profile"}
        };

        for (String[] item : navItems) {
            JButton nav = createNavButton(item[0], item[1]);
            nav.setAlignmentX(Component.LEFT_ALIGNMENT);
            nav.setMaximumSize(new Dimension(Integer.MAX_VALUE, 53));

            nav.addActionListener(e -> openSection(item[1], passenger));

            navigation.add(nav);
            navigation.add(Box.createVerticalStrut(7));
        }

        sideTop.add(navigation);

        JButton logout = createNavButton("\u21AA", "Logout");
        logout.addActionListener(e -> {
            int answer = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
            );

            if (answer == JOptionPane.YES_OPTION) {
                frame.dispose();
                new LoginFrame();
            }
        });

        JPanel sideBottom = new JPanel(new BorderLayout());
        sideBottom.setOpaque(false);
        sideBottom.setBorder(new EmptyBorder(10, 13, 22, 13));
        sideBottom.add(new JSeparator(), BorderLayout.NORTH);
        sideBottom.add(logout, BorderLayout.CENTER);

        sidebar.add(sideTop, BorderLayout.NORTH);
        sidebar.add(sideBottom, BorderLayout.SOUTH);

        // PAGE LAYOUT
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG);

        mainPanel.add(createHomePage(passengerName), "Dashboard");

        mainPanel.add(createInfoPage(
            "My Smart Cards",
            "Manage your virtual smart cards and recharge balance.",
            "\u25A3",
            () -> showMessage("Connect this button to your Smart Card screen.")
        ), "Smart Cards");

        mainPanel.add(createInfoPage(
            "Plan Your Journey",
            "Choose your source and destination stations.",
            "\u21C4",
            () -> showMessage("Connect this button to your Journey Planner.")
        ), "Plan Journey");

        mainPanel.add(createInfoPage(
            "Station Details",
            "Explore metro lines and their stations.",
            "\u25CE",
            () -> showMessage("Connect this button to your Station Details screen.")
        ), "Station Details");

        mainPanel.add(createInfoPage(
            "Metro Tickets",
            "Book and view your metro tickets.",
            "\u25C8",
            () -> showMessage("Connect this button to your Ticket screen.")
        ), "Tickets");

        mainPanel.add(createInfoPage(
            "Transaction History",
            "View your smart card transaction records.",
            "\u2637",
            () -> showMessage("Connect this button to your Transaction History screen.")
        ), "Transaction History");

        mainPanel.add(createInfoPage(
            "My Profile",
            "Welcome, " + passengerName + ".",
            "\u263A",
            () -> showMessage("Profile: " + passengerName)
        ), "Profile");

        root.add(sidebar, BorderLayout.WEST);
        root.add(mainPanel, BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // ================= DASHBOARD HOME =================

    private static JPanel createHomePage(String name) {

        JPanel page = new JPanel(new BorderLayout(0, 22));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(27, 30, 25, 30));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel headings = new JPanel();
        headings.setOpaque(false);
        headings.setLayout(new BoxLayout(headings, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Passenger Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 34));
        title.setForeground(TEXT);

        JLabel welcome = new JLabel("Welcome back, " + name + "!");
        welcome.setFont(new Font("Arial", Font.PLAIN, 17));
        welcome.setForeground(MUTED);

        headings.add(title);
        headings.add(Box.createVerticalStrut(5));
        headings.add(welcome);

        JPanel user = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        user.setOpaque(false);

        JLabel notification = new JLabel("\u2667");
        notification.setFont(new Font("Arial", Font.PLAIN, 29));
        notification.setForeground(NAVY);

        JLabel avatar = new JLabel(getInitials(name), SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(BLUE);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Arial", Font.BOLD, 17));
        avatar.setPreferredSize(new Dimension(48, 48));

        user.add(notification);
        user.add(avatar);

        header.add(headings, BorderLayout.WEST);
        header.add(user, BorderLayout.EAST);

        // SUMMARY CARDS
        JPanel summaries = new JPanel(new GridLayout(1, 3, 18, 0));
        summaries.setOpaque(false);

        summaries.add(createSummaryCard(
            "\u25A3", "Smart Cards", "Manage your virtual cards",
            new Color(224, 239, 255),
            () -> openSection("Smart Cards", null)
        ));

        summaries.add(createSummaryCard(
            "\u25C8", "Active Tickets", "View your bookings",
            new Color(220, 248, 241),
            () -> openSection("Tickets", null)
        ));

        summaries.add(createSummaryCard(
            "\u25CE", "Metro Stations", "Explore metro routes",
            new Color(255, 240, 216),
            () -> openSection("Station Details", null)
        ));

        // JOURNEY BANNER
        JPanel journey = createJourneyBanner();

        // QUICK ACCESS
        JPanel quick = new JPanel(new GridLayout(2, 2, 18, 16));
        quick.setOpaque(false);

        quick.add(createQuickCard(
            "\u25A3", "Smart Cards",
            "Issue or manage your cards and recharge balance.",
            new Color(224, 239, 255),
            () -> openSection("Smart Cards", null)
        ));

        quick.add(createQuickCard(
            "\u25C8", "Tickets",
            "Book, view and cancel eligible metro tickets.",
            new Color(220, 248, 241),
            () -> openSection("Tickets", null)
        ));

        quick.add(createQuickCard(
            "\u25CE", "Station Details",
            "Explore metro lines and their terminal stations.",
            new Color(255, 240, 216),
            () -> openSection("Station Details", null)
        ));

        quick.add(createQuickCard(
            "\u2637", "Transaction History",
            "View your smart card transaction records.",
            new Color(239, 232, 255),
            () -> openSection("Transaction History", null)
        ));

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 20, 0);

        c.gridy = 0;
        content.add(summaries, c);

        c.gridy = 1;
        content.add(journey, c);

        c.gridy = 2;
        c.insets = new Insets(0, 0, 0, 0);
        content.add(quick, c);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getViewport().setBackground(BG);

        page.add(header, BorderLayout.NORTH);
        page.add(scroll, BorderLayout.CENTER);

        return page;
    }

    // ================= JOURNEY BANNER =================

    private static JPanel createJourneyBanner() {

        JPanel banner = new JPanel(new BorderLayout(15, 0));
        banner.setBackground(new Color(226, 241, 255));
        banner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(194, 221, 249)),
            new EmptyBorder(23, 26, 23, 22)
        ));
        banner.setPreferredSize(new Dimension(800, 280));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("P L A N   Y O U R   J O U R N E Y");
        eyebrow.setFont(new Font("Arial", Font.BOLD, 12));
        eyebrow.setForeground(new Color(52, 83, 137));

        JLabel title = new JLabel("Plan Your Journey");
        title.setFont(new Font("Arial", Font.BOLD, 31));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
            "Choose source and destination stations."
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(new Color(76, 99, 139));

        // UI-only selectors. Connect to JourneyFrame in the next step.
        String[] stations = {
            "Select station", "Rajiv Chowk", "Kashmere Gate",
            "New Delhi", "Central Secretariat",
            "Kalkaji Mandir", "Nehru Enclave", "Huda City Centre"
        };

        JComboBox<String> from = new JComboBox<>(stations);
        JComboBox<String> to = new JComboBox<>(stations);

        styleCombo(from);
        styleCombo(to);

        JPanel fields = new JPanel(new GridLayout(1, 2, 12, 0));
        fields.setOpaque(false);
        fields.add(from);
        fields.add(to);

        JButton planButton = new JButton("\u25A4    Plan Journey    \u2192");
        stylePrimary(planButton);
        planButton.setFont(new Font("Arial", Font.BOLD, 15));
        planButton.setPreferredSize(new Dimension(300, 48));
        planButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        planButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        planButton.addActionListener(e -> {
            if (from.getSelectedIndex() == 0 || to.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(
                    frame, "Please select source and destination."
                );
            } else if (from.getSelectedItem().equals(to.getSelectedItem())) {
                JOptionPane.showMessageDialog(
                    frame, "Source and destination must be different."
                );
            } else {
                openSection("Plan Journey", null);
            }
        });

        left.add(eyebrow);
        left.add(Box.createVerticalStrut(10));
        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(subtitle);
        left.add(Box.createVerticalStrut(20));
        left.add(fields);
        left.add(Box.createVerticalStrut(14));
        left.add(planButton);

        banner.add(left, BorderLayout.CENTER);
        banner.add(new MetroArt(), BorderLayout.EAST);

        return banner;
    }

    // ================= SUMMARY CARD =================

    private static JPanel createSummaryCard(
            String iconText, String heading, String subtitle,
            Color iconBg, Runnable action) {

        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(17, 17, 17, 12)
        ));

        JLabel icon = new JLabel(iconText, SwingConstants.CENTER);
        icon.setFont(new Font("Arial", Font.BOLD, 31));
        icon.setForeground(BLUE);
        icon.setOpaque(true);
        icon.setBackground(iconBg);
        icon.setPreferredSize(new Dimension(70, 70));

        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(heading);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(TEXT);

        JLabel detail = new JLabel(
            "<html><div style='width:145px;'>" + subtitle + "</div></html>"
        );
        detail.setFont(new Font("Arial", Font.PLAIN, 13));
        detail.setForeground(MUTED);

        labels.add(title);
        labels.add(Box.createVerticalStrut(7));
        labels.add(detail);

        JButton arrow = new JButton("\u203A");
        arrow.setFont(new Font("Arial", Font.BOLD, 30));
        arrow.setForeground(NAVY);
        arrow.setContentAreaFilled(false);
        arrow.setBorderPainted(false);
        arrow.setFocusPainted(false);
        arrow.addActionListener(e -> action.run());

        card.add(icon, BorderLayout.WEST);
        card.add(labels, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.EAST);

        return card;
    }

    // ================= QUICK ACCESS CARD =================

    private static JPanel createQuickCard(
            String iconText, String heading, String description,
            Color iconBg, Runnable action) {

        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(17, 18, 17, 16)
        ));

        JLabel icon = new JLabel(iconText, SwingConstants.CENTER);
        icon.setFont(new Font("Arial", Font.BOLD, 31));
        icon.setForeground(BLUE);
        icon.setOpaque(true);
        icon.setBackground(iconBg);
        icon.setPreferredSize(new Dimension(76, 76));

        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(heading);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(TEXT);

        JLabel detail = new JLabel(
            "<html><div style='width:180px;'>" + description + "</div></html>"
        );
        detail.setFont(new Font("Arial", Font.PLAIN, 13));
        detail.setForeground(MUTED);

        labels.add(title);
        labels.add(Box.createVerticalStrut(8));
        labels.add(detail);

        JButton open = new JButton("Open  \u2192");
        open.setFont(new Font("Arial", Font.BOLD, 13));
        open.setForeground(BLUE);
        open.setBackground(new Color(232, 243, 255));
        open.setFocusPainted(false);
        open.setBorderPainted(false);
        open.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        open.addActionListener(e -> action.run());

        card.add(icon, BorderLayout.WEST);
        card.add(labels, BorderLayout.CENTER);
        card.add(open, BorderLayout.EAST);

        return card;
    }

    // ================= NAVIGATION =================

    private static JButton createNavButton(String icon, String label) {

        JButton button = new JButton(icon + "     " + label);
        button.setFont(new Font("Arial", Font.PLAIN, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(NAVY);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(15, 18, 15, 10));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(25, 78, 128));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(NAVY);
            }
        });

        return button;
    }

    private static void stylePrimary(JButton button) {
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(new Color(54, 76, 115));
        combo.setPreferredSize(new Dimension(180, 43));
    }

    // ================= OTHER PAGES =================

    private static JPanel createInfoPage(
            String heading, String description, String icon, Runnable action) {

        JPanel page = new JPanel(new GridBagLayout());
        page.setBackground(BG);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(35, 40, 35, 40)
        ));

        JLabel symbol = new JLabel(icon);
        symbol.setFont(new Font("Arial", Font.BOLD, 44));
        symbol.setForeground(BLUE);
        symbol.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(heading);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detail = new JLabel(
            "<html><div style='width:350px;'>" + description + "</div></html>"
        );
        detail.setFont(new Font("Arial", Font.PLAIN, 15));
        detail.setForeground(MUTED);
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton open = new JButton("Open");
        stylePrimary(open);
        open.setPreferredSize(new Dimension(250, 43));
        open.setMaximumSize(new Dimension(250, 43));
        open.setAlignmentX(Component.LEFT_ALIGNMENT);
        open.addActionListener(e -> action.run());

        card.add(symbol);
        card.add(Box.createVerticalStrut(15));
        card.add(title);
        card.add(Box.createVerticalStrut(12));
        card.add(detail);
        card.add(Box.createVerticalStrut(25));
        card.add(open);

        page.add(card);
        return page;
    }

    private static void openSection(String section, Passenger passenger) {
        cardLayout.show(mainPanel, section);
    }

    private static void showMessage(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    private static String getInitials(String name) {
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

    // Simple vector metro illustration; no external image needed.
    private static class MetroArt extends JPanel {

        MetroArt() {
            setOpaque(false);
            setPreferredSize(new Dimension(280, 205));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();

            g.setColor(new Color(170, 215, 251));
            g.setStroke(new BasicStroke(4));
            g.drawLine(5, 48, w / 2, 48);
            g.drawLine(w / 2, 48, w - 15, 88);

            g.setColor(new Color(38, 130, 232));
            g.fillOval(24, 42, 12, 12);
            g.fillOval(w / 2 - 6, 42, 12, 12);
            g.fillOval(w - 24, 82, 12, 12);

            g.setColor(new Color(187, 221, 249));
            g.fillRect(w / 2 - 5, h - 95, 27, 95);
            g.fillRect(w / 2 + 28, h - 120, 35, 120);
            g.fillRect(w / 2 + 68, h - 75, 26, 75);
            g.fillRect(w / 2 + 100, h - 105, 32, 105);

            g.setColor(new Color(89, 152, 211));
            g.setStroke(new BasicStroke(8));
            g.drawLine(0, h - 43, w, h - 43);

            int tx = 12;
            int ty = h - 102;
            int tw = Math.max(130, w - 35);

            g.setColor(new Color(35, 108, 189));
            g.fillRoundRect(tx, ty, tw, 53, 20, 20);

            g.setColor(new Color(244, 250, 255));
            g.fillRoundRect(tx + 9, ty + 8, tw - 18, 25, 9, 9);

            g.setColor(new Color(61, 139, 216));
            int count = Math.max(2, (tw - 35) / 32);
            for (int i = 0; i < count; i++) {
                int x = tx + 16 + i * ((tw - 30) / count);
                g.fillRoundRect(x, ty + 12, 19, 16, 4, 4);
            }

            g.setColor(Color.WHITE);
            g.fillOval(tx + 20, ty + 42, 14, 14);
            g.fillOval(tx + tw - 36, ty + 42, 14, 14);

            g.dispose();
        }
    }
}
