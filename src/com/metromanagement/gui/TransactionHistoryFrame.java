
package com.metromanagement.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import com.metromanagement.database.DBConnection;

public class TransactionHistoryFrame {

    private static JFrame frame;
    private static JTable table;
    private static DefaultTableModel model;
    private static TableRowSorter<DefaultTableModel> sorter;

    private static JComboBox<String> typeFilter;
    private static JTextField cardFilter;
    private static JTextField tableSearch;
    private static JTextField fromDateField;
    private static JTextField toDateField;

    private static JLabel recordCount;
    private static JLabel totalTransactionsValue;
    private static JLabel totalRechargedValue;
    private static JLabel latestTransactionValue;

    private static final Color NAVY = new Color(15, 43, 79);
    private static final Color BLUE = new Color(20, 105, 235);
    private static final Color BACKGROUND = new Color(246, 249, 254);
    private static final Color BORDER = new Color(219, 229, 242);
    private static final Color TEXT = new Color(37, 55, 80);
    private static final Color MUTED = new Color(105, 123, 148);
    private static final Color LIGHT_BLUE = new Color(233, 242, 255);

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static void showManagement() {

        if (frame != null && frame.isDisplayable()) {
            frame.setVisible(true);
            frame.toFront();
            loadSummary();
            loadTransactions();
            return;
        }

        frame = new JFrame("Metro & Smart Card Management System");
        frame.setSize(1450, 850);
        frame.setMinimumSize(new Dimension(1050, 700));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BACKGROUND);

        // ================= HEADER =================

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        4, 0, 0, 0, LIGHT_BLUE),
                new EmptyBorder(18, 30, 18, 30)));

        JPanel headingPanel = new JPanel();
        headingPanel.setOpaque(false);
        headingPanel.setLayout(
                new BoxLayout(headingPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Transaction History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(NAVY);

        JLabel subtitle = new JLabel(
                "View and manage your smart card transactions");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        headingPanel.add(title);
        headingPanel.add(Box.createVerticalStrut(4));
        headingPanel.add(subtitle);

        JLabel portal = new JLabel("PASSENGER PORTAL");
        portal.setIcon(drawIcon("user", BLUE, 17));
        portal.setIconTextGap(8);
        portal.setFont(new Font("Segoe UI", Font.BOLD, 11));
        portal.setForeground(BLUE);
        portal.setBorder(new EmptyBorder(10, 12, 10, 12));
        portal.setOpaque(true);
        portal.setBackground(LIGHT_BLUE);

        header.add(headingPanel, BorderLayout.WEST);
        header.add(portal, BorderLayout.EAST);

        // ================= SUMMARY CARDS =================

        JPanel summaryPanel = new JPanel(
                new GridLayout(1, 3, 16, 0));
        summaryPanel.setBackground(BACKGROUND);

        JPanel totalPanel = createSummary(
                "receipt",
                "Total Transactions",
                "0",
                "All card transactions",
                new Color(231, 241, 255),
                BLUE);

        JPanel rechargePanel = createSummary(
                "wallet",
                "Total Recharged",
                "₹0.00",
                "Total amount added to cards",
                new Color(225, 246, 239),
                new Color(12, 125, 75));

        JPanel latestPanel = createSummary(
                "clock",
                "Latest Transaction",
                "No records",
                "Most recent transaction date",
                new Color(255, 243, 222),
                new Color(216, 133, 0));

        totalTransactionsValue = (JLabel)
                totalPanel.getClientProperty("valueLabel");

        totalRechargedValue = (JLabel)
                rechargePanel.getClientProperty("valueLabel");

        latestTransactionValue = (JLabel)
                latestPanel.getClientProperty("valueLabel");

        summaryPanel.add(totalPanel);
        summaryPanel.add(rechargePanel);
        summaryPanel.add(latestPanel);

        // ================= FILTER PANEL =================

        JPanel filters = new JPanel(new BorderLayout(0, 14));
        filters.setBackground(Color.WHITE);
        filters.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 16, 16, 16)));

        JPanel filterHeading = new JPanel();
        filterHeading.setOpaque(false);
        filterHeading.setLayout(
                new BoxLayout(filterHeading, BoxLayout.Y_AXIS));

        JLabel filterTitle = new JLabel("Find Transactions");
        filterTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        filterTitle.setForeground(NAVY);

        JLabel filterSubtitle = new JLabel(
                "Search and filter your smart card transaction records.");
        filterSubtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 12));
        filterSubtitle.setForeground(MUTED);

        filterHeading.add(filterTitle);
        filterHeading.add(Box.createVerticalStrut(3));
        filterHeading.add(filterSubtitle);

        cardFilter = new JTextField();
        styleField(cardFilter);
        cardFilter.setToolTipText("Enter complete card number");

        typeFilter = new JComboBox<>(new String[]{
            "ALL", "ISSUE", "RECHARGE", "FARE", "REFUND"
        });
        typeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        typeFilter.setBackground(Color.WHITE);
        typeFilter.setPreferredSize(new Dimension(150, 40));

        fromDateField = new JTextField();
        toDateField = new JTextField();

        styleField(fromDateField);
        styleField(toDateField);

        fromDateField.setToolTipText("Format: dd-MM-yyyy");
        toDateField.setToolTipText("Format: dd-MM-yyyy");

        JPanel filterFields = new JPanel(new GridBagLayout());
        filterFields.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weighty = 0;

        addFilterField(filterFields, gbc, 0,
                "Card Number", cardFilter, 1.2);

        addFilterField(filterFields, gbc, 1,
                "Transaction Type", typeFilter, 0.8);

        addFilterField(filterFields, gbc, 2,
                "From Date", createDateInput(fromDateField), 0.8);

        addFilterField(filterFields, gbc, 3,
                "To Date", createDateInput(toDateField), 0.8);

        JButton searchButton = makeIconButton(
                "Search", "search", BLUE);

        JButton refreshButton = makeIconButton(
                "Refresh", "refresh", NAVY);

        JButton clearButton = makeIconButton(
                "Clear Filters", "clear",
                new Color(101, 115, 135));

        searchButton.setPreferredSize(new Dimension(115, 42));
        refreshButton.setPreferredSize(new Dimension(115, 42));
        clearButton.setPreferredSize(new Dimension(130, 42));

        gbc.weightx = 0;
        gbc.insets = new Insets(22, 0, 0, 10);
        gbc.gridx = 4;
        filterFields.add(searchButton, gbc);

        gbc.gridx = 5;
        filterFields.add(refreshButton, gbc);

        gbc.gridx = 6;
        gbc.insets = new Insets(22, 0, 0, 0);
        filterFields.add(clearButton, gbc);

        filters.add(filterHeading, BorderLayout.NORTH);
        filters.add(filterFields, BorderLayout.CENTER);

        // ================= TRANSACTION TABLE =================

        JPanel tablePanel = new JPanel(new BorderLayout(0, 12));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 12, 12, 12)));

        JPanel tableHeading = new JPanel(new BorderLayout(12, 0));
        tableHeading.setOpaque(false);

        JPanel tableTitles = new JPanel();
        tableTitles.setOpaque(false);
        tableTitles.setLayout(
                new BoxLayout(tableTitles, BoxLayout.Y_AXIS));

        JLabel tableTitle = new JLabel("All Transactions");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 21));
        tableTitle.setForeground(NAVY);

        JLabel tableSubtitle = new JLabel(
                "View your complete transaction history with details.");
        tableSubtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 12));
        tableSubtitle.setForeground(MUTED);

        tableTitles.add(tableTitle);
        tableTitles.add(Box.createVerticalStrut(3));
        tableTitles.add(tableSubtitle);

        tableSearch = new JTextField();
        styleField(tableSearch);
        tableSearch.setPreferredSize(new Dimension(300, 40));
        tableSearch.setToolTipText(
                "Search by transaction ID or card number");

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(
                new JLabel(drawIcon("search", BLUE, 18)),
                BorderLayout.WEST);
        searchPanel.add(tableSearch, BorderLayout.CENTER);

        tableHeading.add(tableTitles, BorderLayout.CENTER);
        tableHeading.add(searchPanel, BorderLayout.EAST);

        String[] columns = {
            "Transaction ID",
            "Card Number",
            "Type",
            "Amount (₹)",
            "Balance After (₹)",
            "Description",
            "Date & Time"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setBackground(Color.WHITE);
        table.setRowHeight(40);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(215, 232, 255));
        table.setSelectionForeground(NAVY);
        table.setGridColor(new Color(230, 237, 247));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setForeground(NAVY);
        tableHeader.setBackground(LIGHT_BLUE);
        tableHeader.setPreferredSize(new Dimension(0, 42));
        tableHeader.setReorderingAllowed(false);

        table.getColumnModel().getColumn(2)
                .setCellRenderer(new TransactionTypeRenderer());

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        recordCount = new JLabel("Showing 0 transactions");
        recordCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        recordCount.setForeground(MUTED);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 2, 0, 2));
        footer.add(recordCount, BorderLayout.WEST);

        tablePanel.add(tableHeading, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(footer, BorderLayout.SOUTH);

        // ================= MAIN LAYOUT =================

        JPanel topContent = new JPanel(new BorderLayout(0, 16));
        topContent.setBackground(BACKGROUND);
        topContent.add(summaryPanel, BorderLayout.NORTH);
        topContent.add(filters, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(BACKGROUND);
        content.setBorder(new EmptyBorder(18, 30, 16, 30));
        content.add(topContent, BorderLayout.NORTH);
        content.add(tablePanel, BorderLayout.CENTER);

        frame.add(header, BorderLayout.NORTH);
        frame.add(content, BorderLayout.CENTER);

        // ================= LISTENERS =================

        searchButton.addActionListener(e -> {
            if (validateDates()) {
                loadTransactions();
            }
        });

        refreshButton.addActionListener(e -> {
            if (validateDates()) {
                loadSummary();
                loadTransactions();
            }
        });

        clearButton.addActionListener(e -> {
            cardFilter.setText("");
            typeFilter.setSelectedItem("ALL");
            fromDateField.setText("");
            toDateField.setText("");
            tableSearch.setText("");
            loadTransactions();
        });

        cardFilter.addActionListener(e -> {
            if (validateDates()) {
                loadTransactions();
            }
        });

        typeFilter.addActionListener(e -> {
            if (frame != null && frame.isVisible()
                    && validateDates()) {
                loadTransactions();
            }
        });

        // FIXED: DocumentListener imports and search filtering.
        tableSearch.getDocument().addDocumentListener(
                new DocumentListener() {

            private void filterTable() {
                applyTableSearch();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

        frame.setVisible(true);
        loadSummary();
        loadTransactions();
    }

    // ================= SUMMARY CARD =================

    private static JPanel createSummary(
            String iconType,
            String heading,
            String value,
            String description,
            Color background,
            Color accent) {

        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(background);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel icon = new JLabel(
                drawIcon(iconType, accent, 34),
                SwingConstants.CENTER);
        icon.setOpaque(true);
        icon.setBackground(new Color(
                Math.min(255, background.getRed() + 10),
                Math.min(255, background.getGreen() + 10),
                Math.min(255, background.getBlue() + 10)));
        icon.setPreferredSize(new Dimension(66, 66));

        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(
                new BoxLayout(labels, BoxLayout.Y_AXIS));

        JLabel headingLabel = new JLabel(heading);
        headingLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13));
        headingLabel.setForeground(NAVY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 27));
        valueLabel.setForeground(NAVY);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 12));
        descriptionLabel.setForeground(MUTED);

        labels.add(headingLabel);
        labels.add(Box.createVerticalStrut(4));
        labels.add(valueLabel);
        labels.add(Box.createVerticalStrut(3));
        labels.add(descriptionLabel);

        panel.add(icon, BorderLayout.WEST);
        panel.add(labels, BorderLayout.CENTER);
        panel.putClientProperty("valueLabel", valueLabel);

        return panel;
    }

    // ================= FILTER HELPERS =================

    private static void addFilterField(
            JPanel panel,
            GridBagConstraints gbc,
            int x,
            String labelText,
            JComponent input,
            double weight) {

        JPanel fieldPanel = new JPanel(new BorderLayout(0, 7));
        fieldPanel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(NAVY);

        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(input, BorderLayout.CENTER);

        gbc.gridx = x;
        gbc.gridwidth = 1;
        gbc.weightx = weight;
        gbc.insets = new Insets(0, 0, 0, 12);

        panel.add(fieldPanel, gbc);
    }

    private static JPanel createDateInput(JTextField field) {

        JPanel container = new JPanel(new BorderLayout(7, 0));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 8, 0, 8)));

        JLabel icon = new JLabel(drawIcon("calendar", BLUE, 18));

        field.setBorder(new EmptyBorder(7, 3, 7, 3));
        field.setPreferredSize(new Dimension(110, 38));

        container.add(icon, BorderLayout.WEST);
        container.add(field, BorderLayout.CENTER);

        return container;
    }

    private static void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)));
        field.setPreferredSize(new Dimension(140, 40));
    }

    // ================= ICONS =================

    private static Icon drawIcon(
            String type, Color color, int size) {

        return new Icon() {
            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }

            @Override
            public void paintIcon(
                    Component component, Graphics g, int x, int y) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.translate(x, y);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(
                        2.2f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = size;
                int h = size;

                switch (type) {
                    case "receipt":
                        g2.drawRoundRect(
                                5, 2, w - 10, h - 4, 4, 4);
                        g2.drawLine(8, 9, w - 8, 9);
                        g2.drawLine(8, 14, w - 8, 14);
                        g2.drawLine(8, 19, w - 11, 19);
                        break;

                    case "wallet":
                        g2.drawRoundRect(
                                2, h / 4, w - 4, h * 3 / 5, 5, 5);
                        g2.drawRoundRect(
                                w / 2, h / 2, w / 2 - 2,
                                h / 4, 4, 4);
                        g2.fillOval(
                                w * 2 / 3, h * 9 / 16, 3, 3);
                        break;

                    case "clock":
                        g2.drawOval(2, 2, w - 4, h - 4);
                        g2.drawLine(
                                w / 2, 5, w / 2, h / 2);
                        g2.drawLine(
                                w / 2, h / 2, w * 3 / 4, h * 2 / 3);
                        break;

                    case "search":
                        g2.drawOval(2, 2, w * 2 / 3, h * 2 / 3);
                        g2.drawLine(
                                w * 2 / 3, h * 2 / 3,
                                w - 2, h - 2);
                        break;

                    case "refresh":
                        g2.drawArc(
                                3, 3, w - 6, h - 6, 35, 285);
                        g2.drawLine(
                                w - 5, 3, w - 5, h / 3);
                        g2.drawLine(
                                w - 5, 3, w * 2 / 3, 4);
                        break;

                    case "clear":
                        g2.drawLine(4, h - 4, w - 4, 4);
                        g2.drawLine(4, 4, w - 4, h - 4);
                        break;

                    case "calendar":
                        g2.drawRoundRect(
                                2, 4, w - 4, h - 6, 3, 3);
                        g2.drawLine(2, 9, w - 2, 9);
                        g2.drawLine(6, 1, 6, 6);
                        g2.drawLine(w - 6, 1, w - 6, 6);
                        break;

                    case "user":
                        g2.drawOval(w / 3, 1, w / 3, h / 3);
                        g2.drawArc(
                                2, h / 3, w - 4, h * 2 / 3,
                                0, 180);
                        break;

                    case "card":
                        g2.drawRoundRect(
                                2, h / 4, w - 4, h / 2, 4, 4);
                        g2.drawLine(3, h / 2, w - 3, h / 2);
                        break;

                    case "rupee":
                        g2.setFont(new Font(
                                "Segoe UI", Font.BOLD, size - 2));
                        g2.drawString("₹", 1, h - 2);
                        break;
                }

                g2.dispose();
            }
        };
    }

    private static JButton makeButton(
            String text, Color color) {

        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 42));
        button.setBorder(new EmptyBorder(8, 10, 8, 10));

        return button;
    }

    private static JButton makeIconButton(
            String text, String iconType, Color color) {

        JButton button = makeButton(text, color);
        button.setIcon(drawIcon(iconType, Color.WHITE, 18));
        button.setIconTextGap(8);
        return button;
    }

    // ================= DATE VALIDATION =================

    private static boolean validateDates() {

        LocalDate from = parseDate(fromDateField.getText());
        LocalDate to = parseDate(toDateField.getText());

        if (!fromDateField.getText().trim().isEmpty()
                && from == null) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Enter From Date in dd-MM-yyyy format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            fromDateField.requestFocus();
            return false;
        }

        if (!toDateField.getText().trim().isEmpty()
                && to == null) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Enter To Date in dd-MM-yyyy format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            toDateField.requestFocus();
            return false;
        }

        if (from != null && to != null && from.isAfter(to)) {
            JOptionPane.showMessageDialog(
                    frame,
                    "From Date cannot be after To Date.",
                    "Invalid Date Range",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(value.trim(), DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    // ================= LOAD SUMMARY =================

    private static void loadSummary() {

        String sql =
                "SELECT COUNT(*) AS total_transactions, " +
                "COALESCE(SUM(CASE WHEN transaction_type = 'RECHARGE' " +
                "THEN amount ELSE 0 END), 0) AS total_recharged, " +
                "MAX(transaction_time) AS latest_transaction " +
                "FROM card_transactions";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                totalTransactionsValue.setText(
                        String.valueOf(
                                rs.getInt("total_transactions")));

                BigDecimal recharged =
                        rs.getBigDecimal("total_recharged");

                totalRechargedValue.setText(
                        "₹" + (recharged == null
                                ? "0.00"
                                : recharged.toPlainString()));

                Timestamp latest =
                        rs.getTimestamp("latest_transaction");

                if (latest == null) {
                    latestTransactionValue.setText("No records");
                } else {
                    latestTransactionValue.setText(
                            latest.toLocalDateTime()
                                    .toLocalDate()
                                    .format(
                                            DateTimeFormatter.ofPattern(
                                                    "dd MMM yyyy")));
                }
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // ================= LOAD TRANSACTIONS =================

    private static void loadTransactions() {

        if (model == null) {
            return;
        }

        model.setRowCount(0);

        String cardNumber = cardFilter.getText().trim();
        String transactionType =
                (String) typeFilter.getSelectedItem();

        LocalDate fromDate = parseDate(fromDateField.getText());
        LocalDate toDate = parseDate(toDateField.getText());

        StringBuilder sql = new StringBuilder(
                "SELECT t.transaction_id, s.card_number, " +
                "t.transaction_type, t.amount, t.balance_after, " +
                "t.description, t.transaction_time " +
                "FROM card_transactions t " +
                "INNER JOIN smart_cards s ON t.card_id = s.card_id " +
                "WHERE 1=1 ");

        if (!cardNumber.isEmpty()) {
            sql.append("AND s.card_number = ? ");
        }

        if (transactionType != null
                && !"ALL".equals(transactionType)) {
            sql.append("AND t.transaction_type = ? ");
        }

        if (fromDate != null) {
            sql.append("AND t.transaction_time >= ? ");
        }

        if (toDate != null) {
            sql.append("AND t.transaction_time < ? ");
        }

        sql.append(
                "ORDER BY t.transaction_time DESC, " +
                "t.transaction_id DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql.toString())) {

            int parameter = 1;

            if (!cardNumber.isEmpty()) {
                ps.setString(parameter++, cardNumber);
            }

            if (transactionType != null
                    && !"ALL".equals(transactionType)) {
                ps.setString(parameter++, transactionType);
            }

            if (fromDate != null) {
                ps.setTimestamp(
                        parameter++,
                        Timestamp.valueOf(fromDate.atStartOfDay()));
            }

            if (toDate != null) {
                ps.setTimestamp(
                        parameter++,
                        Timestamp.valueOf(
                                toDate.plusDays(1).atStartOfDay()));
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Timestamp timestamp =
                            rs.getTimestamp("transaction_time");

                    String dateTime = timestamp == null
                            ? ""
                            : timestamp.toLocalDateTime().format(
                                    DateTimeFormatter.ofPattern(
                                            "dd-MM-yyyy HH:mm"));

                    BigDecimal amount =
                            rs.getBigDecimal("amount");

                    BigDecimal balance =
                            rs.getBigDecimal("balance_after");

                    model.addRow(new Object[]{
                        rs.getInt("transaction_id"),
                        rs.getString("card_number"),
                        rs.getString("transaction_type"),
                        amount == null
                                ? "₹0.00"
                                : "₹" + amount.toPlainString(),
                        balance == null
                                ? "₹0.00"
                                : "₹" + balance.toPlainString(),
                        rs.getString("description") == null
                                ? ""
                                : rs.getString("description"),
                        dateTime
                    });
                }
            }

            applyTableSearch();
            updateRecordCount();

            frame.setTitle(
                    "Transaction History - "
                            + table.getRowCount() + " records");

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    // ================= TABLE SEARCH =================

    private static void applyTableSearch() {

        if (model == null || tableSearch == null || table == null) {
            return;
        }

        String search = tableSearch.getText().trim();

        if (search.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + Pattern.quote(search),
                            0, 1));
        }

        updateRecordCount();
    }

    private static void updateRecordCount() {

        if (recordCount == null || table == null) {
            return;
        }

        int visible = table.getRowCount();
        int total = model.getRowCount();

        if (visible == total) {
            recordCount.setText(
                    "Showing " + total + " transactions");
        } else {
            recordCount.setText(
                    "Showing " + visible + " of "
                            + total + " transactions");
        }
    }

    // ================= TRANSACTION TYPE RENDERER =================

    private static class TransactionTypeRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JLabel label = (JLabel)
                    super.getTableCellRendererComponent(
                            table, value, isSelected,
                            hasFocus, row, column);

            String type = value == null
                    ? ""
                    : value.toString().toUpperCase();

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setOpaque(true);

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(NAVY);
                label.setIcon(null);
                return label;
            }

            switch (type) {
                case "RECHARGE":
                    label.setForeground(new Color(12, 125, 75));
                    label.setBackground(new Color(225, 246, 239));
                    label.setIcon(drawIcon(
                            "wallet", new Color(12, 125, 75), 16));
                    break;

                case "ISSUE":
                    label.setForeground(BLUE);
                    label.setBackground(new Color(231, 241, 255));
                    label.setIcon(drawIcon("card", BLUE, 16));
                    break;

                case "REFUND":
                    label.setForeground(new Color(12, 125, 75));
                    label.setBackground(new Color(225, 246, 239));
                    label.setIcon(drawIcon(
                            "refresh", new Color(12, 125, 75), 16));
                    break;

                case "FARE":
                    label.setForeground(new Color(180, 105, 0));
                    label.setBackground(new Color(255, 243, 222));
                    label.setIcon(drawIcon(
                            "rupee", new Color(180, 105, 0), 16));
                    break;

                default:
                    label.setForeground(TEXT);
                    label.setBackground(Color.WHITE);
                    label.setIcon(null);
            }

            label.setIconTextGap(7);
            return label;
        }
    }

    private static void showDatabaseError(SQLException ex) {
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                frame,
                "Database operation failed:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
