package com.smartcity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reusable base for any "search by location → show table of results" screen.
 *
 * Subclasses supply the screen title, the search prompt, the table columns,
 * the SQL, and a {@link #map(ResultSet)} that turns one row of the result
 * set into one row for the table. That's it — no duplicated boilerplate.
 */
public abstract class SearchableTablePanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(SearchableTablePanel.class.getName());

    private final String sql;
    private final JTextField searchField = UI.textField(22);
    private final DefaultTableModel model;
    private final JLabel statusLabel = UI.muted("Enter a location and press Search.");

    protected SearchableTablePanel(String title, String searchHint, String[] columns, String sql) {
        this.sql = sql;
        this.model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        setBackground(UI.BG);
        setLayout(new BorderLayout(0, UI.PAD_M));
        setBorder(UI.pad(UI.PAD_L, UI.PAD_L, UI.PAD_L, UI.PAD_L));

        /* Header */
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UI.h2(title), BorderLayout.NORTH);
        header.add(UI.muted(searchHint), BorderLayout.SOUTH);

        /* Search bar */
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, UI.PAD_S, 0));
        searchBar.setOpaque(false);
        searchBar.setBorder(UI.pad(UI.PAD_M, 0, 0, 0));
        JLabel searchLabel = new JLabel("Location:");
        searchLabel.setForeground(UI.TEXT);
        searchLabel.setFont(UI.BODY_BOLD);
        JButton searchBtn = UI.primaryButton("Search");
        searchBar.add(searchLabel);
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        searchField.addActionListener(e -> searchBtn.doClick());
        searchBtn.addActionListener(e -> runSearch());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(header,    BorderLayout.NORTH);
        top.add(searchBar, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        /* Results table */
        JTable table = new JTable(model);
        UI.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UI.SURFACE);
        scroll.setBorder(BorderFactory.createLineBorder(UI.BORDER, 1));
        scroll.setBackground(UI.SURFACE);
        add(scroll, BorderLayout.CENTER);

        /* Status row */
        add(statusLabel, BorderLayout.SOUTH);
    }

    /** Map one DB row to one table row. */
    protected abstract Object[] map(ResultSet rs) throws SQLException;

    /**
     * How to bind the user's search input into the SQL.
     * Default: substring match (LIKE %x%). Override if you need an exact match
     * or multiple parameters.
     */
    protected void bind(PreparedStatement pst, String userInput) throws SQLException {
        pst.setString(1, "%" + userInput + "%");
    }

    private void runSearch() {
        String input = searchField.getText().trim();
        if (input.isEmpty()) {
            statusLabel.setText("Type a location first.");
            statusLabel.setForeground(UI.DANGER);
            return;
        }

        model.setRowCount(0);
        statusLabel.setForeground(UI.TEXT_MUTED);
        statusLabel.setText("Searching…");

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                statusLabel.setText("Couldn't reach the database.");
                statusLabel.setForeground(UI.DANGER);
                return;
            }
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                bind(pst, input);
                try (ResultSet rs = pst.executeQuery()) {
                    int n = 0;
                    while (rs.next()) {
                        model.addRow(map(rs));
                        n++;
                    }
                    if (n == 0) {
                        statusLabel.setText("No results for \"" + input + "\".");
                    } else {
                        statusLabel.setText(n + (n == 1 ? " result" : " results") + " for \"" + input + "\".");
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Search failed", ex);
            statusLabel.setText("Something went wrong while searching.");
            statusLabel.setForeground(UI.DANGER);
        }
    }
}
