package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Home dashboard shown after sign-in. Greets the user and surfaces the
 * four primary sections as descriptive tiles instead of plain buttons.
 */
public class HomePanel extends JPanel {

    private final JLabel greeting = UI.h1("Welcome");
    private final JLabel subtext  = UI.muted("Pick a section to get started.");
    private final MainFrame frame;

    public HomePanel(MainFrame frame) {
        this.frame = frame;
        setBackground(UI.BG);
        setLayout(new BorderLayout());
        setBorder(UI.pad(UI.PAD_XL, UI.PAD_XL, UI.PAD_XL, UI.PAD_XL));

        /* Header */
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(greeting);
        header.add(Box.createVerticalStrut(UI.PAD_S));
        header.add(subtext);
        add(header, BorderLayout.NORTH);

        /* Tile grid */
        JPanel grid = new JPanel(new GridLayout(2, 2, UI.PAD_L, UI.PAD_L));
        grid.setOpaque(false);
        grid.setBorder(UI.pad(UI.PAD_L, 0, 0, 0));

        grid.add(tile("City Information",
                "Population, key facts, and emergency contacts for any city.",
                MainFrame.CITY));
        grid.add(tile("Tourism",
                "Hotels to stay at, places to visit, and restaurants to try.",
                MainFrame.TOURISM));
        grid.add(tile("Transportation",
                "Find rides between locations with fares and booking options.",
                MainFrame.TRANSPORT));
        grid.add(tile("Shopping Malls",
                "Browse malls by location with their opening hours.",
                MainFrame.SHOPPING));

        add(grid, BorderLayout.CENTER);
    }

    /** Call this after login so the greeting reflects the new user. */
    public void refreshGreeting() {
        String name = frame.getCurrentDisplayName();
        greeting.setText(name == null ? "Welcome" : "Welcome, " + firstName(name));
    }

    private static String firstName(String full) {
        int sp = full.indexOf(' ');
        return sp > 0 ? full.substring(0, sp) : full;
    }

    private JPanel tile(String title, String desc, String targetCard) {
        JPanel card = UI.card();
        card.setLayout(new BorderLayout(0, UI.PAD_S));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel t = UI.h3(title);
        JTextArea d = new JTextArea(desc);
        d.setEditable(false);
        d.setLineWrap(true);
        d.setWrapStyleWord(true);
        d.setOpaque(false);
        d.setFont(UI.BODY);
        d.setForeground(UI.TEXT_MUTED);

        JLabel arrow = new JLabel("Open  →");
        arrow.setFont(UI.BODY_BOLD);
        arrow.setForeground(UI.PRIMARY);

        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.SOUTH);

        // Subtle hover so the user knows it's clickable. The card paints its
        // own background, so we re-fill it via a foreground component.
        MouseAdapter hover = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)  { frame.show(targetCard); }
            @Override public void mouseEntered(MouseEvent e)  { arrow.setForeground(UI.PRIMARY_HOVER); }
            @Override public void mouseExited (MouseEvent e)  { arrow.setForeground(UI.PRIMARY); }
        };
        card.addMouseListener(hover);
        d.addMouseListener(hover);
        t.addMouseListener(hover);
        arrow.addMouseListener(hover);
        return card;
    }
}
