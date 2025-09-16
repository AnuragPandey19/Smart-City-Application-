package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Tourism extends JFrame {
    public Tourism() {
        setTitle("Tourism - Smart City");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 102, 204));
        JLabel titleLabel = new JLabel("Explore Tourism");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel with Buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton btnHotels = createStyledButton("Hotels");
        JButton btnPlaces = createStyledButton("Places to Visit");
        JButton btnRestaurants = createStyledButton("Restaurants");

        centerPanel.add(btnHotels);
        centerPanel.add(btnPlaces);
        centerPanel.add(btnRestaurants);

        add(centerPanel, BorderLayout.CENTER);

        // Button Actions
        btnHotels.addActionListener(e -> new Hotels().setVisible(true));
        btnPlaces.addActionListener(e -> new TouristPlaces().setVisible(true));
        btnRestaurants.addActionListener(e -> new Restaurants().setVisible(true));
    }

    // Method to create styled buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 153, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover Effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 204));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 153, 255));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Tourism().setVisible(true));
    }
}
