package com.metromanagement.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import com.metromanagement.collection.MetroData;
import com.metromanagement.model.Passenger;

public class PassengerManagement {

    public static void showManagement() {

        JFrame frame = new JFrame("Passenger Management");

        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // =========================
        // Input Fields
        // =========================

        JLabel idLabel = new JLabel("Passenger ID:");
        JTextField idField = new JTextField();

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel phoneLabel = new JLabel("Phone:");
        JTextField phoneField = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        // =========================
        // Buttons
        // =========================

        JButton addButton = new JButton("Add Passenger");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        // =========================
        // Table
        // =========================

        String[] columns = {
            "Passenger ID",
            "Name",
            "Phone",
            "Email"
        };

        DefaultTableModel model =
                new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);

        JScrollPane scrollPane =
                new JScrollPane(table);

        // =========================
        // Input Panel
        // =========================

        JPanel inputPanel =
                new JPanel(new GridLayout(4, 2, 10, 10));

        inputPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        inputPanel.add(idLabel);
        inputPanel.add(idField);

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);

        inputPanel.add(phoneLabel);
        inputPanel.add(phoneField);

        inputPanel.add(emailLabel);
        inputPanel.add(emailField);

        // =========================
        // Button Panel
        // =========================

        JPanel buttonPanel =
                new JPanel(new FlowLayout());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        // =========================
        // Add Passenger
        // =========================

        addButton.addActionListener(e -> {

            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (id.isEmpty()
                    || name.isEmpty()
                    || phone.isEmpty()
                    || email.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please fill all fields!",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            Passenger passenger =
                    new Passenger(id, name, phone, email);

            MetroData.passengers.add(passenger);

            model.addRow(new Object[]{
                    id,
                    name,
                    phone,
                    email
            });

            JOptionPane.showMessageDialog(
                    frame,
                    "Passenger added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields(
                    idField,
                    nameField,
                    phoneField,
                    emailField,
                    table
            );
        });

        // =========================
        // Select Table Row
        // =========================

        table.getSelectionModel()
                .addListSelectionListener(e -> {

            int row = table.getSelectedRow();

            if (row >= 0) {

                idField.setText(
                        model.getValueAt(row, 0).toString()
                );

                nameField.setText(
                        model.getValueAt(row, 1).toString()
                );

                phoneField.setText(
                        model.getValueAt(row, 2).toString()
                );

                emailField.setText(
                        model.getValueAt(row, 3).toString()
                );
            }
        });

        // =========================
        // Delete Passenger
        // =========================

        deleteButton.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select a passenger!",
                        "Selection Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            MetroData.passengers.remove(row);

            model.removeRow(row);

            JOptionPane.showMessageDialog(
                    frame,
                    "Passenger deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields(
                    idField,
                    nameField,
                    phoneField,
                    emailField,
                    table
            );
        });

        // =========================
        // Update Passenger
        // =========================

        updateButton.addActionListener(e -> {

            int row = table.getSelectedRow();

            if (row == -1) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please select a passenger!",
                        "Selection Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty()
                    || phone.isEmpty()
                    || email.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please fill all fields!",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            Passenger passenger =
                    MetroData.passengers.get(row);

            passenger.setName(name);
            passenger.setPhone(phone);
            passenger.setEmail(email);

            model.setValueAt(name, row, 1);
            model.setValueAt(phone, row, 2);
            model.setValueAt(email, row, 3);

            JOptionPane.showMessageDialog(
                    frame,
                    "Passenger updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // =========================
        // Clear Button
        // =========================

        clearButton.addActionListener(e -> {

            clearFields(
                    idField,
                    nameField,
                    phoneField,
                    emailField,
                    table
            );
        });

        // =========================
        // Frame Layout
        // =========================

        frame.setLayout(new BorderLayout());

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // =========================
    // Clear Fields Method
    // =========================

    private static void clearFields(
            JTextField idField,
            JTextField nameField,
            JTextField phoneField,
            JTextField emailField,
            JTable table) {

        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");

        table.clearSelection();
    }
}
