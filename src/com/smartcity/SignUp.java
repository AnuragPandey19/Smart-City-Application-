package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignUp extends JFrame {
    public SignUp() {
        setTitle("Sign Up - Smart City");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE); // Modern Background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 🎉 Header
        JLabel header = new JLabel("Are You New Here? Sign Up & Explore! 🚀");
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(header, gbc);

        // 📌 Name
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("👤 Full Name:"), gbc);
        JTextField nameField = new JTextField(15);
        gbc.gridx = 1;
        add(nameField, gbc);

        // 📌 Username
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("📛 Username:"), gbc);
        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        add(userField, gbc);

        // 📌 Password + Show/Hide
        gbc.gridy = 3;
        gbc.gridx = 0;
        add(new JLabel("🔒 Password:"), gbc);
        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passField, gbc);

        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.setBackground(Color.WHITE);
        gbc.gridy = 4;
        gbc.gridx = 1;
        add(showPass, gbc);

        // 📌 DOB
        gbc.gridy = 5;
        gbc.gridx = 0;
        add(new JLabel("📅 Date of Birth:"), gbc);
        JTextField dobField = new JTextField(10);
        gbc.gridx = 1;
        add(dobField, gbc);

        // 📌 Country
        gbc.gridy = 6;
        gbc.gridx = 0;
        add(new JLabel("🌍 Country:"), gbc);
        JTextField countryField = new JTextField(10);
        gbc.gridx = 1;
        add(countryField, gbc);

        // 📌 State
        gbc.gridy = 7;
        gbc.gridx = 0;
        add(new JLabel("🏙️ State:"), gbc);
        JTextField stateField = new JTextField(10);
        gbc.gridx = 1;
        add(stateField, gbc);

        // 📌 Pincode
        gbc.gridy = 8;
        gbc.gridx = 0;
        add(new JLabel("📌 Pincode:"), gbc);
        JTextField pinField = new JTextField(8);
        gbc.gridx = 1;
        add(pinField, gbc);

        // 📌 Mobile Number
        gbc.gridy = 9;
        gbc.gridx = 0;
        add(new JLabel("📱 Mobile:"), gbc);
        JTextField mobileField = new JTextField(10);
        gbc.gridx = 1;
        add(mobileField, gbc);

        // ✨ Register Button
        JButton signupButton = new JButton("🚀 Register Now");
        signupButton.setBackground(new Color(0, 204, 102)); // Green color
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(signupButton, gbc);

        // 👀 Show Password Toggle
        showPass.addActionListener(e -> {
            if (showPass.isSelected()) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('•');
            }
        });

        // 🚀 Sign Up Action
        signupButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String dob = dobField.getText().trim();
            String country = countryField.getText().trim();
            String state = stateField.getText().trim();
            String pincode = pinField.getText().trim();
            String mobile = mobileField.getText().trim();

            // Validate input fields
            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || dob.isEmpty() ||
                    country.isEmpty() || state.isEmpty() || pincode.isEmpty() || mobile.isEmpty()) {
                JOptionPane.showMessageDialog(null, "⚠️ All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!mobile.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(null, "📵 Invalid Mobile Number! Must be 10 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (registerUser(name, username, password, dob, country, state, pincode, mobile)) {
                JOptionPane.showMessageDialog(null, "🎉 Sign Up Successful! Welcome!");
                dispose(); // Close SignUp Window
            } else {
                JOptionPane.showMessageDialog(null, "❌ Error in registration!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    // 🔗 Database Registration
    private boolean registerUser(String name, String username, String password, String dob, String country, String state, String pincode, String mobile) {
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                Logger.getLogger(SignUp.class.getName()).log(Level.SEVERE, "Database connection failed!");
                return false;
            }
            try (PreparedStatement pst = con.prepareStatement("INSERT INTO users (name, username, password, dob, country, state, pincode, mobile) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                pst.setString(1, name);
                pst.setString(2, username);
                pst.setString(3, password);
                pst.setString(4, dob);
                pst.setString(5, country);
                pst.setString(6, state);
                pst.setString(7, pincode);
                pst.setString(8, mobile);
                return pst.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SignUp.class.getName()).log(Level.SEVERE, "Database error", ex);
            return false;
        }
    }
}
