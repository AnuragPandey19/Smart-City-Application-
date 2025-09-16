package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class Transportation extends JFrame {
    private final JComboBox<String> pickupField;
    private final JComboBox<String> destinationField;
    private final JPanel transportPanel;

    public Transportation() {
        setTitle("Find Your Ride");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = new JLabel("Find Your Transportation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        inputPanel.add(new JLabel("🚏 Pickup Location:"), gbc);
        gbc.gridx = 1;
        pickupField = new JComboBox<>(new String[]{"Select", "Central Station", "Airport", "Metro Station", "Railway Station", "Uptown", "Harbor", "Bike Station", "Scooter Stand", "Hotel", "Downtown"});
        pickupField.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(pickupField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("🎯 Destination:"), gbc);
        gbc.gridx = 1;
        destinationField = new JComboBox<>(new String[]{"Select", "Downtown", "City Center", "TechVille", "Springfield", "Old Town", "Coastal Town", "Airport", "Any Destination"});
        destinationField.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(destinationField, gbc);

        // Find Ride Button
        JButton findRideBtn = new JButton("Find Ride");
        findRideBtn.setFont(new Font("Arial", Font.BOLD, 16));
        findRideBtn.setBackground(new Color(30, 144, 255)); // Blue color
        findRideBtn.setForeground(Color.WHITE);
        findRideBtn.setFocusPainted(false);
        findRideBtn.setPreferredSize(new Dimension(200, 40));
        findRideBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(findRideBtn, gbc);

        add(inputPanel, BorderLayout.CENTER);

        // Transport Panel
        transportPanel = new JPanel();
        transportPanel.setLayout(new GridLayout(0, 1, 10, 10));
        transportPanel.setBorder(BorderFactory.createTitledBorder("🚖 Available Transport Options"));
        JScrollPane scrollPane = new JScrollPane(transportPanel);
        scrollPane.setPreferredSize(new Dimension(580, 200));
        add(scrollPane, BorderLayout.SOUTH);

        // Button Action
        findRideBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pickup = (String) pickupField.getSelectedItem();
                String destination = (String) destinationField.getSelectedItem();
                assert pickup != null;
                if (pickup.equals("Select") || Objects.equals(destination, "Select")) {
                    JOptionPane.showMessageDialog(null, "Please select both pickup and destination.");
                } else {
                    fetchTransportOptions(pickup, destination);
                }
            }
        });

        setVisible(true);
    }

    private void fetchTransportOptions(String pickup, String destination) {
        transportPanel.removeAll();
        try {
            Connection conn = DBConnection.getConnection();
            String query = "SELECT * FROM transportation WHERE pickup_point = ? AND destination = ?";
            assert conn != null;
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, pickup);
            pstmt.setString(2, destination);
            ResultSet rs = pstmt.executeQuery();

            boolean dataFound = false;

            while (rs.next()) {
                dataFound = true;
                String transportType = rs.getString("transport_type");
                double fare = rs.getDouble("fare");
                String bookingMethod = rs.getString("booking_method");

                // Transport Option Card
                JPanel transportCard = new JPanel(new BorderLayout());
                transportCard.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                transportCard.setBackground(new Color(245, 245, 245));
                transportCard.setPreferredSize(new Dimension(550, 50));

                JLabel transportLabel = new JLabel("🚖 " + transportType, SwingConstants.LEFT);
                transportLabel.setFont(new Font("Arial", Font.BOLD, 14));

                JLabel fareLabel = new JLabel("💰 Fare: $" + fare, SwingConstants.CENTER);
                JLabel bookingLabel = new JLabel("📱 Booking: " + bookingMethod, SwingConstants.RIGHT);

                transportCard.add(transportLabel, BorderLayout.WEST);
                transportCard.add(fareLabel, BorderLayout.CENTER);
                transportCard.add(bookingLabel, BorderLayout.EAST);

                transportPanel.add(transportCard);
            }

            if (!dataFound) {
                JOptionPane.showMessageDialog(null, "No transport options available for the selected route.");
            }

            transportPanel.revalidate();
            transportPanel.repaint();

            conn.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error fetching data: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new Transportation();
    }
}
