package com.metromanagement.gui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard {

    public static void showDashboard() {

        JFrame frame = new JFrame("Admin Dashboard");

        frame.setSize(600, 450);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel title = new JLabel(
            "ADMIN DASHBOARD",
            SwingConstants.CENTER
        );

        title.setFont(
            new Font("Arial", Font.BOLD, 22)
        );

        JPanel buttons = new JPanel(
            new GridLayout(3, 2, 15, 15)
        );

        buttons.setBorder(
            BorderFactory.createEmptyBorder(
                30, 80, 30, 80
            )
        );

        JButton passengers = new JButton("Manage Passengers");
        passengers.addActionListener(e -> {
            PassengerManagement.showManagement();
        });
        JButton cards = new JButton("Manage Smart Cards");
        cards.addActionListener(e -> {
            SmartCardFrame.showManagement();
        });
        JButton stations = new JButton("Manage Stations");
        stations.addActionListener(e -> {
            StationFrame.showManagement();
        });
        JButton transactions = new JButton("View Transactions");
        JButton reports = new JButton("View Reports");
        JButton logout = new JButton("Logout");

        buttons.add(passengers);
        buttons.add(cards);
        buttons.add(stations);
        buttons.add(transactions);
        buttons.add(reports);
        buttons.add(logout);

        frame.add(title, BorderLayout.NORTH);
        frame.add(buttons, BorderLayout.CENTER);

        logout.addActionListener(e -> {
            frame.dispose();
        });

        frame.setVisible(true);
    }
}