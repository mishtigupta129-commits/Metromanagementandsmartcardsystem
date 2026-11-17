
package com.metromanagement.gui;

import com.metromanagement.database.DBConnection;
import com.metromanagement.service.SmartCardService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class SmartCardFrame {

    private static JFrame frame;

    private static DefaultTableModel cardModel, transactionModel;
    private static JTable cardTable, transactionTable;
    private static JTextField searchField;

    private static JLabel selectedCardLabel, balanceLabel;
    private static JLabel statusLabel, qrLabel, entryLabel, exitLabel;
    private static JButton activateButton, deactivateButton;

    private static String selectedCard = "";
    private static BigDecimal selectedBalance = BigDecimal.ZERO;

    private static final Color NAVY = new Color(8, 43, 100);
    private static final Color BLUE = new Color(0, 100, 245);
    private static final Color BG = new Color(246, 249, 254);
    private static final Color BORDER = new Color(222, 232, 247);
    private static final Color TEXT = new Color(25, 43, 79);
    private static final Color MUTED = new Color(100, 120, 151);
    private static final Color PALE_BLUE = new Color(235, 243, 255);
    private static final Color GREEN = new Color(0, 145, 83);
    private static final Color RED = new Color(220, 49, 55);

    private static final Font REG =
            new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font BOLD =
            new Font("Segoe UI", Font.BOLD, 13);

    public static void showManagement() {
        if (frame != null && frame.isDisplayable()) {
            frame.setVisible(true);
            frame.toFront();
            loadCards();
            return;
        }

        buildUI();
        loadCards();
        frame.setVisible(true);
    }

    private static void buildUI() {
        frame = new JFrame("Metro Management System - Virtual Smart Card");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1500, 900);
        frame.setMinimumSize(new Dimension(1120, 720));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG);

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(NAVY);
        topBar.setBorder(new EmptyBorder(13, 22, 13, 22));

        JLabel brand = new JLabel("\u25A3   Metro Management System");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 17));

        JLabel topRight = new JLabel("VIRTUAL SMART CARD");
        topRight.setForeground(new Color(220, 233, 255));
        topRight.setFont(new Font("Segoe UI", Font.BOLD, 11));

        topBar.add(brand, BorderLayout.WEST);
        topBar.add(topRight, BorderLayout.EAST);
        frame.add(topBar, BorderLayout.NORTH);

        // MAIN CONTENT
        JPanel main = new JPanel(new BorderLayout(17, 0));
        main.setBackground(BG);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        frame.add(main, BorderLayout.CENTER);

        // LEFT CARD LIST
        JPanel left = new JPanel(new BorderLayout(0, 13));
        left.setBackground(BG);
        left.setPreferredSize(new Dimension(330, 0));

        JPanel leftHeading = new JPanel(new BorderLayout(0, 12));
        leftHeading.setOpaque(false);
        leftHeading.add(heading("My Smart Cards", 21), BorderLayout.NORTH);

        JButton issueButton = button("+  Issue New Card", BLUE);
        issueButton.addActionListener(e -> issueCard());
        leftHeading.add(issueButton, BorderLayout.SOUTH);

        searchField = new JTextField();
        searchField.setFont(REG);
        searchField.setToolTipText("Search card number");
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(11, 12, 11, 12)));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createLineBorder(BORDER));
        searchPanel.add(searchField);

        String[] cardColumns = {
                "Card Number", "Balance", "Status", "Card Type"
        };

        cardModel = new DefaultTableModel(cardColumns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        cardTable = new JTable(cardModel);
        styleTable(cardTable);
        cardTable.setRowHeight(57);
        cardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cardTable.getColumnModel().getColumn(0).setPreferredWidth(175);
        cardTable.getColumnModel().getColumn(1).setPreferredWidth(85);
        cardTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        // Keep card type in model but hide its column
        cardTable.getColumnModel().getColumn(3).setMinWidth(0);
        cardTable.getColumnModel().getColumn(3).setMaxWidth(0);
        cardTable.getColumnModel().getColumn(3).setPreferredWidth(0);

        JScrollPane cardScroll = new JScrollPane(cardTable);
        cardScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        cardScroll.getViewport().setBackground(Color.WHITE);

        JPanel leftContent = new JPanel(new BorderLayout(0, 12));
        leftContent.setOpaque(false);
        leftContent.add(searchPanel, BorderLayout.NORTH);
        leftContent.add(cardScroll, BorderLayout.CENTER);

        left.add(leftHeading, BorderLayout.NORTH);
        left.add(leftContent, BorderLayout.CENTER);
        main.add(left, BorderLayout.WEST);

        // RIGHT WORKSPACE
        JPanel right = new JPanel(new BorderLayout(0, 15));
        right.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);

        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.add(heading("Virtual Smart Card", 27));
        titleStack.add(Box.createVerticalStrut(5));
        titleStack.add(label(
                "Manage your card, view its QR code and manage your balance.",
                MUTED, REG));

        titleRow.add(titleStack, BorderLayout.WEST);
        titleRow.add(label(
                "Home   >   Smart Card   >   Virtual Smart Card",
                MUTED, REG), BorderLayout.EAST);

        right.add(titleRow, BorderLayout.NORTH);

        JPanel workspace = new JPanel(new BorderLayout(0, 15));
        workspace.setOpaque(false);

        // CARD + QR + ACTIONS
        JPanel topSection = new JPanel(new GridBagLayout());
        topSection.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.fill = GridBagConstraints.BOTH;
        g.weighty = 1;
        g.insets = new Insets(0, 0, 0, 12);

        g.gridx = 0;
        g.weightx = 0.46;
        topSection.add(buildCardPanel(), g);

        g.gridx = 1;
        g.weightx = 0.30;
        topSection.add(buildQRPanel(), g);

        g.gridx = 2;
        g.weightx = 0.24;
        g.insets = new Insets(0, 0, 0, 0);
        topSection.add(buildActionsPanel(), g);

        workspace.add(topSection, BorderLayout.NORTH);

        // JOURNEY + TRANSACTIONS
        JPanel bottomSection = new JPanel(new BorderLayout(0, 14));
        bottomSection.setOpaque(false);
        bottomSection.add(buildJourneyPanel(), BorderLayout.NORTH);
        bottomSection.add(buildTransactionsPanel(), BorderLayout.CENTER);

        workspace.add(bottomSection, BorderLayout.CENTER);
        right.add(workspace, BorderLayout.CENTER);
        main.add(right, BorderLayout.CENTER);

        // CARD SELECTION
        cardTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()
                    || cardTable.getSelectedRow() < 0) {
                return;
            }

            int row = cardTable.convertRowIndexToModel(
                    cardTable.getSelectedRow());

            selectedCard = String.valueOf(cardModel.getValueAt(row, 0));
            selectedBalance = parseMoney(
                    String.valueOf(cardModel.getValueAt(row, 1)));

            String status = String.valueOf(cardModel.getValueAt(row, 2));
            updateSelectedCard(status);
            loadTransactions();
        });

        // SEARCH
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String query = searchField.getText().trim();

                javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                        new javax.swing.table.TableRowSorter<>(cardModel);

                cardTable.setRowSorter(sorter);

                if (query.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(
                            javax.swing.RowFilter.regexFilter(
                                    "(?i)" + Pattern.quote(query), 0));
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }
        });
    }

    // =========================================================
    // PREMIUM METRO SMART CARD PANEL
    // =========================================================

    private static JPanel buildCardPanel() {
        JPanel panel = new MetroCardBackgroundPanel();
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(new EmptyBorder(23, 24, 23, 24));

        // HEADER
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JLabel cardIcon = new JLabel(new MetroCardIcon(Color.WHITE, 36));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel cardTitle = new JLabel("Metro Smart Card");
        cardTitle.setForeground(Color.WHITE);
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitle = new JLabel(
                "Your card for a seamless metro journey");
        subtitle.setForeground(new Color(224, 237, 255));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        titleBox.add(cardTitle);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);

        JLabel metroLogo = new JLabel(new MetroLogoIcon(58));

        header.add(cardIcon, BorderLayout.WEST);
        header.add(titleBox, BorderLayout.CENTER);
        header.add(metroLogo, BorderLayout.EAST);

        // CARD NUMBER + COPY BUTTON
        JPanel numberPanel = new JPanel(new BorderLayout(8, 0));
        numberPanel.setOpaque(false);

        JPanel numberText = new JPanel();
        numberText.setOpaque(false);
        numberText.setLayout(new BoxLayout(numberText, BoxLayout.Y_AXIS));

        JLabel numberTitle = label(
                "Card Number", new Color(221, 235, 255), REG);

        selectedCardLabel = new JLabel("Select a card from the left");
        selectedCardLabel.setForeground(Color.WHITE);
        selectedCardLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        numberText.add(numberTitle);
        numberText.add(Box.createVerticalStrut(6));
        numberText.add(selectedCardLabel);

        JButton copyButton = new JButton(new CopyIcon(Color.WHITE, 22));
        copyButton.setToolTipText("Copy card number");
        copyButton.setPreferredSize(new Dimension(36, 36));
        copyButton.setBorderPainted(false);
        copyButton.setContentAreaFilled(false);
        copyButton.setFocusPainted(false);
        copyButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        copyButton.addActionListener(e -> {
            if (selectedCard == null || selectedCard.trim().isEmpty()) {
                warn("Please select a smart card first.");
                return;
            }

            try {
                java.awt.datatransfer.StringSelection selection =
                        new java.awt.datatransfer.StringSelection(selectedCard);

                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(selection, null);

                JOptionPane.showMessageDialog(
                        frame,
                        "Card number copied successfully!",
                        "Copied",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                warn("Could not copy card number.");
            }
        });

        numberPanel.add(numberText, BorderLayout.CENTER);
        numberPanel.add(copyButton, BorderLayout.EAST);

        // STATUS
        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        details.add(numberPanel);
        details.add(Box.createVerticalStrut(18));
        details.add(label("Status", new Color(221, 235, 255), REG));

        statusLabel = new JLabel("  NO CARD SELECTED  ");
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(225, 255, 239));
        statusLabel.setForeground(GREEN);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setBorder(new EmptyBorder(6, 10, 6, 10));

        JPanel statusWrap =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        statusWrap.setOpaque(false);
        statusWrap.add(statusLabel);
        details.add(statusWrap);

        // BALANCE + TOP UP
        JPanel bottom = new JPanel(new BorderLayout(12, 0));
        bottom.setOpaque(false);

        JPanel balancePanel = new JPanel();
        balancePanel.setOpaque(false);
        balancePanel.setLayout(
                new BoxLayout(balancePanel, BoxLayout.Y_AXIS));

        balancePanel.add(label(
                "Available Balance", new Color(221, 235, 255), REG));

        balanceLabel = new JLabel("₹0.00");
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 35));

        balancePanel.add(Box.createVerticalStrut(4));
        balancePanel.add(balanceLabel);

        JButton topUp = new JButton("+  Top Up");
        topUp.setFont(new Font("Segoe UI", Font.BOLD, 15));
        topUp.setForeground(new Color(0, 91, 220));
        topUp.setBackground(Color.WHITE);
        topUp.setOpaque(true);
        topUp.setFocusPainted(false);
        topUp.setBorderPainted(false);
        topUp.setBorder(new EmptyBorder(13, 20, 13, 20));
        topUp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        topUp.addActionListener(e -> rechargeCard());

        bottom.add(balancePanel, BorderLayout.WEST);
        bottom.add(topUp, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);
        panel.add(details, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        panel.setPreferredSize(new Dimension(490, 345));
        return panel;
    }

    // =========================================================
    // QR CODE PANEL
    // =========================================================

    private static JPanel buildQRPanel() {
        JPanel panel = whitePanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = heading("Smart Card QR Code", 18);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitle = label(
                "Scan this QR at metro gate for Entry and Exit",
                MUTED, new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setOpaque(false);
        header.add(title);
        header.add(subtitle);

        qrLabel = new JLabel(
                "Select a card to generate QR", SwingConstants.CENTER);
        qrLabel.setOpaque(true);
        qrLabel.setBackground(Color.WHITE);
        qrLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(5, 5, 5, 5)));

        JLabel instruction = label(
                "\u25A3   Use this QR at Metro Gate",
                BLUE, BOLD);
        instruction.setHorizontalAlignment(SwingConstants.CENTER);
        instruction.setOpaque(true);
        instruction.setBackground(PALE_BLUE);
        instruction.setBorder(new EmptyBorder(12, 5, 12, 5));

        panel.add(header, BorderLayout.NORTH);
        panel.add(qrLabel, BorderLayout.CENTER);
        panel.add(instruction, BorderLayout.SOUTH);
        panel.setPreferredSize(new Dimension(300, 330));

        return panel;
    }

    // =========================================================
    // RIGHT-SIDE ACTION BUTTONS
    // =========================================================

    private static JPanel buildActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 12));
        panel.setOpaque(false);

        JButton recharge = actionButton(
                "\u25A3", "Recharge Card", "Add balance to your card",
                new Color(235, 243, 255), BLUE);

        deactivateButton = actionButton(
                "\u23FB", "Deactivate Card", "Temporarily disable your card",
                new Color(232, 250, 242), GREEN);

        activateButton = actionButton(
                "\u25CF", "Activate Card", "Enable your card for use",
                new Color(255, 239, 239), RED);

        JButton details = actionButton(
                "\u25A4", "Card Details", "View complete card information",
                new Color(245, 239, 255), new Color(115, 72, 210));

        recharge.addActionListener(e -> rechargeCard());
        deactivateButton.addActionListener(e -> setCardStatus("INACTIVE"));
        activateButton.addActionListener(e -> setCardStatus("ACTIVE"));
        details.addActionListener(e -> showCardDetails());

        panel.add(recharge);
        panel.add(deactivateButton);
        panel.add(activateButton);
        panel.add(details);

        panel.setPreferredSize(new Dimension(255, 330));
        return panel;
    }

    private static JButton actionButton(
            String iconText, String title, String subtitle,
            Color background, Color accent) {

        JButton button = new JButton();

        JPanel content = new JPanel(new BorderLayout(12, 0));
        content.setOpaque(false);

        JLabel icon = new JLabel(iconText, SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Symbol", Font.BOLD, 22));
        icon.setForeground(accent);
        icon.setOpaque(true);
        icon.setBackground(Color.WHITE);
        icon.setPreferredSize(new Dimension(45, 45));
        icon.setBorder(BorderFactory.createLineBorder(
                new Color(226, 233, 246)));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(TEXT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitleLabel.setForeground(MUTED);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        content.add(icon, BorderLayout.WEST);
        content.add(textPanel, BorderLayout.CENTER);

        button.setLayout(new BorderLayout());
        button.add(content, BorderLayout.CENTER);
        button.setBackground(background);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 12, 12, 10)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    // =========================================================
    // JOURNEY STATUS
    // =========================================================

    private static JPanel buildJourneyPanel() {
        JPanel panel = whitePanel(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel title = new JPanel();
        title.setOpaque(false);
        title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
        title.add(heading("Journey Status", 18));
        title.add(Box.createVerticalStrut(4));
        title.add(label(
                "Current metro journey information", MUTED, REG));

        JPanel rows = new JPanel(new GridLayout(1, 2, 12, 0));
        rows.setOpaque(false);

        entryLabel = journeyStatus(
                "ENTRY", GREEN, new Color(235, 250, 242));
        exitLabel = journeyStatus(
                "EXIT", RED, new Color(255, 239, 239));

        rows.add(entryLabel);
        rows.add(exitLabel);

        panel.add(title, BorderLayout.NORTH);
        panel.add(rows, BorderLayout.CENTER);
        return panel;
    }

    private static JLabel journeyStatus(
            String title, Color foreground, Color background) {

        JLabel label = new JLabel("  " + title + "     Not Scanned     —");
        label.setFont(BOLD);
        label.setForeground(foreground);
        label.setBackground(background);
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(16, 12, 16, 12));

        return label;
    }

    // =========================================================
    // TRANSACTION HISTORY PANEL
    // =========================================================

    private static JPanel buildTransactionsPanel() {
        JPanel panel = whitePanel(new BorderLayout(0, 10));
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.add(heading("Recent Transactions", 18));
        titleStack.add(Box.createVerticalStrut(4));
        titleStack.add(label(
                "Your card activity including recharges and journeys.",
                MUTED, REG));

        JButton history = button("View Full History  >", Color.WHITE);
        history.setForeground(BLUE);
        history.setBorder(BorderFactory.createLineBorder(BLUE));
        history.addActionListener(e -> showFullHistory());

        top.add(titleStack, BorderLayout.WEST);
        top.add(history, BorderLayout.EAST);

        String[] columns = {
                "Date & Time", "Type", "Description",
                "Amount (₹)", "Balance (₹)"
        };

        transactionModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        transactionTable = new JTable(transactionModel);
        styleTable(transactionTable);
        transactionTable.setRowHeight(29);

        JScrollPane scroll = new JScrollPane(transactionTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================
    // SELECTED CARD AND QR
    // =========================================================

    private static void updateSelectedCard(String status) {
        selectedCardLabel.setText(selectedCard);
        balanceLabel.setText("₹" + selectedBalance.toPlainString());

        boolean active = "ACTIVE".equalsIgnoreCase(status);

        statusLabel.setText("  " + status.toUpperCase() + "  ");
        statusLabel.setForeground(active ? GREEN : RED);
        statusLabel.setBackground(active
                ? new Color(225, 255, 239)
                : new Color(255, 229, 230));

        activateButton.setEnabled(!active);
        deactivateButton.setEnabled(active);

        generateQR(selectedCard);
    }

    private static void generateQR(String cardNumber) {
        try {
            String payload = "METRO_SMART_CARD:" + cardNumber;

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(
                    payload, BarcodeFormat.QR_CODE, 220, 220);

            BufferedImage image =
                    MatrixToImageWriter.toBufferedImage(matrix);

            qrLabel.setText("");
            qrLabel.setIcon(new ImageIcon(image));
            qrLabel.setToolTipText("QR linked to " + cardNumber);

        } catch (WriterException ex) {
            qrLabel.setIcon(null);
            qrLabel.setText("Could not generate QR code");
        }
    }

    // =========================================================
    // LOAD CARDS FROM DATABASE
    // =========================================================

    private static void loadCards() {
        if (cardModel == null) {
            return;
        }

        String previous = selectedCard;
        cardModel.setRowCount(0);

        try {
            for (String[] card : SmartCardService.getAllCards()) {
                if (card == null || card.length < 4) {
                    continue;
                }

                cardModel.addRow(new Object[] {
                        card[0], money(card[2]), card[3], card[1]
                });
            }

            if (!previous.isEmpty()) {
                selectCard(previous);
            }

            if (cardTable.getSelectedRow() < 0
                    && cardModel.getRowCount() > 0) {
                cardTable.setRowSelectionInterval(0, 0);
            }

            if (cardModel.getRowCount() == 0) {
                clearSelectedCard();
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private static void selectCard(String number) {
        for (int r = 0; r < cardModel.getRowCount(); r++) {
            if (number.equals(String.valueOf(
                    cardModel.getValueAt(r, 0)))) {

                int view = cardTable.convertRowIndexToView(r);

                if (view >= 0) {
                    cardTable.setRowSelectionInterval(view, view);
                    return;
                }
            }
        }
    }

    private static void clearSelectedCard() {
        selectedCard = "";
        selectedBalance = BigDecimal.ZERO;

        selectedCardLabel.setText("Select a card from the left");
        balanceLabel.setText("₹0.00");
        statusLabel.setText("  NO CARD SELECTED  ");

        qrLabel.setIcon(null);
        qrLabel.setText("Select a card to generate QR");

        transactionModel.setRowCount(0);
    }

    // =========================================================
    // ISSUE NEW VIRTUAL CARD
    // =========================================================

    private static void issueCard() {
        try {
            String number = SmartCardService.issueVirtualCard();

            JOptionPane.showMessageDialog(
                    frame,
                    "Virtual smart card issued successfully!\n"
                            + "Card Number: " + number,
                    "Card Issued",
                    JOptionPane.INFORMATION_MESSAGE);

            loadCards();
            selectCard(number);

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // =========================================================
    // RECHARGE CARD
    // =========================================================

    private static void rechargeCard() {
        if (!requireCard()) {
            return;
        }

        int row = cardTable.getSelectedRow();

        if (row < 0) {
            warn("Please select a card from the list.");
            return;
        }

        row = cardTable.convertRowIndexToModel(row);

        String status = String.valueOf(cardModel.getValueAt(row, 2));

        if (!"ACTIVE".equalsIgnoreCase(status)) {
            warn("Only active cards can be recharged.");
            return;
        }

        String[] amounts = {
                "₹100", "₹200", "₹500", "₹1000", "Custom amount"
        };

        Object choice = JOptionPane.showInputDialog(
                frame, "Select recharge amount",
                "Recharge Card", JOptionPane.QUESTION_MESSAGE,
                null, amounts, amounts[2]);

        if (choice == null) {
            return;
        }

        try {
            BigDecimal amount;

            if ("Custom amount".equals(choice)) {
                String value = JOptionPane.showInputDialog(
                        frame, "Enter amount in ₹:");

                if (value == null) {
                    return;
                }

                amount = new BigDecimal(value.trim());
            } else {
                amount = new BigDecimal(
                        choice.toString().replace("₹", ""));
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "Amount must be greater than zero.");
            }

            String cardToRefresh = selectedCard;

            BigDecimal newBalance = SmartCardService.recharge(
                    cardToRefresh, amount);

            JOptionPane.showMessageDialog(
                    frame,
                    "Recharge successful!\nNew Balance: ₹"
                            + newBalance.toPlainString(),
                    "Recharge Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            loadCards();
            selectCard(cardToRefresh);

        } catch (SQLException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    frame, ex.getMessage(),
                    "Recharge Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // ACTIVATE / DEACTIVATE CARD
    // =========================================================

    private static void setCardStatus(String status) {
        if (!requireCard()) {
            return;
        }

        String cardToUpdate = selectedCard;

        int choice = JOptionPane.showConfirmDialog(
                frame,
                ("ACTIVE".equals(status) ? "Activate" : "Deactivate")
                        + " card " + cardToUpdate + "?",
                "Confirm Card Status",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "UPDATE smart_cards SET status=? WHERE card_number=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, cardToUpdate);

            int updated = ps.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Card " + status.toLowerCase()
                                + " successfully.");

                loadCards();
                selectCard(cardToUpdate);
            } else {
                warn("Card not found in database.");
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // =========================================================
    // CARD DETAILS
    // =========================================================

    private static void showCardDetails() {
        if (!requireCard()) {
            return;
        }

        int row = cardTable.getSelectedRow();

        String type = "VIRTUAL";
        String status = "—";

        if (row >= 0) {
            row = cardTable.convertRowIndexToModel(row);
            type = String.valueOf(cardModel.getValueAt(row, 3));
            status = String.valueOf(cardModel.getValueAt(row, 2));
        }

        JOptionPane.showMessageDialog(
                frame,
                "Card Number: " + selectedCard
                        + "\nCard Type: " + type
                        + "\nBalance: ₹" + selectedBalance.toPlainString()
                        + "\nStatus: " + status
                        + "\nQR Payload: METRO_SMART_CARD:" + selectedCard,
                "Card Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================
    // RECENT TRANSACTIONS
    // =========================================================

    private static void loadTransactions() {
        if (transactionModel == null || selectedCard.isEmpty()) {
            return;
        }

        transactionModel.setRowCount(0);

        String sql =
                "SELECT transaction_type, amount, balance_after, "
                + "description FROM card_transactions "
                + "WHERE card_id=(SELECT card_id FROM smart_cards "
                + "WHERE card_number=?) "
                + "ORDER BY transaction_id DESC LIMIT 5";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, selectedCard);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactionModel.addRow(new Object[] {
                            "—",
                            rs.getString("transaction_type"),
                            rs.getString("description"),
                            rs.getBigDecimal("amount"),
                            rs.getBigDecimal("balance_after")
                    });
                }
            }

        } catch (SQLException ex) {
            transactionModel.addRow(new Object[] {
                    "—", "INFO",
                    "Transaction history unavailable",
                    "—", "—"
            });
        }
    }

    // =========================================================
    // FULL TRANSACTION HISTORY
    // =========================================================

    private static void showFullHistory() {
        if (!requireCard()) {
            return;
        }

        String sql =
                "SELECT transaction_type, amount, balance_after, "
                + "description FROM card_transactions "
                + "WHERE card_id=(SELECT card_id FROM smart_cards "
                + "WHERE card_number=?) "
                + "ORDER BY transaction_id DESC";

        DefaultTableModel model = new DefaultTableModel(
                new String[] {
                        "Type", "Amount (₹)",
                        "Balance After (₹)", "Description"
                }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, selectedCard);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                            rs.getString("transaction_type"),
                            rs.getBigDecimal("amount"),
                            rs.getBigDecimal("balance_after"),
                            rs.getString("description")
                    });
                }
            }

            JTable historyTable = new JTable(model);
            styleTable(historyTable);
            historyTable.setRowHeight(29);

            JScrollPane scroll = new JScrollPane(historyTable);
            scroll.setPreferredSize(new Dimension(760, 380));

            JOptionPane.showMessageDialog(
                    frame, scroll,
                    "Transaction History - " + selectedCard,
                    JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // =========================================================
    // GENERAL HELPERS
    // =========================================================

    private static boolean requireCard() {
        if (selectedCard == null || selectedCard.trim().isEmpty()) {
            warn("Please select a smart card first.");
            return false;
        }
        return true;
    }

    private static JPanel whitePanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(1, 1, 1, 1)));
        return panel;
    }

    private static JLabel heading(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, size));
        label.setForeground(NAVY);
        return label;
    }

    private static JLabel label(String text, Color color, Font font) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(font);
        return label;
    }

    private static JButton button(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setForeground(background.equals(Color.WHITE)
                ? BLUE : Color.WHITE);
        button.setBackground(background);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(11, 14, 11, 14));
        return button;
    }

    private static void styleTable(JTable table) {
        table.setFont(REG);
        table.setForeground(TEXT);
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(231, 238, 248));
        table.setSelectionBackground(new Color(213, 231, 255));
        table.setSelectionForeground(NAVY);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(BOLD);
        header.setForeground(NAVY);
        header.setBackground(PALE_BLUE);
        header.setPreferredSize(new Dimension(0, 35));
        header.setReorderingAllowed(false);
    }

    private static BigDecimal parseMoney(String text) {
        try {
            return new BigDecimal(
                    text.replace("₹", "")
                            .replace(",", "")
                            .trim());
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private static String money(String text) {
        try {
            return new BigDecimal(
                    text.replace("₹", "")
                            .replace(",", "")
                            .trim())
                    .setScale(2, java.math.RoundingMode.HALF_UP)
                    .toPlainString();
        } catch (Exception ex) {
            return text;
        }
    }

    private static void warn(String message) {
        JOptionPane.showMessageDialog(
                frame, message, "Smart Card",
                JOptionPane.WARNING_MESSAGE);
    }

    private static void showError(SQLException ex) {
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                frame,
                "Database operation failed:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // CUSTOM CARD BACKGROUND
    // =========================================================

    private static class MetroCardBackgroundPanel extends JPanel {
        MetroCardBackgroundPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g.setPaint(new GradientPaint(
                    0, 0, new Color(5, 48, 135),
                    w, h, new Color(18, 104, 220)));

            g.fillRoundRect(0, 0, w, h, 24, 24);

            // Subtle metro tunnel perspective
            g.setStroke(new BasicStroke(2f));
            g.setColor(new Color(255, 255, 255, 20));

            for (int i = 0; i < 6; i++) {
                int inset = 20 + i * 15;
                g.drawRoundRect(
                        w / 2 - inset,
                        h / 2 - inset / 2,
                        w / 2 + inset / 2,
                        h / 2 + inset,
                        26, 26);
            }

            g.setColor(new Color(255, 255, 255, 28));
            g.drawLine(w / 2, h, w * 3 / 4, h / 2);
            g.drawLine(w * 3 / 4, h / 2, w, h / 2 - 24);
            g.drawLine(w * 2 / 3, h, w * 4 / 5, h / 2);

            // Decorative train illustration
            int tx = w - 145;
            int ty = h / 2 - 2;

            g.setColor(new Color(255, 255, 255, 24));
            g.fillRoundRect(tx, ty, 104, 78, 18, 18);

            g.setColor(new Color(255, 255, 255, 35));
            g.fillRoundRect(tx + 10, ty + 10, 84, 28, 7, 7);
            g.fillOval(tx + 17, ty + 65, 17, 17);
            g.fillOval(tx + 72, ty + 65, 17, 17);

            g.dispose();
            super.paintComponent(graphics);
        }
    }

    // =========================================================
    // CUSTOM METRO SMART-CARD ICON
    // =========================================================

    private static class MetroCardIcon implements Icon {
        private final Color color;
        private final int size;

        MetroCardIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

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
                Component c, Graphics graphics, int x, int y) {

            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(color);
            g.setStroke(new BasicStroke(2.4f));

            g.drawRoundRect(
                    x + 3, y + 5, size - 6, size - 10, 5, 5);

            g.drawLine(x + 7, y + 11, x + size - 7, y + 11);
            g.drawOval(x + 8, y + 17, 5, 5);
            g.drawOval(x + size - 13, y + 17, 5, 5);
            g.drawLine(
                    x + 8, y + size - 9,
                    x + size - 8, y + size - 9);

            g.dispose();
        }
    }

    // =========================================================
    // CUSTOM METRO ROUND LOGO
    // =========================================================

    private static class MetroLogoIcon implements Icon {
        private final int size;

        MetroLogoIcon(int size) {
            this.size = size;
        }

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
                Component c, Graphics graphics, int x, int y) {

            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(Color.WHITE);
            g.fillOval(x + 2, y + 2, size - 4, size - 4);

            g.setColor(new Color(220, 35, 45));
            g.setStroke(new BasicStroke(4f));
            g.drawOval(x + 8, y + 8, size - 16, size - 16);

            int left = x + 13;
            int right = x + size - 13;
            int top = y + size / 2 - 8;
            int bottom = y + size / 2 + 8;

            g.drawLine(left, top, right, top);
            g.drawLine(left, bottom, right, bottom);
            g.drawLine(left + 5, bottom, right - 5, top);

            g.dispose();
        }
    }

    // =========================================================
    // CUSTOM COPY ICON
    // =========================================================

    private static class CopyIcon implements Icon {
        private final Color color;
        private final int size;

        CopyIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

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
                Component c, Graphics graphics, int x, int y) {

            Graphics2D g = (Graphics2D) graphics.create();

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(color);
            g.setStroke(new BasicStroke(2f));

            g.drawRoundRect(x + 7, y + 2, 12, 15, 3, 3);
            g.drawRoundRect(x + 3, y + 6, 12, 14, 3, 3);

            g.dispose();
        }
    }
}
