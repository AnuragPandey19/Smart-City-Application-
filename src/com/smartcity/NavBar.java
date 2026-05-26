package com.smartcity;

import javax.swing.*;
import java.awt.*;

/**
 * Top navigation bar shown after sign-in.
 * Left:  Back + brand + breadcrumb.
 * Right: greeting, Home, Logout.
 */
public class NavBar extends JPanel {
    private final JLabel greeting = new JLabel(" ");
    private final JLabel crumb    = new JLabel(" ");
    private final JButton backBtn;

    public NavBar(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(new Color(0x14, 0x1A, 0x2E)); // slightly darker than surface for separation
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UI.BORDER),
                UI.pad(12, 24, 12, 24)));

        /* Left: back + brand + breadcrumb */
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        backBtn = ghostNavButton("‹  Back");
        backBtn.addActionListener(e -> frame.back());
        backBtn.setEnabled(false);

        JLabel brand = new JLabel("Smart City Guide");
        brand.setFont(UI.H3);
        brand.setForeground(UI.TEXT);

        crumb.setFont(UI.BODY);
        crumb.setForeground(UI.TEXT_MUTED);

        left.add(backBtn);
        left.add(brand);
        left.add(crumb);

        /* Right: greeting + home + logout */
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        greeting.setFont(UI.BODY);
        greeting.setForeground(UI.TEXT_MUTED);

        JButton home   = ghostNavButton("Home");
        JButton logout = primaryNavButton("Logout");
        home.addActionListener(e -> frame.show(MainFrame.HOME));
        logout.addActionListener(e -> frame.logout());

        right.add(greeting);
        right.add(home);
        right.add(logout);

        add(left,  BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    /* Subtle button that fits the dark nav bar. */
    private JButton ghostNavButton(String text) {
        JButton b = new JButton(text);
        b.setFont(UI.BODY_BOLD);
        b.setForeground(UI.TEXT);
        b.setBackground(new Color(0x1E, 0x24, 0x40));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(UI.pad(7, 14, 7, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addChangeListener(e -> {
            if (!b.isEnabled()) {
                b.setForeground(UI.TEXT_MUTED);
                b.setBackground(new Color(0x16, 0x1B, 0x2E));
            } else if (b.getModel().isRollover()) {
                b.setBackground(new Color(0x2A, 0x32, 0x55));
            } else {
                b.setForeground(UI.TEXT);
                b.setBackground(new Color(0x1E, 0x24, 0x40));
            }
        });
        return b;
    }

    private JButton primaryNavButton(String text) {
        JButton b = new JButton(text);
        b.setFont(UI.BODY_BOLD);
        b.setForeground(new Color(0x0B, 0x10, 0x20));
        b.setBackground(UI.PRIMARY);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setBorder(UI.pad(7, 14, 7, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addChangeListener(e -> b.setBackground(b.getModel().isRollover() ? UI.PRIMARY_HOVER : UI.PRIMARY));
        return b;
    }

    public void setUser(String displayName) {
        greeting.setText(displayName == null ? " " : "Hi, " + displayName);
    }

    public void setBackEnabled(boolean enabled) {
        backBtn.setEnabled(enabled);
    }

    public void setActive(String cardName) {
        String label;
        switch (cardName) {
            case MainFrame.HOME:      label = "";                              break;
            case MainFrame.CITY:      label = "›  City Information";           break;
            case MainFrame.TOURISM:   label = "›  Tourism";                    break;
            case MainFrame.HOTELS:    label = "›  Tourism  ›  Hotels";         break;
            case MainFrame.PLACES:    label = "›  Tourism  ›  Places to Visit";break;
            case MainFrame.FOOD:      label = "›  Tourism  ›  Restaurants";    break;
            case MainFrame.TRANSPORT: label = "›  Transportation";             break;
            case MainFrame.SHOPPING:  label = "›  Shopping Malls";             break;
            default: label = "";
        }
        crumb.setText(label);
    }
}
