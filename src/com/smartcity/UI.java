package com.smartcity;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Central dark theme + reusable component factory.
 * Every screen must source its colors, fonts, and primary widgets from here
 * so the app looks consistent.
 */
public final class UI {

    private UI() {}

    /* =========================  PALETTE  (DARK) ========================= */
    public static final Color BG          = new Color(0x0B, 0x10, 0x20); // app background — near black
    public static final Color SURFACE     = new Color(0x16, 0x1B, 0x2E); // cards / panels
    public static final Color SURFACE_ALT = new Color(0x1E, 0x24, 0x40); // alt rows / hover
    public static final Color BORDER      = new Color(0x2A, 0x30, 0x50); // hairline strokes

    public static final Color PRIMARY       = new Color(0x5B, 0x8C, 0xFF); // electric blue
    public static final Color PRIMARY_HOVER = new Color(0x7B, 0xA3, 0xFF);
    public static final Color ACCENT        = new Color(0x2D, 0xD4, 0xBF); // teal
    public static final Color ACCENT_HOVER  = new Color(0x5E, 0xEA, 0xD4);
    public static final Color DANGER        = new Color(0xF8, 0x71, 0x71);
    public static final Color WARNING       = new Color(0xFB, 0xBF, 0x24);

    public static final Color TEXT       = new Color(0xEC, 0xEF, 0xF4); // near-white body
    public static final Color TEXT_MUTED = new Color(0x94, 0xA3, 0xB8); // secondary

    /* =========================  FONTS  ========================= */
    private static final String FAMILY = "Segoe UI";
    public static final Font H1    = new Font(FAMILY, Font.BOLD, 30);
    public static final Font H2    = new Font(FAMILY, Font.BOLD, 22);
    public static final Font H3    = new Font(FAMILY, Font.BOLD, 17);
    public static final Font BODY  = new Font(FAMILY, Font.PLAIN, 14);
    public static final Font BODY_BOLD = new Font(FAMILY, Font.BOLD, 14);
    public static final Font SMALL = new Font(FAMILY, Font.PLAIN, 12);

    /* =========================  SPACING  ========================= */
    public static final int PAD_S  = 6;
    public static final int PAD_M  = 12;
    public static final int PAD_L  = 20;
    public static final int PAD_XL = 32;

    /* =========================  GLOBAL L&F SETUP  ========================= */

    /**
     * Apply the dark theme globally. Call ONCE before constructing MainFrame.
     * Uses Nimbus (built into every JDK) and overrides its color keys so
     * popups, dropdowns, dialogs, and tables all pick up the dark palette.
     */
    public static void applyDarkLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { /* fall through to defaults */ }

        ColorUIResource bg       = new ColorUIResource(BG);
        ColorUIResource surface  = new ColorUIResource(SURFACE);
        ColorUIResource border   = new ColorUIResource(BORDER);
        ColorUIResource text     = new ColorUIResource(TEXT);
        ColorUIResource muted    = new ColorUIResource(TEXT_MUTED);
        ColorUIResource primary  = new ColorUIResource(PRIMARY);
        ColorUIResource selBg    = new ColorUIResource(SURFACE_ALT);
        FontUIResource bodyFont  = new FontUIResource(BODY);

        UIManager.put("control",       surface);
        UIManager.put("info",          surface);
        UIManager.put("nimbusBase",    new ColorUIResource(0x16_1B_2E));
        UIManager.put("nimbusBlueGrey",new ColorUIResource(0x2A_30_50));
        UIManager.put("nimbusLightBackground", surface);
        UIManager.put("text",          text);

        UIManager.put("Panel.background",       bg);
        UIManager.put("Panel.foreground",       text);
        UIManager.put("OptionPane.background",  surface);
        UIManager.put("OptionPane.messageForeground", text);
        UIManager.put("OptionPane.foreground",  text);

        UIManager.put("Label.foreground",       text);
        UIManager.put("Label.font",             bodyFont);

        UIManager.put("Button.background",      surface);
        UIManager.put("Button.foreground",      text);
        UIManager.put("Button.font",            bodyFont);

        UIManager.put("TextField.background",   surface);
        UIManager.put("TextField.foreground",   text);
        UIManager.put("TextField.caretForeground", text);
        UIManager.put("PasswordField.background", surface);
        UIManager.put("PasswordField.foreground", text);
        UIManager.put("PasswordField.caretForeground", text);
        UIManager.put("TextArea.background",    surface);
        UIManager.put("TextArea.foreground",    text);
        UIManager.put("TextArea.caretForeground", text);

        UIManager.put("ComboBox.background",       surface);
        UIManager.put("ComboBox.foreground",       text);
        UIManager.put("ComboBox.buttonBackground", surface);
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(0x2A_44_88));
        UIManager.put("ComboBox.selectionForeground", text);
        UIManager.put("ComboBox:\"ComboBox.listRenderer\".background", surface);
        UIManager.put("ComboBox:\"ComboBox.listRenderer\".foreground", text);

        UIManager.put("List.background",        surface);
        UIManager.put("List.foreground",        text);
        UIManager.put("List.selectionBackground", new ColorUIResource(0x2A_44_88));
        UIManager.put("List.selectionForeground", text);

        UIManager.put("Table.background",       surface);
        UIManager.put("Table.foreground",       text);
        UIManager.put("Table.alternateRowColor", new ColorUIResource(0x1E_24_40));
        UIManager.put("Table.gridColor",        border);
        UIManager.put("Table.selectionBackground", new ColorUIResource(0x2A_44_88));
        UIManager.put("Table.selectionForeground", text);
        UIManager.put("TableHeader.background", new ColorUIResource(0x1F_27_4B));
        UIManager.put("TableHeader.foreground", text);
        UIManager.put("TableHeader.font",       new FontUIResource(BODY_BOLD));

        UIManager.put("ScrollPane.background",  bg);
        UIManager.put("ScrollPane.foreground",  text);
        UIManager.put("Viewport.background",    bg);
        UIManager.put("ScrollBar.thumb",        new ColorUIResource(SURFACE_ALT));
        UIManager.put("ScrollBar.track",        bg);

        UIManager.put("CheckBox.background",    surface);
        UIManager.put("CheckBox.foreground",    text);

        UIManager.put("ToolTip.background",     surface);
        UIManager.put("ToolTip.foreground",     text);
    }

    /* =========================  FACTORIES  ========================= */

    public static JLabel h1(String text) { return label(text, H1, TEXT); }
    public static JLabel h2(String text) { return label(text, H2, TEXT); }
    public static JLabel h3(String text) { return label(text, H3, TEXT); }
    public static JLabel body(String text)  { return label(text, BODY, TEXT); }
    public static JLabel muted(String text) { return label(text, BODY, TEXT_MUTED); }

    private static JLabel label(String text, Font font, Color fg) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(fg);
        return l;
    }

    public static JTextField textField(int cols) {
        JTextField f = new JTextField(cols);
        styleTextComponent(f);
        return f;
    }

    public static JPasswordField passwordField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        styleTextComponent(f);
        return f;
    }

    private static void styleTextComponent(JComponent c) {
        c.setFont(BODY);
        c.setBackground(SURFACE);
        c.setForeground(TEXT);
        if (c instanceof JTextField)  ((JTextField) c).setCaretColor(TEXT);
        c.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
    }

    /** Primary action button (filled blue). */
    public static JButton primaryButton(String text) {
        return styledButton(text, PRIMARY, PRIMARY_HOVER, new Color(0x0B, 0x10, 0x20));
    }

    /** Accent action button (filled teal) — for "create / register" actions. */
    public static JButton accentButton(String text) {
        return styledButton(text, ACCENT, ACCENT_HOVER, new Color(0x0B, 0x10, 0x20));
    }

    /** Secondary / ghost button — used in nav + back actions. */
    public static JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BODY_BOLD);
        b.setForeground(PRIMARY);
        b.setBackground(SURFACE);
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setBorder(new CompoundBorder(
                new LineBorder(PRIMARY, 1, true),
                new EmptyBorder(8, 16, 8, 16)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(SURFACE_ALT); }
            @Override public void mouseExited (MouseEvent e) { b.setBackground(SURFACE); }
        });
        return b;
    }

    private static JButton styledButton(String text, Color base, Color hover, Color fg) {
        JButton b = new JButton(text);
        b.setFont(BODY_BOLD);
        b.setForeground(fg);
        b.setBackground(base);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setBorder(new EmptyBorder(10, 22, 10, 22));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited (MouseEvent e) { b.setBackground(base); }
        });
        return b;
    }

    /**
     * Card panel — dark surface with rounded corners and a hairline border.
     * Use as a container for forms / content blocks.
     */
    public static JPanel card() {
        JPanel p = new RoundedPanel(14, SURFACE, BORDER);
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(PAD_L, PAD_L, PAD_L, PAD_L));
        return p;
    }

    /** Convenience: form-row builder that returns a 2-col panel (label / field). */
    public static JPanel formRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(PAD_M, 0));
        row.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(BODY_BOLD);
        l.setForeground(TEXT);
        l.setPreferredSize(new Dimension(180, 36));
        row.add(l, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    /** Apply consistent table styling. */
    public static void styleTable(JTable t) {
        t.setFont(BODY);
        t.setRowHeight(30);
        t.setGridColor(BORDER);
        t.setShowGrid(true);
        t.setForeground(TEXT);
        t.setBackground(SURFACE);
        t.setSelectionBackground(new Color(0x2A, 0x44, 0x88));
        t.setSelectionForeground(TEXT);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setFont(BODY_BOLD);
        t.getTableHeader().setBackground(new Color(0x1F, 0x27, 0x4B));
        t.getTableHeader().setForeground(TEXT);
        t.getTableHeader().setReorderingAllowed(false);
    }

    /** Empty border using our spacing scale. */
    public static Border pad(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }

    /* ==========  Custom rounded card panel  ========== */
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;
        private final Color stroke;

        RoundedPanel(int radius, Color fill, Color stroke) {
            this.radius = radius;
            this.fill = fill;
            this.stroke = stroke;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(stroke);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
