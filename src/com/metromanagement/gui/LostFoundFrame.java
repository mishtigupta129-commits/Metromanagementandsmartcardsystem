
package com.metromanagement.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.metromanagement.database.DBConnection;

public class LostFoundFrame extends JFrame {

    private final Color cyan = new Color(0, 181, 225);
    private final Color navy = new Color(20, 43, 72);
    private final Color bg = new Color(244, 247, 251);
    private final Color outline = new Color(216, 222, 230);

    private JRadioButton lostRadio;
    private JRadioButton foundRadio;

    private final List<String[]> reports = new ArrayList<>();

    public LostFoundFrame() {
        setTitle("Metro & Smart Card Management System - Lost & Found");
        setSize(1050, 800);
        setMinimumSize(new Dimension(760, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bg);
        root.add(createHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(bg);
        content.setBorder(new EmptyBorder(22, 26, 25, 26));

        content.add(createReportCard());
        content.add(Box.createVerticalStrut(20));
        content.add(createInstructionHeading());
        content.add(Box.createVerticalStrut(12));
        content.add(createInstructionCard());
        content.add(Box.createVerticalStrut(20));
        content.add(createDMRCHeading());
        content.add(Box.createVerticalStrut(16));
        content.add(createOfficeCard());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(bg);

        root.add(scroll, BorderLayout.CENTER);
        setContentPane(root);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ================= HEADER =================

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 27, 15, 27));

        JButton back = new JButton("\u2190");
        back.setFont(new Font("Segoe UI", Font.PLAIN, 27));
        back.setForeground(navy);
        back.setBorderPainted(false);
        back.setContentAreaFilled(false);
        back.setFocusPainted(false);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> dispose());

        JLabel title = new JLabel("Lost & Found");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(navy);

        header.add(back, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);

        return header;
    }

    // ================= REPORT CARD =================

    private JPanel createReportCard() {
        RoundedPanel card = new RoundedPanel(Color.WHITE, outline, 20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel("Complain Lost & Found");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 21));
        heading.setForeground(navy);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(heading);
        card.add(Box.createVerticalStrut(18));

        lostRadio = new JRadioButton("Report Lost Item", true);
        foundRadio = new JRadioButton("Report Found Item");

        styleRadio(lostRadio);
        styleRadio(foundRadio);

        ButtonGroup group = new ButtonGroup();
        group.add(lostRadio);
        group.add(foundRadio);

        JPanel choices = new JPanel(new GridLayout(1, 2, 20, 0));
        choices.setOpaque(false);
        choices.add(lostRadio);
        choices.add(foundRadio);
        choices.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        choices.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(choices);
        card.add(Box.createVerticalStrut(20));

        JPanel buttonHolder = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonHolder.setOpaque(false);
        buttonHolder.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonHolder.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 54));

        JButton listButton = new JButton("LIST OF LOST & FOUND ITEMS");
        styleButton(listButton);
        listButton.setPreferredSize(new Dimension(470, 52));
        listButton.addActionListener(e -> showReports());

        buttonHolder.add(listButton);
        card.add(buttonHolder);

        lostRadio.addActionListener(e -> openReportForm(true));
        foundRadio.addActionListener(e -> openReportForm(false));

        return card;
    }

    private void styleRadio(JRadioButton radio) {
        radio.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        radio.setForeground(navy);
        radio.setOpaque(false);
        radio.setFocusPainted(false);
        radio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ================= DATABASE STATIONS =================

    private String[] loadStationsFromDatabase() {
        List<String> stations = new ArrayList<>();
        stations.add("Select Station");

        String sql =
                "SELECT DISTINCT station_name " +
                "FROM metro_stations " +
                "WHERE station_name IS NOT NULL " +
                "AND TRIM(station_name) <> '' " +
                "ORDER BY station_name";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                String stationName = rs.getString("station_name");

                if (stationName != null
                        && !stationName.trim().isEmpty()) {
                    stations.add(stationName.trim());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load metro stations from the database.\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return stations.toArray(new String[0]);
    }

    // ================= COMPACT POP-UP FORM =================

    private void openReportForm(boolean isLost) {

        JDialog dialog = new JDialog(
                this,
                isLost ? "Report Lost Item" : "Report Found Item",
                true
        );

        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        dialog.getRootPane().registerKeyboardAction(
                e -> dialog.dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Close the pop-up when it loses focus.
        dialog.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // No action required.
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (dialog.isDisplayable() && dialog.isVisible()) {
                        dialog.dispose();
                    }
                });
            }
        });

        int dialogWidth = 550;
        int dialogHeight = 690;

        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setMinimumSize(
                new Dimension(dialogWidth, dialogHeight));
        dialog.setMaximumSize(
                new Dimension(dialogWidth, dialogHeight));

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(new EmptyBorder(1, 1, 1, 1));

        RoundedPanel card = new RoundedPanel(Color.WHITE, outline, 28);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 27, 20, 27));

        // Grey handle
        JPanel handleHolder = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 0, 0));
        handleHolder.setOpaque(false);
        handleHolder.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 10));

        JPanel handle = new RoundedPanel(
                new Color(204, 207, 212),
                new Color(204, 207, 212), 10);
        handle.setPreferredSize(new Dimension(58, 7));

        handleHolder.add(handle);
        card.add(handleHolder);
        card.add(Box.createVerticalStrut(15));

        JLabel title = new JLabel(
                isLost ? "Report Lost Item" : "Report Found Item");
        title.setFont(new Font("Segoe UI", Font.PLAIN, 27));
        title.setForeground(Color.BLACK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(9));

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(185, 190, 198));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(separator);
        card.add(Box.createVerticalStrut(12));

        JTextField itemName = createInput("Enter Item Name", 43);
        addField(card, itemName, 8);

        JTextArea description = new JTextArea();
        description.setText("Enter Item Description");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setForeground(new Color(105, 108, 115));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setRows(2);
        description.setBackground(Color.WHITE);
        description.setBorder(new EmptyBorder(10, 13, 8, 13));

        installAreaPlaceholder(description, "Enter Item Description");

        RoundedPanel descriptionBox = new RoundedPanel(
                Color.WHITE, outline, 13);
        descriptionBox.setLayout(new BorderLayout());
        descriptionBox.setBorder(new EmptyBorder(1, 1, 1, 1));
        descriptionBox.add(description, BorderLayout.CENTER);
        descriptionBox.setPreferredSize(new Dimension(480, 60));
        descriptionBox.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 60));
        descriptionBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(descriptionBox);
        card.add(Box.createVerticalStrut(8));

        JTextField quantity = createInput("Enter Item Quantity", 43);
        addField(card, quantity, 8);

        // Stations are loaded from MySQL for both Lost and Found forms.
        JComboBox<String> station =
                new JComboBox<>(loadStationsFromDatabase());

        styleCombo(station);
        station.setPreferredSize(new Dimension(480, 43));
        station.setMinimumSize(new Dimension(100, 43));
        station.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        station.setAlignmentX(Component.LEFT_ALIGNMENT);

        addField(card, station, 8);

        JTextField contactName = createInput("Enter your name", 43);
        addField(card, contactName, 8);

        JTextField mobile = createInput("Enter your mobile number", 43);
        addField(card, mobile, 8);

        JTextField date = createInput(
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")), 43);
        addLabeledField(card, "Date", date, 8);

        JTextField time = createInput(
                LocalTime.now().format(
                        DateTimeFormatter.ofPattern("hh:mm a")), 43);
        addLabeledField(card, "Time", time, 12);

        JButton submit = new JButton("SUBMIT");
        styleButton(submit);
        submit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        submit.setPreferredSize(new Dimension(480, 50));
        submit.setMinimumSize(new Dimension(100, 50));
        submit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        submit.setAlignmentX(Component.LEFT_ALIGNMENT);

        submit.addActionListener(e -> {

            String item = inputValue(itemName, "Enter Item Name");
            String details = description.getText().trim();
            String qty = inputValue(quantity, "Enter Item Quantity");
            String name = inputValue(contactName, "Enter your name");
            String phone = inputValue(
                    mobile, "Enter your mobile number");

            if (item.isEmpty()
                    || details.isEmpty()
                    || details.equals("Enter Item Description")
                    || qty.isEmpty()
                    || name.isEmpty()
                    || phone.isEmpty()
                    || station.getSelectedIndex() <= 0) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please fill in all fields.",
                        "Missing Details",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int quantityValue;

            try {
                quantityValue = Integer.parseInt(qty);

                if (quantityValue < 1) {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Enter a valid positive quantity.",
                        "Invalid Quantity",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Enter a valid 10-digit mobile number.",
                        "Invalid Mobile Number",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String selectedStation =
                    String.valueOf(station.getSelectedItem());

            boolean saved = saveReportToDatabase(
                    isLost ? "Lost" : "Found",
                    item,
                    details,
                    quantityValue,
                    selectedStation,
                    name,
                    phone,
                    date.getText().trim(),
                    time.getText().trim()
            );

            if (!saved) {
                return;
            }

            JOptionPane.showMessageDialog(
                    dialog,
                    "Your " + (isLost ? "lost" : "found")
                            + " item report has been saved successfully!",
                    "Report Submitted",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dialog.dispose();
        });
        card.add(submit);
        outer.add(card, BorderLayout.CENTER);
        dialog.setContentPane(outer);

        try {
            dialog.setShape(new RoundRectangle2D.Double(
                    0, 0, dialogWidth, dialogHeight, 30, 30));
        } catch (UnsupportedOperationException ignored) {
            // Use regular dialog shape if unsupported.
        }

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ================= INPUT COMPONENTS =================

    private JTextField createInput(String placeholder, int height) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(105, 108, 115));
        field.setBackground(Color.WHITE);
        field.setBorder(new EmptyBorder(9, 13, 9, 13));

        field.putClientProperty("placeholder", placeholder);
        installFieldPlaceholder(field, placeholder);

        field.setPreferredSize(new Dimension(480, height));
        field.setMinimumSize(new Dimension(100, height));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));

        return field;
    }

    private void installFieldPlaceholder(
            JTextField field, String placeholder) {

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(105, 108, 115));
                }
            }
        });
    }

    private void installAreaPlaceholder(
            JTextArea area, String placeholder) {

        area.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (area.getText().equals(placeholder)) {
                    area.setText("");
                    area.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (area.getText().trim().isEmpty()) {
                    area.setText(placeholder);
                    area.setForeground(new Color(105, 108, 115));
                }
            }
        });
    }

    private void addField(JPanel panel, JComponent field, int gap) {
        if (field instanceof JTextField) {
            JTextField textField = (JTextField) field;

            RoundedPanel wrapper = new RoundedPanel(
                    Color.WHITE, outline, 13);
            wrapper.setLayout(new BorderLayout());
            wrapper.setBorder(new EmptyBorder(1, 1, 1, 1));
            wrapper.add(textField, BorderLayout.CENTER);
            wrapper.setPreferredSize(new Dimension(480, 43));
            wrapper.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 43));
            wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

            textField.setBorder(new EmptyBorder(9, 13, 9, 13));
            panel.add(wrapper);
        } else {
            panel.add(field);
        }

        panel.add(Box.createVerticalStrut(gap));
    }

    private void addLabeledField(
            JPanel panel,
            String labelText,
            JTextField field,
            int gap) {

        JPanel wrapper = new JPanel(new BorderLayout(0, 3));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(75, 80, 90));

        RoundedPanel inputBorder = new RoundedPanel(
                Color.WHITE, outline, 13);
        inputBorder.setLayout(new BorderLayout());
        inputBorder.setBorder(new EmptyBorder(1, 1, 1, 1));

        field.setBorder(new EmptyBorder(7, 12, 7, 12));
        inputBorder.add(field, BorderLayout.CENTER);
        inputBorder.setPreferredSize(new Dimension(480, 43));
        inputBorder.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 43));

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(inputBorder, BorderLayout.CENTER);

        panel.add(wrapper);
        panel.add(Box.createVerticalStrut(gap));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(new Color(50, 55, 65));
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(outline),
                new EmptyBorder(4, 9, 4, 9)));
        combo.setFocusable(false);
        combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private String inputValue(JTextField field, String placeholder) {
        String value = field.getText().trim();
        return value.equals(placeholder) ? "" : value;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(cyan);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(
                BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = c.getWidth();
                int h = c.getHeight();

                g2.setColor(new Color(0, 0, 0, 25));
                g2.fillRoundRect(1, 3, w - 2, h - 4, 20, 20);

                Color fill = b.getModel().isRollover()
                        ? new Color(0, 155, 195)
                        : cyan;

                g2.setColor(fill);
                g2.fillRoundRect(0, 0, w - 2, h - 4, 20, 20);
                g2.dispose();

                super.paint(g, c);
            }
        });
    }

    // ================= LOST & FOUND LIST =================
    private boolean saveReportToDatabase(
            String type,
            String item,
            String description,
            int quantity,
            String station,
            String name,
            String phone,
            String date,
            String time) {

        String sql = "INSERT INTO lost_found_reports "
                + "(report_type, item_name, item_description, quantity, "
                + "station_name, contact_name, mobile, report_date, report_time) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            java.sql.Connection con =
                    com.metromanagement.database.DBConnection.getConnection();

            java.sql.PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, type);
            ps.setString(2, item);
            ps.setString(3, description);
            ps.setInt(4, quantity);
            ps.setString(5, station);
            ps.setString(6, name);
            ps.setString(7, phone);
            ps.setString(8, date);
            ps.setString(9, time);

            return ps.executeUpdate() > 0;

        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Report save nahi hui.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }

    private void showReports() {

        boolean showLost = lostRadio.isSelected();

        String type = showLost ? "Lost" : "Found";

        String sql =
                "SELECT report_id, item_name, item_description, quantity, "
                + "station_name, contact_name, mobile, report_date, report_time "
                + "FROM lost_found_reports "
                + "WHERE report_type = ? "
                + "ORDER BY created_at DESC, report_id DESC";

        DefaultListModel<String> model = new DefaultListModel<>();

        try (
            java.sql.Connection con =
                    com.metromanagement.database.DBConnection.getConnection();

            java.sql.PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setString(1, type);

            try (java.sql.ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String report =
                            "Report ID: " + rs.getInt("report_id")
                            + "\nItem: " + rs.getString("item_name")
                            + "\nDescription: "
                            + rs.getString("item_description")
                            + "\nQuantity: " + rs.getInt("quantity")
                            + "\nStation: " + rs.getString("station_name")
                            + "\nReported By: " + rs.getString("contact_name")
                            + "\nMobile: " + rs.getString("mobile")
                            + "\nDate: " + rs.getString("report_date")
                            + "\nTime: " + rs.getString("report_time");

                    model.addElement(report);
                }
            }

        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Reports load nahi ho paayi.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (model.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No " + type.toLowerCase()
                            + " item reports are available yet.",
                    "Lost & Found Items",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        JList<String> list = new JList<>(model);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(-1);
        list.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Display each report in a readable multi-line format.
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean selected,
                    boolean focused) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, focused);

                label.setText("<html><div style='padding:8px;'>"
                        + escapeHtml(String.valueOf(value))
                                .replace("\n", "<br>")
                        + "</div></html>");

                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(4, 4, 4, 4),
                        BorderFactory.createLineBorder(outline)
                ));

                label.setPreferredSize(new Dimension(550, 145));

                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(620, 420));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                showLost
                        ? "List of Lost Items"
                        : "List of Found Items",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    // ================= INSTRUCTIONS =================

    private JPanel createInstructionHeading() {
        JPanel panel = new JPanel(new BorderLayout(13, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(2, 5, 2, 5));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel question = new JLabel("?");
        question.setFont(new Font("Segoe UI", Font.BOLD, 37));
        question.setForeground(cyan);
        question.setPreferredSize(new Dimension(45, 45));
        question.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel(
                "Instructions to Collect Lost Articles");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 19));
        heading.setForeground(navy);

        JLabel subtitle = new JLabel(
                "Important guidelines for collecting belongings");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(105, 115, 130));

        textPanel.add(heading);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(subtitle);

        panel.add(question, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInstructionCard() {
        RoundedPanel card = new RoundedPanel(Color.WHITE, outline, 18);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(17, 22, 5, 22));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        addInstruction(card,
                "Visit the customer care centre of a nearby station within "
                        + "7 days. After 7 days, contact the Lost & Found office "
                        + "mentioned below.");

        addInstruction(card,
                "A photocopy of valid ID proof showing your address, "
                        + "along with the original ID, is mandatory to collect articles.");

        addInstruction(card,
                "If lost articles are not claimed within one month "
                        + "(for DMRC) and one year (for Rapid Metro), the deposited "
                        + "articles may be disposed of.");

        return card;
    }

    private void addInstruction(JPanel parent, String message) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(0, 0, 13, 0));

        JLabel bullet = new JLabel("\u2022");
        bullet.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel text = new JLabel(
                "<html><div style='width:650px;'>"
                        + escapeHtml(message) + "</div></html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        text.setForeground(new Color(55, 65, 81));

        row.add(bullet, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);

        parent.add(row);
    }

    // ================= DMRC =================

    private JPanel createDMRCHeading() {
        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel symbol = new JLabel("\u2296");
        symbol.setFont(new Font("Segoe UI Symbol", Font.BOLD, 44));
        symbol.setForeground(new Color(205, 0, 45));

        JLabel title = new JLabel("DMRC");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        panel.add(symbol);
        panel.add(title);

        return panel;
    }

    // ================= OFFICE DETAILS =================

    private JPanel createOfficeCard() {
        RoundedPanel card = new RoundedPanel(Color.WHITE, outline, 18);
        card.setLayout(new BorderLayout(0, 15));
        card.setBorder(new EmptyBorder(20, 22, 15, 22));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel("Lost & Found Office");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(navy);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        addOfficeRow(details, "\u25CF",
                "Concourse level, below platform no. 03, "
                        + "Kashmere Gate Metro Station",
                new Color(225, 0, 60));

        addOfficeRow(details, "\u260E", "8527405555",
                new Color(0, 165, 220));

        addOfficeRow(details, "\u260E",
                "011-23417910, Extn: 113701",
                new Color(220, 40, 65));

        addOfficeRow(details, "\u25F7",
                "08:00 a.m. to 08:00 p.m. "
                        + "(Except on Sunday, all National and Gazetted Holidays)",
                new Color(0, 165, 190));

        card.add(heading, BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);

        return card;
    }

    private void addOfficeRow(
            JPanel parent,
            String symbol,
            String detail,
            Color iconColor) {

        JPanel row = new JPanel(new BorderLayout(13, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(4, 0, 10, 0));

        JLabel icon = new JLabel(symbol, SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20));
        icon.setForeground(iconColor);
        icon.setPreferredSize(new Dimension(38, 32));

        JLabel text = new JLabel(
                "<html><div style='width:650px;'>"
                        + escapeHtml(detail) + "</div></html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        text.setForeground(new Color(55, 65, 81));

        row.add(icon, BorderLayout.WEST);
        row.add(text, BorderLayout.CENTER);

        parent.add(row);
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public static void showManagement() {
        SwingUtilities.invokeLater(LostFoundFrame::new);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LostFoundFrame::new);
    }

    // ================= ROUNDED PANEL =================

    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color outline;
        private final int arc;

        RoundedPanel(Color fill, Color outline, int arc) {
            this.fill = fill;
            this.outline = outline;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth() - 2;
            int height = getHeight() - 2;

            g.setColor(new Color(0, 0, 0, 10));
            g.fill(new RoundRectangle2D.Float(
                    2, 3, width - 1, height - 1, arc, arc));

            g.setColor(fill);
            g.fill(new RoundRectangle2D.Float(
                    1, 1, width - 2, height - 2, arc, arc));

            g.setColor(outline);
            g.draw(new RoundRectangle2D.Float(
                    1, 1, width - 2, height - 2, arc, arc));

            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
