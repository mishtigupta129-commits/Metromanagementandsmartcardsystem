
package com.metromanagement.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimePickerTest extends JFrame {

    private final Color cyan = new Color(0, 181, 225);
    private final Color navy = new Color(20, 43, 72);
    private final Color background = new Color(244, 247, 251);
    private final Color outline = new Color(216, 222, 230);

    private JTextField dateField;
    private JTextField timeField;

    public DateTimePickerTest() {
        setTitle("Lost & Found - Date and Time Picker Test");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel();
        root.setBackground(background);
        root.setBorder(new EmptyBorder(30, 40, 30, 40));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Report Lost / Found Item");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 25));
        heading.setForeground(navy);

        JLabel subtitle = new JLabel("Test the date and time selection");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        dateField = new JTextField(
            LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        timeField = new JTextField(
            LocalTime.now().format(
                DateTimeFormatter.ofPattern("hh:mm a"))
        );

        styleField(dateField);
        styleField(timeField);

        JButton dateButton = new JButton("Select Date  ▾");
        JButton timeButton = new JButton("Select Time  ▾");
        JButton submitButton = new JButton("SUBMIT");

        styleButton(dateButton);
        styleButton(timeButton);
        styleButton(submitButton);

        dateButton.addActionListener(e -> showDatePicker());
        timeButton.addActionListener(e -> showTimePicker());

        submitButton.addActionListener(e ->
            JOptionPane.showMessageDialog(
                this,
                "Selected Date: " + dateField.getText()
                    + "\nSelected Time: " + timeField.getText(),
                "Selection Confirmed",
                JOptionPane.INFORMATION_MESSAGE
            )
        );

        root.add(heading);
        root.add(Box.createVerticalStrut(8));
        root.add(subtitle);
        root.add(Box.createVerticalStrut(30));

        root.add(new JLabel("Date"));
        root.add(Box.createVerticalStrut(5));
        root.add(dateField);
        root.add(Box.createVerticalStrut(8));
        root.add(dateButton);

        root.add(Box.createVerticalStrut(22));

        root.add(new JLabel("Time"));
        root.add(Box.createVerticalStrut(5));
        root.add(timeField);
        root.add(Box.createVerticalStrut(8));
        root.add(timeButton);

        root.add(Box.createVerticalStrut(25));
        root.add(submitButton);

        setContentPane(root);
        setVisible(true);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(navy);
        field.setBackground(Color.WHITE);
        field.setEditable(false);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(outline, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        field.setPreferredSize(new Dimension(500, 46));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(cyan);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(
            Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setPreferredSize(new Dimension(500, 44));
    }

    private void showDatePicker() {
        LocalDate initial;
        try {
            initial = LocalDate.parse(
                dateField.getText(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            );
        } catch (Exception ex) {
            initial = LocalDate.now();
        }

        JSpinner day = new JSpinner(
            new SpinnerNumberModel(initial.getDayOfMonth(), 1, 31, 1));
        JSpinner month = new JSpinner(
            new SpinnerNumberModel(initial.getMonthValue(), 1, 12, 1));
        JSpinner year = new JSpinner(
            new SpinnerNumberModel(initial.getYear(), 2000, 2100, 1));

        day.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        month.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        year.setFont(new Font("Segoe UI", Font.PLAIN, 17));

        JPanel picker = new JPanel(new GridLayout(2, 3, 12, 8));
        picker.setBorder(new EmptyBorder(18, 18, 18, 18));
        picker.add(new JLabel("Day"));
        picker.add(new JLabel("Month"));
        picker.add(new JLabel("Year"));
        picker.add(day);
        picker.add(month);
        picker.add(year);

        int result = JOptionPane.showConfirmDialog(
            this, picker, "Select Date",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                LocalDate selected = LocalDate.of(
                    (Integer) year.getValue(),
                    (Integer) month.getValue(),
                    (Integer) day.getValue()
                );

                dateField.setText(selected.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Please select a valid date.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    private void showTimePicker() {
        LocalTime initial;
        try {
            initial = LocalTime.parse(
                timeField.getText(),
                DateTimeFormatter.ofPattern("hh:mm a")
            );
        } catch (Exception ex) {
            initial = LocalTime.now();
        }

        JSpinner hour = new JSpinner(
            new SpinnerNumberModel(
                initial.getHour() % 12 == 0 ? 12
                    : initial.getHour() % 12,
                1, 12, 1
            )
        );

        JSpinner minute = new JSpinner(
            new SpinnerNumberModel(initial.getMinute(), 0, 59, 1)
        );

        String[] periods = {"am", "pm"};
        JComboBox<String> period = new JComboBox<>(periods);
        period.setSelectedItem(initial.getHour() < 12 ? "am" : "pm");

        hour.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        minute.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        period.setFont(new Font("Segoe UI", Font.PLAIN, 17));

        JPanel picker = new JPanel(new GridLayout(2, 3, 12, 8));
        picker.setBorder(new EmptyBorder(18, 18, 18, 18));
        picker.add(new JLabel("Hour"));
        picker.add(new JLabel("Minute"));
        picker.add(new JLabel("AM / PM"));
        picker.add(hour);
        picker.add(minute);
        picker.add(period);

        int result = JOptionPane.showConfirmDialog(
            this, picker, "Select Time",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            int h = (Integer) hour.getValue();
            int m = (Integer) minute.getValue();

            if ("pm".equals(period.getSelectedItem()) && h != 12) {
                h += 12;
            } else if ("am".equals(period.getSelectedItem()) && h == 12) {
                h = 0;
            }

            LocalTime selected = LocalTime.of(h, m);

            timeField.setText(selected.format(
                DateTimeFormatter.ofPattern("hh:mm a")));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DateTimePickerTest::new);
    }
}
