
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
import java.util.ArrayList;

public class StationFrame {

    private static final Color DARK_BLUE = new Color(20, 48, 90);
    private static final Color BLUE = new Color(42, 105, 180);
    private static final Color LIGHT_BG = new Color(245, 248, 252);
    private static final Color TEXT = new Color(45, 55, 72);

    private static JFrame frame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;

    public static void showManagement() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Metro Station Details");
            frame.setSize(1000, 700);
            frame.setMinimumSize(new Dimension(800, 550));
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);
            mainPanel.setBackground(LIGHT_BG);

            mainPanel.add(createLinePage(), "LINES");

            frame.setContentPane(mainPanel);
            frame.setVisible(true);
            cardLayout.show(mainPanel, "LINES");
        });
    }

    // PAGE 1: Show all metro lines
    private static JPanel createLinePage() {
        JPanel page = new JPanel(new BorderLayout(0, 15));
        page.setBackground(LIGHT_BG);
        page.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel heading = new JLabel("Metro Lines");
        heading.setFont(new Font("Arial", Font.BOLD, 27));
        heading.setForeground(DARK_BLUE);

        JLabel subtitle = new JLabel("Click anywhere on a line card to view its stations.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.add(heading);
        header.add(Box.createVerticalStrut(7));
        header.add(subtitle);

        page.add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 18, 18));
        grid.setBackground(LIGHT_BG);
        grid.setBorder(new EmptyBorder(10, 2, 10, 2));

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LIGHT_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        page.add(scrollPane, BorderLayout.CENTER);

        loadLines(grid);

        return page;
    }

    private static void loadLines(JPanel grid) {
        grid.removeAll();

        String sql = "SELECT line_id, line_name, start_station, end_station "
                   + "FROM metro_lines ORDER BY line_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int index = 0;

            while (rs.next()) {
                int lineId = rs.getInt("line_id");
                String lineName = rs.getString("line_name");
                String start = rs.getString("start_station");
                String end = rs.getString("end_station");

                Color[] colors = {
                    new Color(42, 105, 180),
                    new Color(0, 137, 123),
                    new Color(120, 75, 170),
                    new Color(210, 115, 35),
                    new Color(190, 65, 80),
                    new Color(50, 130, 150)
                };

                Color lineColor = colors[index % colors.length];
                index++;

                JButton lineCard = createLineCard(
                    lineId, lineName, start, end, lineColor
                );

                grid.add(lineCard);
            }

            if (index == 0) {
                grid.setLayout(new BorderLayout());
                JLabel empty = new JLabel(
                    "No metro lines found in the database.",
                    SwingConstants.CENTER
                );
                empty.setFont(new Font("Arial", Font.BOLD, 16));
                grid.add(empty, BorderLayout.CENTER);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                frame,
                "Unable to load metro lines.\nCheck DBConnection and database tables.\n\n"
                    + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        grid.revalidate();
        grid.repaint();
    }

    // WHOLE CARD IS CLICKABLE
    private static JButton createLineCard(
            int lineId,
            String lineName,
            String start,
            String end,
            Color lineColor) {

        String html =
            "<html><div style='text-align:left; padding:8px;'>"
            + "<font color='#" + toHex(lineColor) + "' size='5'><b>"
            + escapeHtml(lineName)
            + "</b></font><br><br>"
            + "<font color='#2D3748' size='4'>"
            + "FROM: " + escapeHtml(start)
            + "<br><br>TO: " + escapeHtml(end)
            + "</font><br><br>"
            + "<font color='#" + toHex(lineColor)
            + "' size='4'><b>VIEW STATIONS  &#8594;</b></font>"
            + "</div></html>";

        JButton card = new JButton(html);
        card.setHorizontalAlignment(SwingConstants.LEFT);
        card.setVerticalAlignment(SwingConstants.CENTER);
        card.setBackground(Color.WHITE);
        card.setOpaque(true);
        card.setFocusPainted(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 6, 0, 0, lineColor),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 235)),
                new EmptyBorder(18, 18, 18, 18)
            )
        ));
        card.setPreferredSize(new Dimension(390, 175));

        // Clicking ANYWHERE on this button opens the stations page.
        card.addActionListener(e -> showStations(
            lineId, lineName, lineColor
        ));

        return card;
    }

    // PAGE 2: Show stations for selected line
    private static void showStations(
            int lineId, String lineName, Color lineColor) {

        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setBackground(LIGHT_BG);
        page.setBorder(new EmptyBorder(25, 30, 25, 30));

        JButton backButton = new JButton("\u2190  Back to Lines");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(DARK_BLUE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "LINES"));

        JLabel heading = new JLabel(lineName + " - Stations");
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(DARK_BLUE);

        JPanel header = new JPanel(new BorderLayout(0, 15));
        header.setOpaque(false);
        header.add(backButton, BorderLayout.NORTH);
        header.add(heading, BorderLayout.SOUTH);
        page.add(header, BorderLayout.NORTH);

        JPanel stationList = new JPanel();
        stationList.setLayout(new BoxLayout(stationList, BoxLayout.Y_AXIS));
        stationList.setBackground(LIGHT_BG);
        stationList.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(stationList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LIGHT_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        page.add(scrollPane, BorderLayout.CENTER);

        String sql =
            "SELECT s.station_name, ls.station_order "
            + "FROM metro_line_stations ls "
            + "JOIN metro_stations s ON s.station_id = ls.station_id "
            + "WHERE ls.line_id = ? "
            + "ORDER BY ls.station_order ASC, s.station_name ASC";

        int count = 0;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, lineId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    count++;

                    String stationName = rs.getString("station_name");
                    int order = rs.getInt("station_order");

                    JPanel stationCard = new JPanel(new BorderLayout(15, 0));
                    stationCard.setBackground(Color.WHITE);
                    stationCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                            0, 4, 1, 0, lineColor
                        ),
                        new EmptyBorder(14, 18, 14, 18)
                    ));
                    stationCard.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, 75)
                    );
                    stationCard.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JLabel number = new JLabel(String.format("%02d", count));
                    number.setFont(new Font("Arial", Font.BOLD, 18));
                    number.setForeground(lineColor);
                    number.setPreferredSize(new Dimension(45, 35));

                    JLabel name = new JLabel(stationName);
                    name.setFont(new Font("Arial", Font.BOLD, 16));
                    name.setForeground(TEXT);

                    JLabel orderLabel = new JLabel("Stop " + order);
                    orderLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                    orderLabel.setForeground(Color.GRAY);

                    JPanel details = new JPanel(new BorderLayout(0, 5));
                    details.setOpaque(false);
                    details.add(name, BorderLayout.CENTER);
                    details.add(orderLabel, BorderLayout.SOUTH);

                    stationCard.add(number, BorderLayout.WEST);
                    stationCard.add(details, BorderLayout.CENTER);

                    stationList.add(stationCard);
                    stationList.add(Box.createVerticalStrut(8));
                }
            }

            if (count == 0) {
                JLabel empty = new JLabel(
                    "No stations mapped to this line in the database.",
                    SwingConstants.CENTER
                );
                empty.setFont(new Font("Arial", Font.BOLD, 16));
                empty.setForeground(Color.GRAY);
                stationList.add(empty);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                frame,
                "Unable to load stations.\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

        JLabel countLabel = new JLabel("Total stations: " + count);
        countLabel.setFont(new Font("Arial", Font.BOLD, 14));
        countLabel.setForeground(DARK_BLUE);
        page.add(countLabel, BorderLayout.SOUTH);

        mainPanel.add(page, "STATIONS");
        cardLayout.show(mainPanel, "STATIONS");
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }

    private static String toHex(Color color) {
        return String.format("%02X%02X%02X",
            color.getRed(), color.getGreen(), color.getBlue());
    }
}
