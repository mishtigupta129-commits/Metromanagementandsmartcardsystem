
package com.metromanagement.gui;

import com.metromanagement.database.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StationDetailsPanel extends JPanel {

    private final Color background = new Color(245, 248, 252);
    private final Color darkBlue = new Color(49, 91, 145);
    private final Color textColor = new Color(35, 45, 58);

    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pageContainer = new JPanel(pageLayout);

    private final JPanel lineListPanel = new JPanel();
    private final JPanel stationListPanel = new JPanel();

    private JLabel stationCountLabel;
    private JLabel selectedLineLabel;
    private JPanel stationHeader;

    public StationDetailsPanel() {
        setLayout(new BorderLayout());
        setBackground(background);

        pageContainer.setBackground(background);
        pageContainer.add(createLineSelectionPage(), "LINES");
        pageContainer.add(createStationListPage(), "STATIONS");

        add(pageContainer, BorderLayout.CENTER);

        loadMetroLines();
        pageLayout.show(pageContainer, "LINES");
    }

    // ================= LINE SELECTION PAGE =================

    private JPanel createLineSelectionPage() {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setBackground(background);
        page.setBorder(new EmptyBorder(24, 26, 20, 26));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 0, 0)
        );
        titleRow.setOpaque(false);

        JLabel metroIcon = new JLabel("\uD83D\uDE87");
        metroIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        metroIcon.setBorder(new EmptyBorder(0, 0, 0, 10));

        JLabel title = new JLabel("Know Your Station");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(darkBlue);

        titleRow.add(metroIcon);
        titleRow.add(title);

        JLabel subtitle = new JLabel(
                "Select a metro line to view all its stations"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(105, 120, 140));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.add(titleRow);
        heading.add(Box.createVerticalStrut(7));
        heading.add(subtitle);

        lineListPanel.setLayout(
                new BoxLayout(lineListPanel, BoxLayout.Y_AXIS)
        );
        lineListPanel.setBackground(background);
        lineListPanel.setBorder(new EmptyBorder(4, 3, 4, 8));

        JScrollPane scrollPane = new JScrollPane(lineListPanel);
        styleScrollPane(scrollPane);

        page.add(heading, BorderLayout.NORTH);
        page.add(scrollPane, BorderLayout.CENTER);

        return page;
    }

    // ================= LOAD METRO LINES =================

    private void loadMetroLines() {
        lineListPanel.removeAll();

        String sql =
                "SELECT line_id, line_name, start_station, end_station " +
                "FROM metro_lines ORDER BY line_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            boolean found = false;

            while (result.next()) {
                found = true;

                int lineId = result.getInt("line_id");
                String lineName = result.getString("line_name");
                String startStation = result.getString("start_station");
                String endStation = result.getString("end_station");

                JPanel card = createLineCard(
                        lineId,
                        lineName,
                        startStation,
                        endStation,
                        getLineColor(lineName)
                );

                lineListPanel.add(card);
                lineListPanel.add(Box.createVerticalStrut(12));
            }

            if (!found) {
                lineListPanel.add(createMessageLabel(
                        "No metro lines found in the database."
                ));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

            lineListPanel.add(createMessageLabel(
                    "Unable to load metro lines. Check the database connection."
            ));

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load metro lines:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        lineListPanel.revalidate();
        lineListPanel.repaint();
    }

    // ================= CLICKABLE LINE CARD =================

    private JPanel createLineCard(
            int lineId,
            String lineName,
            String startStation,
            String endStation,
            Color lineColor) {

        Color lightColor = blendWithWhite(lineColor, 0.87f);

        RoundedPanel card = new RoundedPanel(lightColor, lineColor);
        card.setLayout(new BorderLayout(8, 8));
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        card.setPreferredSize(new Dimension(650, 120));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(lineName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 17));
        nameLabel.setForeground(textColor);

        JLabel fromLabel = new JLabel(
                "<html><b>From:</b> " +
                escapeHtml(startStation) + "</html>"
        );
        fromLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        fromLabel.setForeground(textColor);

        JLabel toLabel = new JLabel(
                "<html><b>To:</b> " +
                escapeHtml(endStation) + "</html>"
        );
        toLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        toLabel.setForeground(textColor);

        JPanel routeInfo = new JPanel();
        routeInfo.setOpaque(false);
        routeInfo.setLayout(new BoxLayout(routeInfo, BoxLayout.Y_AXIS));

        routeInfo.add(nameLabel);
        routeInfo.add(Box.createVerticalStrut(9));
        routeInfo.add(fromLabel);
        routeInfo.add(Box.createVerticalStrut(5));
        routeInfo.add(toLabel);

        card.add(routeInfo, BorderLayout.CENTER);

        MouseAdapter cardClick = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showStations(lineId, lineName, lineColor);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackgroundColor(
                        blendWithWhite(lineColor, 0.78f)
                );
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackgroundColor(lightColor);
                card.repaint();
            }
        };

        card.addMouseListener(cardClick);
        addClickListenerToChildren(routeInfo, cardClick);

        return card;
    }

    private void addClickListenerToChildren(
            Container container,
            MouseAdapter listener) {

        for (Component component : container.getComponents()) {
            component.addMouseListener(listener);

            if (component instanceof Container) {
                addClickListenerToChildren(
                        (Container) component, listener
                );
            }
        }
    }

    // ================= STATION LIST PAGE =================

    private JPanel createStationListPage() {
        JPanel page = new JPanel(new BorderLayout(0, 15));
        page.setBackground(background);
        page.setBorder(new EmptyBorder(24, 26, 20, 26));

        stationHeader = new JPanel(new BorderLayout(10, 0));
        stationHeader.setBorder(new EmptyBorder(12, 16, 12, 16));
        stationHeader.setBackground(darkBlue);

        JButton backButton = new JButton("\u2190");
        backButton.setFont(new Font("Arial", Font.BOLD, 28));
        backButton.setForeground(Color.WHITE);
        backButton.setContentAreaFilled(false);
        backButton.setOpaque(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 8)
        );
        backButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        backButton.addActionListener(
                e -> pageLayout.show(pageContainer, "LINES")
        );

        selectedLineLabel = new JLabel("Metro Line");
        selectedLineLabel.setFont(new Font("Arial", Font.BOLD, 20));
        selectedLineLabel.setForeground(Color.WHITE);

        JPanel headerLeft = new JPanel(new BorderLayout(12, 0));
        headerLeft.setOpaque(false);
        headerLeft.add(backButton, BorderLayout.WEST);
        headerLeft.add(selectedLineLabel, BorderLayout.CENTER);

        stationHeader.add(headerLeft, BorderLayout.CENTER);

        stationCountLabel = new JLabel("0 Stations");
        stationCountLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        stationCountLabel.setForeground(textColor);

        stationListPanel.setLayout(
                new BoxLayout(stationListPanel, BoxLayout.Y_AXIS)
        );
        stationListPanel.setBackground(Color.WHITE);
        stationListPanel.setBorder(new EmptyBorder(12, 8, 12, 8));

        JScrollPane stationScroll = new JScrollPane(stationListPanel);
        styleScrollPane(stationScroll);
        stationScroll.getViewport().setBackground(Color.WHITE);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(background);
        center.add(stationCountLabel, BorderLayout.NORTH);
        center.add(stationScroll, BorderLayout.CENTER);

        page.add(stationHeader, BorderLayout.NORTH);
        page.add(center, BorderLayout.CENTER);

        return page;
    }

    // ================= LOAD SELECTED LINE STATIONS =================

    private void showStations(
            int lineId,
            String lineName,
            Color lineColor) {

        selectedLineLabel.setText(lineName);
        stationHeader.setBackground(lineColor);
        stationListPanel.removeAll();

        String sql =
                "SELECT s.station_name, ls.station_order " +
                "FROM metro_line_stations ls " +
                "JOIN metro_stations s " +
                "ON s.station_id = ls.station_id " +
                "WHERE ls.line_id = ? " +
                "ORDER BY ls.station_order";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, lineId);

            try (ResultSet result = statement.executeQuery()) {
                int count = 0;

                while (result.next()) {
                    count++;

                    String stationName =
                            result.getString("station_name");

                    JPanel stationCard = createStationCard(
                            count, stationName, lineColor
                    );

                    // No gap between station rows:
                    // this keeps the connector line continuous.
                    stationListPanel.add(stationCard);
                }

                stationCountLabel.setText(count + " Stations");

                if (count == 0) {
                    stationListPanel.add(createMessageLabel(
                            "No stations are mapped to this line yet."
                    ));
                }
            }

            pageLayout.show(pageContainer, "STATIONS");

        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load stations:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        stationListPanel.revalidate();
        stationListPanel.repaint();
    }

    // ================= STATION CARD =================

    private JPanel createStationCard(
            int serialNumber,
            String stationName,
            Color lineColor) {

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        row.setPreferredSize(new Dimension(650, 90));
        row.setMinimumSize(new Dimension(300, 90));

        StationNumberColumn numberColumn =
                new StationNumberColumn(serialNumber, lineColor);

        RoundedPanel stationCard = new RoundedPanel(
                blendWithWhite(lineColor, 0.88f),
                blendWithWhite(lineColor, 0.72f)
        );
        stationCard.setLayout(new BorderLayout());
        stationCard.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel stationLabel = new JLabel(stationName);
        stationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        stationLabel.setForeground(textColor);

        stationCard.add(stationLabel, BorderLayout.CENTER);

        row.add(numberColumn, BorderLayout.WEST);
        row.add(stationCard, BorderLayout.CENTER);

        return row;
    }

    // ================= CIRCLES + CONTINUOUS CONNECTOR =================

    private static class StationNumberColumn extends JPanel {

        private final int serialNumber;
        private final Color lineColor;

        StationNumberColumn(int serialNumber, Color lineColor) {
            this.serialNumber = serialNumber;
            this.lineColor = lineColor;

            setOpaque(false);
            setPreferredSize(new Dimension(44, 90));
            setMinimumSize(new Dimension(44, 90));
            setMaximumSize(new Dimension(44, 90));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int centerX = getWidth() / 2;
            int diameter = 40;
            int centerY = getHeight() / 2;

            // Vertical line runs through the center of every circle.
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(4));
            g2.drawLine(centerX, 0, centerX, getHeight());

            // Circular serial number.
            g2.setColor(lineColor);
            g2.fillOval(
                    centerX - diameter / 2,
                    centerY - diameter / 2,
                    diameter,
                    diameter
            );

            // Number is centered in the circle.
            String text = String.valueOf(serialNumber);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 16));

            FontMetrics fm = g2.getFontMetrics();

            g2.drawString(
                    text,
                    centerX - fm.stringWidth(text) / 2,
                    centerY + (fm.getAscent() - fm.getDescent()) / 2
            );

            g2.dispose();
        }
    }

    // ================= LINE COLOURS =================

    private Color getLineColor(String lineName) {
        String name = lineName.toLowerCase();

        if (name.contains("yellow")) {
            return new Color(231, 199, 20);
        }
        if (name.contains("blue")) {
            return new Color(57, 133, 189);
        }
        if (name.contains("green")) {
            return new Color(74, 174, 99);
        }
        if (name.contains("violet")) {
            return new Color(139, 69, 197);
        }
        if (name.contains("pink")) {
            return new Color(217, 132, 181);
        }
        if (name.contains("magenta")) {
            return new Color(206, 50, 199);
        }
        if (name.contains("grey") || name.contains("gray")) {
            return new Color(133, 133, 133);
        }
        if (name.contains("airport") || name.contains("orange")) {
            return new Color(217, 133, 54);
        }
        if (name.contains("rapid")) {
            return new Color(57, 134, 156);
        }
        if (name.contains("red")) {
            return new Color(217, 67, 67);
        }

        return darkBlue;
    }

    // ================= UI HELPERS =================

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(background);
        scrollPane.getViewport().setBackground(background);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
    }

    private JLabel createMessageLabel(String message) {
        JLabel label = new JLabel(message);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(new Color(105, 115, 130));
        label.setBorder(new EmptyBorder(18, 12, 18, 12));
        return label;
    }

    private Color blendWithWhite(Color color, float whiteAmount) {
        float amount = Math.max(0f, Math.min(1f, whiteAmount));

        int red = Math.round(
                color.getRed() * (1 - amount) + 255 * amount
        );
        int green = Math.round(
                color.getGreen() * (1 - amount) + 255 * amount
        );
        int blue = Math.round(
                color.getBlue() * (1 - amount) + 255 * amount
        );

        return new Color(red, green, blue);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
    }

    // ================= ROUNDED PANEL =================

    private static class RoundedPanel extends JPanel {

        private Color fillColor;
        private final Color outlineColor;

        RoundedPanel(Color fillColor, Color outlineColor) {
            this.fillColor = fillColor;
            this.outlineColor = outlineColor;
            setOpaque(false);
        }

        void setBackgroundColor(Color color) {
            this.fillColor = color;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth() - 2;
            int height = getHeight() - 2;

            g2.setColor(fillColor);
            g2.fillRoundRect(1, 1, width, height, 26, 26);

            g2.setColor(outlineColor);
            g2.drawRoundRect(1, 1, width, height, 26, 26);

            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}