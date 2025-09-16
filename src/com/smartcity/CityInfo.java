package com.smartcity;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CityInfo extends JFrame {
    private final JTextField searchField;
    private final JLabel cityNameLabel;
    private final JTextPane infoPane;

    public CityInfo() {
        setTitle("City Information");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel (Title)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 144, 255)); // Dodger Blue
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        cityNameLabel = new JLabel("CITY INFORMATION", SwingConstants.CENTER);
        cityNameLabel.setFont(new Font("Arial", Font.BOLD, 26));
        cityNameLabel.setForeground(Color.WHITE);
        headerPanel.add(cityNameLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Info Pane (City Details)
        infoPane = new JTextPane();
        infoPane.setContentType("text/html");
        infoPane.setEditable(false);
        infoPane.setFont(new Font("Arial", Font.PLAIN, 16));
        infoPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        JScrollPane scrollPane = new JScrollPane(infoPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245)); // Light Gray Background

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 2), // Cornflower Blue Border
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        searchPanel.add(new JLabel("Enter City: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        mainPanel.add(searchPanel, BorderLayout.SOUTH);

        // Search Action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchCity();
            }
        });
    }

    private void searchCity() {
        String cityName = searchField.getText().trim();
        if (cityName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a city name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT description, population, emergency_contacts FROM city_information WHERE name = ?")) {

                stmt.setString(1, cityName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String description = rs.getString("description");
                    int population = rs.getInt("population");
                    String emergencyContacts = rs.getString("emergency_contacts");

                    // Update Title & Text
                    cityNameLabel.setText(cityName.toUpperCase());
                    String displayText = "<html><div style='text-align:left;'>"
                            + "<h2 style='color:#1E90FF;'>Population: " + population + "</h2>"
                            + "<p style='font-size:16px;'>" + description + "</p>"
                            + "<div style='background-color:#FFD700; padding:10px; border-radius:5px;'>"
                            + "<b>Emergency Contacts:</b> " + emergencyContacts + "</div>"
                            + "</div></html>";

                    infoPane.setText(displayText);
                } else {
                    infoPane.setText("<html><h3 style='color:red;'>No information found for: " + cityName + "</h3></html>");
                }

            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CityInfo().setVisible(true));
    }
}
