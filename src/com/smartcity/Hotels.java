package com.smartcity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Hotels extends JFrame {
    private final JTextField searchField;
    private final DefaultTableModel model;

    public Hotels() {
        setTitle("Hotel Search");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for Search
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Enter Location: "));
        searchField = new JTextField(15);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        // Table Model Setup
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"Hotel Name", "Location", "Contact", "Price Range", "Rating"});
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Layout
        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Action Listener for Search Button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchHotels();
            }
        });
    }

    private void searchHotels() {
        String location = searchField.getText().trim();

        if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a location!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT hotel_name, location, contact, price_range, rating FROM hotels WHERE location LIKE ?";
            assert con != null;
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + location + "%");

            ResultSet rs = pst.executeQuery();
            model.setRowCount(0); // Clear previous results

            boolean found = false;
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("hotel_name"),
                        rs.getString("location"),
                        rs.getString("contact"),
                        rs.getString("price_range"),
                        rs.getFloat("rating")
                });
                found = true;
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No hotels found in this location.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();
            pst.close();
            con.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Hotels().setVisible(true));
    }
}
