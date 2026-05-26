package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Looks up a city by name and renders a rich profile —
 * facts, description, and emergency contacts.
 */
public class CityInfoPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(CityInfoPanel.class.getName());

    private final JTextField searchField = UI.textField(22);
    private final JEditorPane infoPane = new JEditorPane();
    private final JLabel headerLabel = UI.h1("City Information");
    private final JLabel subHeader   = UI.muted("Search a city by name for its profile, facts, and emergency contacts.");

    public CityInfoPanel() {
        setBackground(UI.BG);
        setLayout(new BorderLayout(0, UI.PAD_L));
        setBorder(UI.pad(UI.PAD_XL, UI.PAD_XL, UI.PAD_XL, UI.PAD_XL));

        /* ---- TOP: title + search bar ---- */
        JPanel header = new JPanel(new BorderLayout(0, UI.PAD_S));
        header.setOpaque(false);
        header.add(headerLabel, BorderLayout.NORTH);
        header.add(subHeader, BorderLayout.CENTER);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, UI.PAD_S, 0));
        searchBar.setOpaque(false);
        searchBar.setBorder(UI.pad(UI.PAD_M, 0, 0, 0));
        JLabel cityLabel = new JLabel("City name:");
        cityLabel.setForeground(UI.TEXT);
        JButton searchBtn = UI.primaryButton("Search");
        searchField.addActionListener(e -> searchBtn.doClick());
        searchBtn.addActionListener(e -> lookupCity());
        searchBar.add(cityLabel);
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        header.add(searchBar, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        /* ---- BODY: HTML profile pane ---- */
        infoPane.setContentType("text/html");
        infoPane.setEditable(false);
        infoPane.setBackground(UI.SURFACE);
        infoPane.setBorder(UI.pad(UI.PAD_L, UI.PAD_L, UI.PAD_L, UI.PAD_L));
        renderEmptyState();

        JScrollPane scroll = new JScrollPane(infoPane);
        scroll.setBorder(BorderFactory.createLineBorder(UI.BORDER, 1));
        scroll.getViewport().setBackground(UI.SURFACE);
        add(scroll, BorderLayout.CENTER);
    }

    /* ----------------------------------------------------------------- */

    private void renderEmptyState() {
        infoPane.setText(htmlShell(
            "<div style=\"text-align:center; padding:60px 20px;\">"
          + "<div style=\"font-size:48px; margin-bottom:14px;\">⟢</div>"
          + "<div style=\"color:#94A3B8; font-size:15px;\">"
          + "Try a city like <b style=\"color:#5B8CFF;\">Mumbai</b>, "
          + "<b style=\"color:#5B8CFF;\">Bengaluru</b>, "
          + "<b style=\"color:#5B8CFF;\">Singapore</b>, or "
          + "<b style=\"color:#5B8CFF;\">Dubai</b>.</div>"
          + "</div>"));
    }

    private void lookupCity() {
        String name = searchField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Type a city name first.",
                    "Missing input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                infoPane.setText(htmlShell(
                        "<div style=\"color:#F87171; padding:30px;\">Couldn't reach the database.</div>"));
                return;
            }
            try (PreparedStatement pst = con.prepareStatement(
                    "SELECT name, state, country, population, area_sqkm, "
                  + "languages, famous_for, best_time_to_visit, description, "
                  + "emergency_contacts "
                  + "FROM city_information WHERE name = ?")) {
                pst.setString(1, name);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        renderCity(rs);
                    } else {
                        renderNotFound(name);
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "City lookup failed", ex);
            infoPane.setText(htmlShell(
                    "<div style=\"color:#F87171; padding:30px;\">Something went wrong while loading this city.</div>"));
        }
    }

    private void renderCity(ResultSet rs) throws SQLException {
        String city       = rs.getString("name");
        String state      = rs.getString("state");
        String country    = rs.getString("country");
        int population    = rs.getInt("population");
        double area       = rs.getDouble("area_sqkm");
        String languages  = rs.getString("languages");
        String famousFor  = rs.getString("famous_for");
        String bestTime   = rs.getString("best_time_to_visit");
        String description= rs.getString("description");
        String contacts   = rs.getString("emergency_contacts");

        headerLabel.setText(city);
        subHeader.setText(state + ", " + country);

        String formattedPop  = NumberFormat.getNumberInstance(Locale.US).format(population);
        String formattedArea = String.format(Locale.US, "%,.0f km²", area);
        double density       = area > 0 ? population / area : 0;
        String formattedDen  = String.format(Locale.US, "%,.0f / km²", density);

        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"padding:6px 0 18px 0;\">");
        sb.append("<table cellpadding='0' cellspacing='0' style=\"width:100%;\"><tr>");
        sb.append(factCell("POPULATION", formattedPop));
        sb.append(factCell("AREA",       formattedArea));
        sb.append(factCell("DENSITY",    formattedDen));
        sb.append("</tr></table></div>");

        sb.append(sectionLabel("About"));
        sb.append("<div style=\"color:#ECEFF4; font-size:14px; line-height:1.65; margin-bottom:22px;\">")
          .append(escape(description)).append("</div>");

        sb.append(sectionLabel("Famous for"));
        sb.append("<div style=\"color:#ECEFF4; font-size:14px; margin-bottom:22px;\">")
          .append(escape(famousFor)).append("</div>");

        sb.append("<table cellpadding='0' cellspacing='0' style=\"width:100%; margin-bottom:22px;\"><tr>");
        sb.append(infoCell("Languages", languages));
        sb.append(infoCell("Best time to visit", bestTime));
        sb.append("</tr></table>");

        sb.append("<div style=\"background:#3B1F1F; border-left:4px solid #F87171; ")
          .append("padding:14px 16px; border-radius:6px;\">");
        sb.append("<div style=\"color:#F87171; font-weight:bold; font-size:12px; ")
          .append("letter-spacing:0.5px; margin-bottom:6px;\">EMERGENCY CONTACTS</div>");
        sb.append("<div style=\"color:#ECEFF4; font-size:14px;\">")
          .append(escape(contacts)).append("</div>");
        sb.append("</div>");

        infoPane.setText(htmlShell(sb.toString()));
        infoPane.setCaretPosition(0);
    }

    private void renderNotFound(String name) {
        headerLabel.setText("City Information");
        subHeader.setText("Search a city by name for its profile, facts, and emergency contacts.");
        infoPane.setText(htmlShell(
            "<div style=\"text-align:center; padding:60px 20px;\">"
          + "<div style=\"font-size:48px; margin-bottom:14px;\">⚠</div>"
          + "<div style=\"color:#ECEFF4; font-size:16px; margin-bottom:6px;\">"
          + "No record found for <b style=\"color:#FBBF24;\">" + escape(name) + "</b>.</div>"
          + "<div style=\"color:#94A3B8; font-size:13px;\">"
          + "Check the spelling, or try a different city.</div>"
          + "</div>"));
    }

    /* ---- HTML helpers --------------------------------------------------- */

    private static String htmlShell(String body) {
        return "<html><body style=\"font-family:'Segoe UI', sans-serif; "
             + "background:#161B2E; color:#ECEFF4; margin:0; padding:0;\">"
             + body + "</body></html>";
    }

    private static String factCell(String label, String value) {
        return "<td style=\"width:33%; background:#1E2440; border:1px solid #2A3050; "
             + "border-radius:8px; padding:14px 16px; vertical-align:top;\">"
             + "<div style=\"color:#94A3B8; font-size:11px; letter-spacing:0.7px; margin-bottom:6px;\">"
             + label + "</div>"
             + "<div style=\"color:#5B8CFF; font-size:20px; font-weight:bold;\">"
             + value + "</div></td>"
             + "<td style=\"width:12px;\"></td>";
    }

    private static String infoCell(String label, String value) {
        return "<td style=\"width:50%; vertical-align:top; padding-right:16px;\">"
             + "<div style=\"color:#94A3B8; font-size:11px; letter-spacing:0.7px; margin-bottom:6px;\">"
             + label.toUpperCase() + "</div>"
             + "<div style=\"color:#ECEFF4; font-size:14px;\">"
             + escape(value) + "</div></td>";
    }

    private static String sectionLabel(String text) {
        return "<div style=\"color:#94A3B8; font-size:11px; letter-spacing:0.7px; "
             + "margin-bottom:8px;\">" + text.toUpperCase() + "</div>";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
