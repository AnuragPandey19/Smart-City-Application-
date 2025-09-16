package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Welcome extends JFrame {
    private JPanel welcomePanel;
    private JPanel loginPanel;
    private final MainFrame mainFrame;

    public Welcome(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setTitle("Smart City");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new CardLayout());

        // Create Welcome and Login Panels
        createWelcomePanel();
        createLoginPanel();

        // Initially show the Welcome Panel
        setContentPane(welcomePanel);
        setVisible(true);
    }

    private void createWelcomePanel() {
        welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(220, 235, 255)); // Soft light blue

        // **Title: "Welcome to Smart City"**
        JLabel headline = new JLabel("Welcome to Smart City", SwingConstants.CENTER);
        headline.setFont(new Font("Arial", Font.BOLD, 26));
        headline.setForeground(new Color(0, 76, 153)); // Dark blue for elegance

        // **Subheading**
        JLabel subheading = new JLabel("Your Guide to Explore and Navigate", SwingConstants.CENTER);
        subheading.setFont(new Font("Arial", Font.PLAIN, 16));
        subheading.setForeground(new Color(80, 80, 80)); // Dark gray for readability

        // **Button Panel for Centering**
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(220, 235, 255)); // Same as main panel
        JButton getStartedButton = new JButton("Get Started");
        getStartedButton.setFont(new Font("Arial", Font.BOLD, 16));
        getStartedButton.setBackground(new Color(0, 153, 76)); // Green
        getStartedButton.setForeground(Color.WHITE);
        getStartedButton.setFocusPainted(false);
        getStartedButton.setPreferredSize(new Dimension(150, 40)); // Proper size

        // **Hover Effect**
        getStartedButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                getStartedButton.setBackground(new Color(0, 204, 102)); // Lighter green
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                getStartedButton.setBackground(new Color(0, 153, 76));
            }
        });

        buttonPanel.add(getStartedButton);

        // **Adding Components**
        welcomePanel.add(subheading, BorderLayout.NORTH);
        welcomePanel.add(headline, BorderLayout.CENTER);
        welcomePanel.add(buttonPanel, BorderLayout.SOUTH);

        // **Button Click Action: Switch to Login**
        getStartedButton.addActionListener(e -> {
            setContentPane(loginPanel);
            revalidate();
            repaint();
        });
    }

    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE); // White background for contrast

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        // Title
        JLabel titleLabel = new JLabel("Login to Smart City");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        // Username Label
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Username:"), gbc);

        // Username Field
        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        loginPanel.add(userField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(new JLabel("Password:"), gbc);

        // Password Field
        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        loginPanel.add(passField, gbc);

        // Sign In Button
        JButton loginButton = new JButton("Sign In");
        loginButton.setBackground(new Color(0, 102, 204)); // Blue color
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        // Sign Up Button
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBackground(new Color(0, 204, 102)); // Green color
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        gbc.gridy = 4;
        loginPanel.add(signupButton, gbc);

        // Sign In Action
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (validateUser(username, password)) {
                JOptionPane.showMessageDialog(null, "Login Successful!");
                dispose(); // Close Welcome Window
                mainFrame.showMainUI(); // Open MainFrame
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect Username or Password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Open SignUp Window
        signupButton.addActionListener(e -> new SignUp());
    }



    private boolean validateUser(String username, String password) {
        try (Connection con = DBConnection.getConnection()) {
            assert con != null;
            try (PreparedStatement pst = con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
                pst.setString(1, username);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();
                return rs.next(); // Returns true if user exists
            }
        } catch (SQLException ex) {
            Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, "Database error", ex);
            return false;
        }
    }
}