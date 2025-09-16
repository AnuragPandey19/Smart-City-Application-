package com.smartcity;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JPanel {
    public HomePage(MainFrame mainFrame) {
        setLayout(new GridBagLayout());
        setBackground(new Color(230, 245, 255)); // Light background

        // **Card Panel for Centered Content**
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                Color color1 = new Color(180, 220, 255);
                Color color2 = new Color(120, 180, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, width, height, 30, 30); // Rounded Corners
            }
        };
        cardPanel.setPreferredSize(new Dimension(450, 300));
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // **Title**
        JLabel titleLabel = new JLabel("Smart City Guide", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(10, 10, 70));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        cardPanel.add(titleLabel, gbc);

        // **Buttons with Styling**
        JButton cityInfoButton = createStyledButton("City Information");
        JButton tourismButton = createStyledButton("Tourism");
        JButton transportButton = createStyledButton("Transportation");
        JButton shoppingButton = createStyledButton("Shopping Malls");

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        cardPanel.add(cityInfoButton, gbc);

        gbc.gridx = 1;
        cardPanel.add(tourismButton, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        cardPanel.add(transportButton, gbc);

        gbc.gridx = 1;
        cardPanel.add(shoppingButton, gbc);

        add(cardPanel);

        // **Button Actions**
        cityInfoButton.addActionListener(e -> new CityInfo().setVisible(true));
        tourismButton.addActionListener(e -> new Tourism().setVisible(true));
        transportButton.addActionListener(e -> new Transportation().setVisible(true));
        shoppingButton.addActionListener(e -> new ShoppingMalls().setVisible(true));
    }

    // **Create a Styled Button**
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setPreferredSize(new Dimension(180, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // **3D Button Effect**
        button.setBorder(BorderFactory.createRaisedSoftBevelBorder());

        // **Hover Effect**
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 153, 255));
                button.setBorder(BorderFactory.createLoweredSoftBevelBorder());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 204));
                button.setBorder(BorderFactory.createRaisedSoftBevelBorder());
            }
        });

        return button;
    }
}
