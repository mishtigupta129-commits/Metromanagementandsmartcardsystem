package com.metromanagement.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import com.metromanagement.database.DBConnection;

public class TicketFrame {

    private static final Color NAVY = new Color(15, 43, 79);
    private static final Color BLUE = new Color(20, 105, 235);
    private static final Color BG = new Color(246, 249, 254);
    private static final Color BORDER = new Color(220, 231, 245);
    private static final Color MUTED = new Color(105, 123, 148);
    private static final Color LIGHT_BLUE = new Color(232, 242, 255);
    private static final Color GREEN = new Color(12, 125, 75);
    private static final Color RED = new Color(205, 48, 48);

    private static JFrame frame;

    private static JComboBox<String> cardBox;
    private static JComboBox<String> sourceBox;
    private static JComboBox<String> destinationBox;

    private static JTable table;
    private static DefaultTableModel model;
    private static TableRowSorter<DefaultTableModel> sorter;

    private static JTextField searchField;

    private static JLabel totalTicketsValue;
    private static JLabel bookedTicketsValue;
    private static JLabel totalFareValue;
    private static JLabel routeLabel;
    private static JLabel fareLabel;
    private static JLabel recordCount;

    private static final DecimalFormat MONEY =
            new DecimalFormat("#,##0.00");

    public static void showManagement() {

        if (frame != null && frame.isDisplayable()) {
            frame.setVisible(true);
            frame.toFront();
            refreshAll();
            return;
        }

        frame = new JFrame("Metro Tickets");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1536, 900);
        frame.setMinimumSize(new Dimension(1050, 700));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG);

        // ================= HEADER =================

        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(10, 24, 10, 24));

        JLabel metroIcon = new JLabel(new MetroIcon(BLUE, 42));
        metroIcon.setOpaque(true);
        metroIcon.setBackground(LIGHT_BLUE);
        metroIcon.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headingText = new JPanel();
        headingText.setOpaque(false);
        headingText.setLayout(
                new BoxLayout(headingText, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Metro Ticket Booking");
        title.setFont(new Font("Segoe UI", Font.BOLD, 29));
        title.setForeground(NAVY);

        JLabel subtitle = new JLabel(
                "Book your metro tickets using your smart card");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        headingText.add(title);
        headingText.add(Box.createVerticalStrut(3));
        headingText.add(subtitle);

        JPanel heading = new JPanel(new BorderLayout(15, 0));
        heading.setOpaque(false);
        heading.add(metroIcon, BorderLayout.WEST);
        heading.add(headingText, BorderLayout.CENTER);

        JLabel portal = new JLabel("PASSENGER PORTAL");
        portal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        portal.setForeground(BLUE);
        portal.setIcon(new UserIcon(BLUE, 18));
        portal.setIconTextGap(8);
        portal.setOpaque(true);
        portal.setBackground(LIGHT_BLUE);
        portal.setBorder(new EmptyBorder(12, 14, 12, 14));

        header.add(heading, BorderLayout.WEST);
        header.add(portal, BorderLayout.EAST);

        // ================= SUMMARY CARDS =================

        JPanel summaryPanel = new JPanel(
                new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(BG);

        JPanel totalCard = createSummaryCard(
                new TicketIcon(BLUE, 36),
                "Total Tickets",
                "0",
                "All tickets in the system",
                new Color(232, 242, 255));

        JPanel bookedCard = createSummaryCard(
                new CheckIcon(GREEN, 36),
                "Booked Tickets",
                "0",
                "Successfully booked",
                new Color(225, 246, 239));

        JPanel fareCard = createSummaryCard(
                new WalletIcon(new Color(220, 139, 0), 36),
                "Total Fare",
                "₹0.00",
                "Total fare of booked tickets",
                new Color(255, 244, 224));

        totalTicketsValue = (JLabel)
                totalCard.getClientProperty("valueLabel");

        bookedTicketsValue = (JLabel)
                bookedCard.getClientProperty("valueLabel");

        totalFareValue = (JLabel)
                fareCard.getClientProperty("valueLabel");

        summaryPanel.add(totalCard);
        summaryPanel.add(bookedCard);
        summaryPanel.add(fareCard);

        // ================= BOOKING PANEL =================

        JPanel bookingPanel = createPanel();

        JPanel bookingHeading = new JPanel();
        bookingHeading.setOpaque(false);
        bookingHeading.setLayout(
                new BoxLayout(bookingHeading, BoxLayout.Y_AXIS));
        bookingHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel bookingTitle = new JLabel("Book a New Ticket");
        bookingTitle.setFont(
                new Font("Segoe UI", Font.BOLD, 22));
        bookingTitle.setForeground(NAVY);
        bookingTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel bookingSubtitle = new JLabel(
                "Select your smart card and journey details to book a metro ticket.");
        bookingSubtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 13));
        bookingSubtitle.setForeground(MUTED);
        bookingSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        bookingHeading.add(bookingTitle);
        bookingHeading.add(Box.createVerticalStrut(3));
        bookingHeading.add(bookingSubtitle);

        cardBox = new JComboBox<>();
        sourceBox = new JComboBox<>();
        destinationBox = new JComboBox<>();

        styleCombo(cardBox);
        styleCombo(sourceBox);
        styleCombo(destinationBox);

        JPanel fields = new JPanel(
                new GridLayout(1, 3, 22, 0));
        fields.setOpaque(false);

        fields.add(createField(
                "Smart Card Number",
                new CardIcon(BLUE, 20),
                cardBox));

        fields.add(createField(
                "Source Station",
                new PinIcon(BLUE, 20),
                sourceBox));

        fields.add(createField(
                "Destination Station",
                new PinIcon(BLUE, 20),
                destinationBox));

        // Route preview

        JPanel routePreview = new JPanel(
                new BorderLayout(16, 0));
        routePreview.setBackground(LIGHT_BLUE);
        routePreview.setBorder(
                new EmptyBorder(8, 14, 8, 14));

        JLabel routeIcon = new JLabel(
                new RouteIcon(BLUE, 42));

        JPanel routeDetails = new JPanel();
        routeDetails.setOpaque(false);
        routeDetails.setLayout(
                new BoxLayout(routeDetails, BoxLayout.Y_AXIS));

        JLabel selectedRoute = new JLabel("Selected Route");
        selectedRoute.setFont(
                new Font("Segoe UI", Font.PLAIN, 13));
        selectedRoute.setForeground(BLUE);

        routeLabel = new JLabel("Select your stations");
        routeLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 21));
        routeLabel.setForeground(NAVY);

        JLabel routeDescription = new JLabel(
                "Single Journey · Standard Metro Route");
        routeDescription.setFont(
                new Font("Segoe UI", Font.PLAIN, 12));
        routeDescription.setForeground(MUTED);

        routeDetails.add(selectedRoute);
        routeDetails.add(Box.createVerticalStrut(3));
        routeDetails.add(routeLabel);
        routeDetails.add(Box.createVerticalStrut(3));
        routeDetails.add(routeDescription);

        JPanel farePanel = new JPanel(
                new BorderLayout(12, 0));
        farePanel.setOpaque(false);
        farePanel.setBorder(
                BorderFactory.createMatteBorder(
                        0, 1, 0, 0,
                        new Color(205, 223, 246)));
        farePanel.setPreferredSize(new Dimension(250, 60));

        JLabel rupeeIcon = new JLabel(
                new RupeeIcon(BLUE, 32));
        rupeeIcon.setBorder(
                new EmptyBorder(0, 14, 0, 0));

        JPanel fareDetails = new JPanel();
        fareDetails.setOpaque(false);
        fareDetails.setLayout(
                new BoxLayout(fareDetails, BoxLayout.Y_AXIS));

        JLabel fareTitle = new JLabel("Ticket Fare");
        fareTitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 13));
        fareTitle.setForeground(BLUE);

        fareLabel = new JLabel("₹0.00");
        fareLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 24));
        fareLabel.setForeground(NAVY);

        JLabel fareDescription = new JLabel(
                "Calculated for this route");
        fareDescription.setFont(
                new Font("Segoe UI", Font.PLAIN, 12));
        fareDescription.setForeground(MUTED);

        fareDetails.add(fareTitle);
        fareDetails.add(Box.createVerticalStrut(2));
        fareDetails.add(fareLabel);
        fareDetails.add(Box.createVerticalStrut(2));
        fareDetails.add(fareDescription);

        farePanel.add(rupeeIcon, BorderLayout.WEST);
        farePanel.add(fareDetails, BorderLayout.CENTER);

        routePreview.add(routeIcon, BorderLayout.WEST);
        routePreview.add(routeDetails, BorderLayout.CENTER);
        routePreview.add(farePanel, BorderLayout.EAST);

        // Buttons

        JButton bookButton = createButton(
                "Book Ticket",
                new TicketIcon(Color.WHITE, 19),
                BLUE);

        JButton refreshButton = createButton(
                "Refresh",
                new RefreshIcon(Color.WHITE, 19),
                NAVY);

        JButton cancelButton = createButton(
                "Cancel Selected Ticket",
                new CancelIcon(Color.WHITE, 19),
                RED);

        JPanel actions = new JPanel(
                new GridLayout(1, 3, 14, 0));
        actions.setOpaque(false);

        actions.add(bookButton);
        actions.add(refreshButton);
        actions.add(cancelButton);

        JPanel bookingBody = new JPanel();
        bookingBody.setOpaque(false);
        bookingBody.setLayout(
                new BoxLayout(bookingBody, BoxLayout.Y_AXIS));

        bookingBody.add(bookingHeading);
        bookingBody.add(Box.createVerticalStrut(8));
        bookingBody.add(fields);
        bookingBody.add(Box.createVerticalStrut(8));
        bookingBody.add(routePreview);
        bookingBody.add(Box.createVerticalStrut(8));
        bookingBody.add(actions);

        bookingPanel.add(bookingBody, BorderLayout.CENTER);

        // ================= MY TICKETS =================

        JPanel ticketsPanel = createPanel();

        JPanel ticketsHeading = new JPanel(
                new BorderLayout(15, 0));
        ticketsHeading.setOpaque(false);

        JPanel ticketTitles = new JPanel();
        ticketTitles.setOpaque(false);
        ticketTitles.setLayout(
                new BoxLayout(ticketTitles, BoxLayout.Y_AXIS));

        JLabel ticketsTitle = new JLabel("My Tickets");
        ticketsTitle.setFont(
                new Font("Segoe UI", Font.BOLD, 22));
        ticketsTitle.setForeground(NAVY);

        JLabel ticketsSubtitle = new JLabel(
                "View and manage your booked metro tickets.");
        ticketsSubtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 13));
        ticketsSubtitle.setForeground(MUTED);

        ticketTitles.add(ticketsTitle);
        ticketTitles.add(Box.createVerticalStrut(3));
        ticketTitles.add(ticketsSubtitle);

        searchField = new JTextField();
        styleTextField(searchField);
        searchField.setPreferredSize(new Dimension(300, 36));
        searchField.setToolTipText(
                "Search ticket number or card number");

        JPanel searchBox = new JPanel(
                new BorderLayout(8, 0));
        searchBox.setOpaque(false);

        searchBox.add(
                new JLabel(new SearchIcon(BLUE, 19)),
                BorderLayout.WEST);
        searchBox.add(searchField, BorderLayout.CENTER);

        ticketsHeading.add(
                ticketTitles, BorderLayout.WEST);
        ticketsHeading.add(
                searchBox, BorderLayout.EAST);

        String[] columns = {
            "Ticket ID",
            "Ticket Number",
            "Card Number",
            "Source",
            "Destination",
            "Type",
            "Fare (₹)",
            "Booking Time",
            "Status"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(
                    int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setFont(
                new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(NAVY);
        table.setBackground(Color.WHITE);

        // Compact rows so multiple bookings fit onscreen.
        table.setRowHeight(32);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(
                new Color(211, 231, 255));
        table.setSelectionForeground(NAVY);
        table.setShowGrid(true);
        table.setShowVerticalLines(false);
        table.setGridColor(
                new Color(230, 237, 247));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(
                new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setForeground(NAVY);
        tableHeader.setBackground(LIGHT_BLUE);
        tableHeader.setPreferredSize(
                new Dimension(0, 34));
        tableHeader.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.getColumnModel().getColumn(8)
                .setCellRenderer(new StatusRenderer());

        DefaultTableCellRenderer centered =
                new DefaultTableCellRenderer();
        centered.setHorizontalAlignment(
                SwingConstants.CENTER);

        table.getColumnModel().getColumn(0)
                .setCellRenderer(centered);
        table.getColumnModel().getColumn(5)
                .setCellRenderer(centered);
        table.getColumnModel().getColumn(6)
                .setCellRenderer(centered);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(
                BorderFactory.createLineBorder(BORDER));
        tableScroll.getViewport().setBackground(Color.WHITE);

        table.setPreferredScrollableViewportSize(
                new Dimension(1100, 220));
        tableScroll.setMinimumSize(
                new Dimension(500, 160));

        recordCount = new JLabel("Showing 0 tickets");
        recordCount.setFont(
                new Font("Segoe UI", Font.PLAIN, 13));
        recordCount.setForeground(MUTED);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(
                new EmptyBorder(5, 2, 0, 2));
        footer.add(recordCount, BorderLayout.WEST);

        ticketsPanel.add(
                ticketsHeading, BorderLayout.NORTH);
        ticketsPanel.add(
                tableScroll, BorderLayout.CENTER);
        ticketsPanel.add(
                footer, BorderLayout.SOUTH);

        // ================= MAIN LAYOUT =================

        JPanel content = new JPanel(
                new BorderLayout(0, 12));
        content.setBackground(BG);
        content.setBorder(
                new EmptyBorder(10, 24, 10, 24));

        /*
         * Compact upper section.
         * The tickets table receives the remaining vertical space.
         */
        JPanel upperSection = new JPanel(
                new BorderLayout(0, 10));
        upperSection.setBackground(BG);

        upperSection.add(
                summaryPanel, BorderLayout.NORTH);
        upperSection.add(
                bookingPanel, BorderLayout.CENTER);

        upperSection.setPreferredSize(
                new Dimension(1200, 390));
        upperSection.setMinimumSize(
                new Dimension(900, 370));

        content.add(
                upperSection, BorderLayout.NORTH);
        content.add(
                ticketsPanel, BorderLayout.CENTER);

        frame.add(header, BorderLayout.NORTH);
        frame.add(content, BorderLayout.CENTER);

        // ================= EVENT LISTENERS =================

        bookButton.addActionListener(
                e -> bookTicket());

        refreshButton.addActionListener(
                e -> refreshAll());

        cancelButton.addActionListener(
                e -> cancelTicket());

        sourceBox.addActionListener(
                e -> updateRoutePreview());

        destinationBox.addActionListener(
                e -> updateRoutePreview());

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTickets();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTickets();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTickets();
            }
        });

        frame.setVisible(true);
        refreshAll();
    }

    // ================= SUMMARY CARD =================

    private static JPanel createSummaryCard(
            Icon icon,
            String heading,
            String value,
            String description,
            Color background) {

        JPanel panel = new JPanel(
                new BorderLayout(16, 0));
        panel.setBackground(background);
        panel.setBorder(
                new EmptyBorder(9, 13, 9, 13));

        JLabel iconLabel = new JLabel(
                icon, SwingConstants.CENTER);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(Color.WHITE);
        iconLabel.setPreferredSize(
                new Dimension(58, 58));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(
                new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel h = new JLabel(heading);
        h.setFont(
                new Font("Segoe UI", Font.BOLD, 14));
        h.setForeground(NAVY);

        JLabel v = new JLabel(value);
        v.setFont(
                new Font("Segoe UI", Font.BOLD, 28));
        v.setForeground(NAVY);

        JLabel d = new JLabel(description);
        d.setFont(
                new Font("Segoe UI", Font.PLAIN, 12));
        d.setForeground(MUTED);

        text.add(h);
        text.add(Box.createVerticalStrut(4));
        text.add(v);
        text.add(Box.createVerticalStrut(3));
        text.add(d);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(text, BorderLayout.CENTER);
        panel.putClientProperty("valueLabel", v);

        return panel;
    }

    // ================= PANEL HELPERS =================

    private static JPanel createPanel() {
        JPanel p = new JPanel(
                new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(10, 14, 10, 14)));
        return p;
    }

    private static JPanel createField(
            String title, Icon icon, JComponent input) {

        JPanel p = new JPanel(
                new BorderLayout(0, 8));
        p.setOpaque(false);

        JLabel label = new JLabel(title);
        label.setFont(
                new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(NAVY);

        JPanel inputContainer = new JPanel(
                new BorderLayout(9, 0));
        inputContainer.setBackground(Color.WHITE);
        inputContainer.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(0, 10, 0, 8)));

        JLabel iconLabel = new JLabel(icon);
        inputContainer.add(iconLabel, BorderLayout.WEST);
        inputContainer.add(input, BorderLayout.CENTER);

        p.add(label, BorderLayout.NORTH);
        p.add(inputContainer, BorderLayout.CENTER);

        return p;
    }

    private static void styleCombo(
            JComboBox<String> combo) {

        combo.setFont(
                new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(NAVY);
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(
                new Dimension(200, 38));
        combo.setBorder(
                BorderFactory.createEmptyBorder());
    }

    private static void styleTextField(
            JTextField field) {

        field.setFont(
                new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(NAVY);
        field.setBackground(Color.WHITE);
        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(8, 10, 8, 10)));
    }

    private static JButton createButton(
            String text, Icon icon, Color color) {

        JButton b = new JButton(text);
        b.setIcon(icon);
        b.setIconTextGap(10);
        b.setFont(
                new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(
                new Dimension(180, 42));
        b.setBorder(
                new EmptyBorder(10, 10, 10, 10));

        return b;
    }

    // ================= LOAD SMART CARDS =================

    private static void loadCards() {

        String previous =
                (String) cardBox.getSelectedItem();

        cardBox.removeAllItems();

        String sql =
                "SELECT card_number FROM smart_cards " +
                "WHERE status = 'ACTIVE' ORDER BY card_number";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cardBox.addItem(
                        rs.getString("card_number"));
            }

            if (previous != null) {
                cardBox.setSelectedItem(previous);
            }

        } catch (SQLException ex) {
            showError("Could not load smart cards", ex);
        }
    }

    // ================= LOAD ALL STATIONS =================

    private static void loadStations() {

        String previousSource =
                (String) sourceBox.getSelectedItem();

        String previousDestination =
                (String) destinationBox.getSelectedItem();

        sourceBox.removeAllItems();
        destinationBox.removeAllItems();

        String sql =
                "SELECT station_name FROM metro_stations " +
                "ORDER BY station_order ASC, station_name ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int count = 0;

            while (rs.next()) {

                String station =
                        rs.getString("station_name");

                if (station != null &&
                        !station.trim().isEmpty()) {

                    sourceBox.addItem(station);
                    destinationBox.addItem(station);
                    count++;
                }
            }

            if (previousSource != null) {
                sourceBox.setSelectedItem(previousSource);
            }

            if (previousDestination != null) {
                destinationBox.setSelectedItem(
                        previousDestination);
            }

            System.out.println(
                    "Stations loaded: " + count);

            if (count == 0) {
                JOptionPane.showMessageDialog(
                        frame,
                        "No stations were found in metro_stations.",
                        "No Stations",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException ex) {
            showError(
                    "Could not load stations. Check the station table columns.",
                    ex);
        }
    }

    // ================= ROUTE / FARE =================

    private static double calculateFare() {

        int a = sourceBox.getSelectedIndex();
        int b = destinationBox.getSelectedIndex();

        if (a < 0 || b < 0) {
            return 0;
        }

        return Math.max(10, Math.abs(a - b) * 5.0);
    }

    private static void updateRoutePreview() {

        if (routeLabel == null || fareLabel == null) {
            return;
        }

        String source =
                (String) sourceBox.getSelectedItem();

        String destination =
                (String) destinationBox.getSelectedItem();

        if (source == null || destination == null) {
            routeLabel.setText("Select your stations");
            fareLabel.setText("₹0.00");
            return;
        }

        routeLabel.setText(
                source + "    →    " + destination);

        if (source.equals(destination)) {
            fareLabel.setText("Not available");
        } else {
            fareLabel.setText(
                    "₹" + MONEY.format(calculateFare()));
        }
    }

    // ================= BOOK TICKET =================

    private static void bookTicket() {

        String card =
                (String) cardBox.getSelectedItem();

        String source =
                (String) sourceBox.getSelectedItem();

        String destination =
                (String) destinationBox.getSelectedItem();

        if (card == null ||
                source == null ||
                destination == null) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Select a smart card, source and destination.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (source.equals(destination)) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Source and destination must be different.",
                    "Invalid Route",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double fare = calculateFare();
        String ticketNumber =
                "MT" + System.currentTimeMillis();

        String sql =
                "INSERT INTO tickets " +
                "(ticket_number, card_number, source_station, " +
                "destination_station, ticket_type, fare, ticket_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'BOOKED')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ticketNumber);
            ps.setString(2, card);
            ps.setString(3, source);
            ps.setString(4, destination);
            ps.setString(5, "SINGLE");
            ps.setBigDecimal(
                    6, BigDecimal.valueOf(fare));

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    frame,
                    "Ticket booked successfully!\n\n"
                    + "Ticket Number: " + ticketNumber
                    + "\nRoute: " + source + " → " + destination
                    + "\nFare: ₹" + MONEY.format(fare),
                    "Booking Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshAll();

        } catch (SQLException ex) {
            showError("Ticket booking failed", ex);
        }
    }

    // ================= LOAD TICKETS =================

    private static void loadTickets() {

        model.setRowCount(0);

        String sql =
                "SELECT ticket_id, ticket_number, card_number, " +
                "source_station, destination_station, ticket_type, " +
                "fare, booking_time, ticket_status " +
                "FROM tickets ORDER BY ticket_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Timestamp timestamp =
                        rs.getTimestamp("booking_time");

                String bookingTime = timestamp == null
                        ? ""
                        : timestamp.toLocalDateTime().format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm:ss"));

                BigDecimal fare =
                        rs.getBigDecimal("fare");

                model.addRow(new Object[]{
                    rs.getInt("ticket_id"),
                    rs.getString("ticket_number"),
                    rs.getString("card_number"),
                    rs.getString("source_station"),
                    rs.getString("destination_station"),
                    rs.getString("ticket_type"),
                    fare == null
                            ? "0.00"
                            : MONEY.format(fare),
                    bookingTime,
                    rs.getString("ticket_status")
                });
            }

            filterTickets();
            table.revalidate();
            table.repaint();
            updateRecordCount();

            System.out.println(
                    "Ticket rows loaded: " + model.getRowCount());

        } catch (SQLException ex) {
            showError("Could not load tickets", ex);
        }
    }

    // ================= SUMMARY =================

    private static void loadSummary() {

        String sql =
                "SELECT COUNT(*) AS total_tickets, " +
                "COALESCE(SUM(CASE WHEN ticket_status = 'BOOKED' " +
                "THEN 1 ELSE 0 END), 0) AS booked_tickets, " +
                "COALESCE(SUM(CASE WHEN ticket_status = 'BOOKED' " +
                "THEN fare ELSE 0 END), 0) AS total_fare " +
                "FROM tickets";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                totalTicketsValue.setText(
                        String.valueOf(
                                rs.getInt("total_tickets")));

                bookedTicketsValue.setText(
                        String.valueOf(
                                rs.getInt("booked_tickets")));

                BigDecimal amount =
                        rs.getBigDecimal("total_fare");

                totalFareValue.setText(
                        "₹" + MONEY.format(
                                amount == null
                                        ? BigDecimal.ZERO
                                        : amount));
            }

        } catch (SQLException ex) {
            showError("Could not load ticket summary", ex);
        }
    }

    // ================= SEARCH =================

    private static void filterTickets() {

        if (sorter == null || searchField == null) {
            return;
        }

        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + Pattern.quote(query),
                            1, 2));
        }

        table.revalidate();
        table.repaint();
        updateRecordCount();
    }

    private static void updateRecordCount() {

        if (recordCount == null || table == null) {
            return;
        }

        int visible = table.getRowCount();
        int total = model.getRowCount();

        recordCount.setText(
                visible == total
                        ? "Showing " + total + " tickets"
                        : "Showing " + visible
                                + " of " + total + " tickets");
    }

    // ================= CANCEL TICKET =================

    private static void cancelTicket() {

        int selected = table.getSelectedRow();

        if (selected < 0) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Select a ticket from the table first.",
                    "No Ticket Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = table.convertRowIndexToModel(selected);

        int ticketId = Integer.parseInt(
                model.getValueAt(row, 0).toString());

        String status =
                model.getValueAt(row, 8).toString();

        if (!"BOOKED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Only booked tickets can be cancelled.",
                    "Cannot Cancel",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to cancel this ticket?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "UPDATE tickets SET ticket_status = 'CANCELLED' " +
                "WHERE ticket_id = ? AND ticket_status = 'BOOKED'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ticketId);

            if (ps.executeUpdate() == 0) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Ticket was not cancelled. Refresh and try again.");
            } else {
                JOptionPane.showMessageDialog(
                        frame,
                        "Ticket cancelled successfully.",
                        "Cancellation Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            refreshAll();

        } catch (SQLException ex) {
            showError("Could not cancel ticket", ex);
        }
    }

    // ================= REFRESH =================

    private static void refreshAll() {

        if (frame == null || !frame.isDisplayable()) {
            return;
        }

        loadCards();
        loadStations();
        loadTickets();
        loadSummary();
        updateRoutePreview();
    }

    // ================= STATUS RENDERER =================

    private static class StatusRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focus,
                int row,
                int column) {

            JLabel label = (JLabel)
                    super.getTableCellRendererComponent(
                            table, value, selected,
                            focus, row, column);

            String status = value == null
                    ? ""
                    : value.toString().toUpperCase();

            label.setHorizontalAlignment(
                    SwingConstants.CENTER);
            label.setFont(
                    new Font("Segoe UI", Font.BOLD, 12));
            label.setOpaque(true);

            if (selected) {
                label.setBackground(
                        table.getSelectionBackground());
                label.setForeground(NAVY);
            } else if ("BOOKED".equals(status)) {
                label.setBackground(
                        new Color(220, 247, 232));
                label.setForeground(GREEN);
            } else if ("CANCELLED".equals(status)) {
                label.setBackground(
                        new Color(255, 229, 229));
                label.setForeground(RED);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(NAVY);
            }

            return label;
        }
    }

    // ================= CUSTOM ICONS =================

    private abstract static class SimpleIcon implements Icon {

        protected final Color color;
        protected final int size;

        SimpleIcon(Color color, int size) {
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

        protected Graphics2D graphics(
                Graphics g, int x, int y) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(
                    2.2f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            return g2;
        }
    }

    private static class MetroIcon extends SimpleIcon {

        MetroIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);
            int s = size;

            a.drawRoundRect(
                    s / 5, s / 6,
                    s * 3 / 5, s * 3 / 5,
                    5, 5);

            a.drawLine(
                    s / 5, s * 2 / 5,
                    s * 4 / 5, s * 2 / 5);

            a.fillOval(
                    s / 3, s * 3 / 5, 4, 4);

            a.fillOval(
                    s * 3 / 5, s * 3 / 5, 4, 4);

            a.drawLine(
                    s / 3, s * 4 / 5,
                    s / 5, s * 9 / 10);

            a.drawLine(
                    s * 2 / 3, s * 4 / 5,
                    s * 4 / 5, s * 9 / 10);

            a.dispose();
        }
    }

    private static class TicketIcon extends SimpleIcon {

        TicketIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);

            a.drawRoundRect(
                    size / 7, size / 4,
                    size * 5 / 7, size / 2,
                    5, 5);

            a.drawLine(
                    size / 2, size / 4,
                    size / 2, size * 3 / 4);

            a.dispose();
        }
    }

    private static class CheckIcon extends SimpleIcon {

        CheckIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);

            a.drawOval(2, 2, size - 4, size - 4);

            a.drawLine(
                    size / 4, size / 2,
                    size * 2 / 5, size * 2 / 3);

            a.drawLine(
                    size * 2 / 5, size * 2 / 3,
                    size * 3 / 4, size / 3);

            a.dispose();
        }
    }

    private static class WalletIcon extends SimpleIcon {

        WalletIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);

            a.drawRoundRect(
                    2, size / 4,
                    size - 4, size * 3 / 5,
                    5, 5);

            a.drawRoundRect(
                    size / 2, size / 2,
                    size / 2 - 2, size / 4,
                    4, 4);

            a.fillOval(
                    size * 2 / 3, size * 9 / 16,
                    3, 3);

            a.dispose();
        }
    }

    private static class CardIcon extends SimpleIcon {

        CardIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);

            a.drawRoundRect(
                    2, size / 4,
                    size - 4, size / 2,
                    4, 4);

            a.drawLine(
                    3, size / 2,
                    size - 3, size / 2);

            a.dispose();
        }
    }

    private static class PinIcon extends SimpleIcon {

        PinIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);

            a.drawOval(
                    size / 4, 1,
                    size / 2, size / 2);

            a.drawLine(
                    size / 4, size / 3,
                    size / 2, size - 2);

            a.drawLine(
                    size * 3 / 4, size / 3,
                    size / 2, size - 2);

            a.fillOval(
                    size * 2 / 5, size / 5,
                    size / 5, size / 5);

            a.dispose();
        }
    }

    private static class RouteIcon extends SimpleIcon {

        RouteIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);

            a.drawOval(2, size / 2, 8, 8);
            a.drawOval(size - 10, size / 4, 8, 8);

            a.drawLine(
                    10, size / 2 + 4,
                    size / 3, size / 2 + 4);

            a.drawLine(
                    size / 3, size / 2 + 4,
                    size / 2, size / 3);

            a.drawLine(
                    size / 2, size / 3,
                    size * 2 / 3, size / 3);

            a.drawLine(
                    size * 2 / 3, size / 3,
                    size - 10, size / 4 + 4);

            a.dispose();
        }
    }

    private static class RupeeIcon extends SimpleIcon {

        RupeeIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);
            a.setFont(
                    new Font("Segoe UI", Font.BOLD, size - 2));
            a.drawString("₹", 1, size - 3);
            a.dispose();
        }
    }

    private static class SearchIcon extends SimpleIcon {

        SearchIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);

            a.drawOval(
                    2, 2, size * 2 / 3, size * 2 / 3);

            a.drawLine(
                    size * 2 / 3, size * 2 / 3,
                    size - 2, size - 2);

            a.dispose();
        }
    }

    private static class RefreshIcon extends SimpleIcon {

        RefreshIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);

            a.drawArc(
                    3, 3, size - 6, size - 6,
                    40, 285);

            a.drawLine(
                    size - 5, 3,
                    size - 5, size / 3);

            a.drawLine(
                    size - 5, 3,
                    size * 2 / 3, 4);

            a.dispose();
        }
    }

    private static class CancelIcon extends SimpleIcon {

        CancelIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);

            a.drawOval(2, 2, size - 4, size - 4);

            a.drawLine(
                    6, 6, size - 6, size - 6);

            a.drawLine(
                    size - 6, 6, 6, size - 6);

            a.dispose();
        }
    }

    private static class UserIcon extends SimpleIcon {

        UserIcon(Color c, int s) {
            super(c, s);
        }

        @Override
        public void paintIcon(
                Component c, Graphics g, int x, int y) {

            Graphics2D a = graphics(g, x, y);

            a.drawOval(
                    size / 3, 1,
                    size / 3, size / 3);

            a.drawArc(
                    2, size / 3,
                    size - 4, size * 2 / 3,
                    0, 180);

            a.dispose();
        }
    }

    // ================= ERROR HANDLING =================

    private static void showError(
            String message, Exception ex) {

        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                frame,
                message + "\n\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}