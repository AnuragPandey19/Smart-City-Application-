package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Picks a pickup + destination, then renders the available transport
 * options as a list of cards.
 */
public class TransportationPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(TransportationPanel.class.getName());

    private static final String SELECT = "Select…";
    private static final String[] PICKUPS = {
        SELECT, "Central Station", "Airport", "Metro Station", "Railway Station",
        "Uptown", "Harbor", "Bike Station", "Scooter Stand", "Hotel", "Downtown"
    };
    private static final String[] DESTINATIONS = {
        SELECT, "Downtown", "City Center", "TechVille", "Springfield",
        "Old Town", "Coastal Town", "Airport", "Any Destination"
    };

    private final JComboBox<String> pickupBox = new JComboBox<>(PICKUPS);
    private final JComboBox<String> destBox   = new JComboBox<>(DESTINATIONS);
    private final JPanel resultsPanel = new JPanel();
    private final JLabel status = UI.muted("Select a pickup and destination to see available options.");

    public TransportationPanel() {
        setBackground(UI.BG);
        setLayout(new BorderLayout(0, UI.PAD_M));
        setBorder(UI.pad(UI.PAD_L, UI.PAD_L, UI.PAD_L, UI.PAD_L));

        /* Header */
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UI.h2("Transportation"), BorderLayout.NORTH);
        header.add(UI.muted("Find rides between locations — fares and how to book."), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        /* Form + results */
        JPanel center = new JPanel(new BorderLayout(0, UI.PAD_M));
        center.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(UI.pad(UI.PAD_M, 0, UI.PAD_M, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(formLabel("Pickup:"), gbc);
        gbc.gridx = 1;
        styleCombo(pickupBox);
        form.add(pickupBox, gbc);

        gbc.gridx = 2;
        form.add(formLabel("Destination:"), gbc);
        gbc.gridx = 3;
        styleCombo(destBox);
        form.add(destBox, gbc);

        gbc.gridx = 4;
        JButton find = UI.primaryButton("Find Rides");
        find.addActionListener(e -> findOptions());
        form.add(find, gbc);

        center.add(form, BorderLayout.NORTH);

        resultsPanel.setOpaque(false);
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setBorder(BorderFactory.createLineBorder(UI.BORDER, 1));
        scroll.getViewport().setBackground(UI.BG);
        center.add(scroll, BorderLayout.CENTER);

        center.add(status, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
    }

    private void styleCombo(JComboBox<String> box) {
        box.setBackground(UI.SURFACE);
        box.setForeground(UI.TEXT);
        box.setFont(UI.BODY);
        box.setPreferredSize(new Dimension(180, 34));
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(UI.TEXT);
        l.setFont(UI.BODY_BOLD);
        return l;
    }

    private void findOptions() {
        String pickup = String.valueOf(pickupBox.getSelectedItem());
        String dest   = String.valueOf(destBox.getSelectedItem());

        if (SELECT.equals(pickup) || SELECT.equals(dest)) {
            status.setForeground(UI.DANGER);
            status.setText("Pick both a pickup and a destination.");
            return;
        }
        if (pickup.equals(dest)) {
            status.setForeground(UI.DANGER);
            status.setText("Pickup and destination can't be the same.");
            return;
        }

        resultsPanel.removeAll();
        status.setForeground(UI.TEXT_MUTED);
        status.setText("Searching…");

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                status.setForeground(UI.DANGER);
                status.setText("Couldn't reach the database.");
                return;
            }
            try (PreparedStatement pst = con.prepareStatement(
                    "SELECT transport_type, fare, booking_method FROM transportation "
                  + "WHERE pickup_point = ? AND destination = ?")) {
                pst.setString(1, pickup);
                pst.setString(2, dest);
                try (ResultSet rs = pst.executeQuery()) {
                    int n = 0;
                    while (rs.next()) {
                        resultsPanel.add(buildOptionCard(
                                rs.getString("transport_type"),
                                rs.getDouble("fare"),
                                rs.getString("booking_method")));
                        resultsPanel.add(Box.createVerticalStrut(UI.PAD_S));
                        n++;
                    }
                    if (n == 0) {
                        status.setText("No transport options for that route.");
                    } else {
                        status.setText(n + (n == 1 ? " option" : " options") + " available.");
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Transport lookup failed", ex);
            status.setForeground(UI.DANGER);
            status.setText("Something went wrong while loading options.");
        }
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JComponent buildOptionCard(String type, double fare, String booking) {
        JPanel card = UI.card();
        card.setLayout(new BorderLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel name = UI.h3(type == null ? "—" : type);
        JPanel right = new JPanel(new GridLayout(2, 1));
        right.setOpaque(false);
        JLabel fareLabel = UI.body(String.format("Fare: $%.2f", fare));
        fareLabel.setFont(UI.BODY_BOLD);
        right.add(fareLabel);
        right.add(UI.muted("Book via: " + (booking == null ? "—" : booking)));

        card.add(name,  BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);
        return card;
    }
}
