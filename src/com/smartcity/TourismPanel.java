package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Tourism hub — routes to Hotels, Places to Visit, or Restaurants.
 */
public class TourismPanel extends JPanel {

    public TourismPanel(MainFrame frame) {
        setBackground(UI.BG);
        setLayout(new BorderLayout());
        setBorder(UI.pad(UI.PAD_XL, UI.PAD_XL, UI.PAD_XL, UI.PAD_XL));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(UI.h1("Tourism"));
        header.add(Box.createVerticalStrut(UI.PAD_S));
        header.add(UI.muted("Where to stay, what to see, where to eat."));
        add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 3, UI.PAD_L, 0));
        grid.setOpaque(false);
        grid.setBorder(UI.pad(UI.PAD_L, 0, 0, 0));

        grid.add(tile(frame, "Hotels",
                "Search hotels by location — contact, price, and ratings.",
                MainFrame.HOTELS));
        grid.add(tile(frame, "Places to Visit",
                "Tourist spots with descriptions, fees, and opening hours.",
                MainFrame.PLACES));
        grid.add(tile(frame, "Restaurants",
                "Restaurants by location with cuisine and price range.",
                MainFrame.FOOD));

        add(grid, BorderLayout.CENTER);
    }

    private JPanel tile(MainFrame frame, String title, String desc, String card) {
        JPanel p = UI.card();
        p.setLayout(new BorderLayout(0, UI.PAD_S));
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        p.add(t, BorderLayout.NORTH);
        p.add(d, BorderLayout.CENTER);
        p.add(arrow, BorderLayout.SOUTH);

        MouseAdapter hover = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { frame.show(card); }
            @Override public void mouseEntered(MouseEvent e) { arrow.setForeground(UI.PRIMARY_HOVER); }
            @Override public void mouseExited (MouseEvent e) { arrow.setForeground(UI.PRIMARY); }
        };
        p.addMouseListener(hover);
        t.addMouseListener(hover);
        d.addMouseListener(hover);
        arrow.addMouseListener(hover);
        return p;
    }
}
