
package com.metromanagement.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MetroMapFrame extends JFrame {

    private BufferedImage mapImage;
    private JLabel imageLabel;
    private JScrollPane scrollPane;

    private double zoom = 1.0;
    private final double MIN_ZOOM = 0.25;
    private final double MAX_ZOOM = 5.0;
    private final double ZOOM_STEP = 0.25;

    private final Color navy = new Color(14, 43, 77);
    private final Color blue = new Color(22, 105, 230);

    public MetroMapFrame() {
        setTitle("Delhi Metro Network Map");
        setSize(1200, 800);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load the downloaded high-resolution image.
        try {
        	File imageFile = new File(
        		    "src/com/metromanagement/gui/delhi_metro_map.jpg"
        		);

            if (!imageFile.exists()) {
                throw new IOException(
                        "Image file not found: "
                        + imageFile.getAbsolutePath()
                );
            }

            mapImage = ImageIO.read(imageFile);

            if (mapImage == null) {
                throw new IOException(
                        "Image format is not supported."
                );
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Delhi Metro map load nahi ho paaya.\n\n"
                    + "Please check:\n"
                    + "1. metro-map folder project root mein hai.\n"
                    + "2. Image ka naam delhi_metro_map.png hai.\n\n"
                    + ex.getMessage(),
                    "Metro Map Error",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        // TOP TOOLBAR
        JPanel toolbar = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 10)
        );
        toolbar.setBackground(Color.WHITE);

        JButton zoomIn = new JButton("Zoom In +");
        JButton zoomOut = new JButton("Zoom Out -");
        JButton resetZoom = new JButton("Reset Zoom");

        styleButton(zoomIn);
        styleButton(zoomOut);
        styleButton(resetZoom);

        JLabel zoomLabel = new JLabel();
        zoomLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        zoomLabel.setForeground(navy);

        toolbar.add(zoomIn);
        toolbar.add(zoomOut);
        toolbar.add(resetZoom);
        toolbar.add(zoomLabel);

        // IMAGE VIEW
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );
        imageLabel.setVerticalAlignment(
                SwingConstants.CENTER
        );
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(imageLabel);
        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );
        scrollPane.getViewport().setBackground(Color.WHITE);

        // ZOOM BUTTON ACTIONS
        zoomIn.addActionListener(e -> {
            setZoom(zoom + ZOOM_STEP);
            zoomLabel.setText(
                    "Zoom: " + Math.round(zoom * 100) + "%"
            );
        });

        zoomOut.addActionListener(e -> {
            setZoom(zoom - ZOOM_STEP);
            zoomLabel.setText(
                    "Zoom: " + Math.round(zoom * 100) + "%"
            );
        });

        resetZoom.addActionListener(e -> {
            setZoom(1.0);
            zoomLabel.setText("Zoom: 100%");
        });

        // Mouse wheel zoom with Ctrl + wheel
        imageLabel.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                if (e.getWheelRotation() < 0) {
                    setZoom(zoom + ZOOM_STEP);
                } else {
                    setZoom(zoom - ZOOM_STEP);
                }

                zoomLabel.setText(
                        "Zoom: " + Math.round(zoom * 100) + "%"
                );
            }
        });

        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        updateImage();
        zoomLabel.setText("Zoom: 100%");

        setVisible(true);
    }

    private void setZoom(double newZoom) {
        zoom = Math.max(
                MIN_ZOOM,
                Math.min(MAX_ZOOM, newZoom)
        );

        updateImage();
    }

    private void updateImage() {
        if (mapImage == null || imageLabel == null) {
            return;
        }

        int width = Math.max(
                1,
                (int) (mapImage.getWidth() * zoom)
        );

        int height = Math.max(
                1,
                (int) (mapImage.getHeight() * zoom)
        );

        // Smoothly resize the original image.
        Image scaledImage = mapImage.getScaledInstance(
                width,
                height,
                Image.SCALE_SMOOTH
        );

        imageLabel.setIcon(new ImageIcon(scaledImage));
        imageLabel.setPreferredSize(
                new Dimension(width, height)
        );

        imageLabel.revalidate();
        imageLabel.repaint();
    }

    private void styleButton(JButton button) {
        button.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        button.setForeground(Color.WHITE);
        button.setBackground(blue);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );
    }

    public static void showManagement() {
        SwingUtilities.invokeLater(() -> {
            MetroMapFrame frame = new MetroMapFrame();
            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        showManagement();
    }
}
