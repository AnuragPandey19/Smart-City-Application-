package com.smartcity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ShoppingMalls extends JFrame {
    private final JTextField searchField;
    private final DefaultTableModel tableModel;

    public ShoppingMalls() {
        setTitle("Shopping Malls");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255)); // Light blue background

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(200, 220, 240));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setLayout(new FlowLayout());

        JLabel searchLabel = new JLabel("Search by Location:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchField = new JTextField(15);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(70, 130, 180)); // Steel Blue
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // Table Panel
        tableModel = new DefaultTableModel(new String[]{"Mall Name", "Location", "Opening Time", "Closing Time"}, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(173, 216, 230));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Search action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String location = searchField.getText().trim();
                searchMalls(location);
            }
        });

        setVisible(true);
    }

    private void searchMalls(String location) {
        tableModel.setRowCount(0); // Clear previous results

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT mall_name, location, opening_time, closing_time FROM shopping_mall WHERE location LIKE ?")) {

                stmt.setString(1, "%" + location + "%");
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    tableModel.addRow(new Object[]{rs.getString("mall_name"), rs.getString("location"), rs.getString("opening_time"), rs.getString("closing_time")});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new ShoppingMalls();
    }
}
