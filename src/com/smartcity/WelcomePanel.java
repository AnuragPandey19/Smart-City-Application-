package com.smartcity;

import javax.swing.*;
import java.awt.*;

/**
 * Landing screen. First thing a user sees.
 * Sells the app in one line, then routes to Login.
 */
public class WelcomePanel extends JPanel {

    public WelcomePanel(MainFrame frame) {
        setBackground(UI.BG);
        setLayout(new GridBagLayout());

        JPanel card = UI.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(560, 360));

        JLabel title = UI.h1("Smart City Guide");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = UI.muted("Everything you need to navigate a city — in one place.");
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea description = new JTextArea(
                "Search hotels, restaurants and tourist places, plan transport between "
              + "locations, browse shopping malls, and look up essential city information "
              + "and emergency contacts. Designed for travellers and residents alike.");
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(UI.BODY);
        description.setForeground(UI.TEXT_MUTED);
        description.setBackground(UI.SURFACE);
        description.setOpaque(false);
        description.setMaximumSize(new Dimension(480, 100));
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton getStarted = UI.primaryButton("Get Started");
        getStarted.setAlignmentX(Component.CENTER_ALIGNMENT);
        getStarted.addActionListener(e -> frame.show(MainFrame.LOGIN));

        card.add(Box.createVerticalGlue());
        card.add(title);
        card.add(Box.createVerticalStrut(UI.PAD_S));
        card.add(tagline);
        card.add(Box.createVerticalStrut(UI.PAD_L));
        card.add(description);
        card.add(Box.createVerticalStrut(UI.PAD_L));
        card.add(getStarted);
        card.add(Box.createVerticalGlue());

        add(card);
    }
}
