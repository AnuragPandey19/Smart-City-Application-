package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates the {@code smartcity} database + tables + seed data on first run.
 *
 * Reads {@code /resources/schema.sql} from the classpath (bundled inside the
 * runnable jar) and feeds the statements to the MySQL server.
 */
public final class SchemaInitializer {

    private static final Logger LOGGER = Logger.getLogger(SchemaInitializer.class.getName());
    private static final String RESOURCE_PATH = "/resources/schema.sql";

    private SchemaInitializer() {}

    /**
     * Runs the schema. Shows a small modal "Setting up database…" dialog while
     * the work happens so the user sees something instead of a frozen window.
     *
     * @return true on success, false if anything went wrong.
     */
    public static boolean initialize() {
        ProgressDialog progress = new ProgressDialog("Setting up the database",
                "Creating tables and seeding data — one-time setup, takes a few seconds.");

        final boolean[] success = {false};
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                return runSchemaQuietly();
            }
            @Override protected void done() {
                try { success[0] = get(); } catch (Exception e) { success[0] = false; }
                progress.dispose();
            }
        };
        worker.execute();
        progress.setVisible(true); // blocks until dispose()
        return success[0];
    }

    /** Run schema synchronously without UI. Useful from tests / scripts. */
    public static boolean runSchemaQuietly() {
        List<String> statements;
        try {
            statements = loadAndSplit();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't load bundled schema.sql", e);
            return false;
        }

        try (Connection con = DBConnection.getServerConnection();
             Statement st = con.createStatement()) {
            for (String sql : statements) {
                st.execute(sql);
            }
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Schema initialisation failed", e);
            return false;
        }
    }

    /* ====================================================================
     * SQL loading / splitting
     * ==================================================================*/

    private static List<String> loadAndSplit() throws IOException {
        try (InputStream in = SchemaInitializer.class.getResourceAsStream(RESOURCE_PATH)) {
            if (in == null) {
                throw new IOException("Resource not found on classpath: " + RESOURCE_PATH);
            }
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder all = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    // Skip pure comment lines so the parser stays simple.
                    String trimmed = line.trim();
                    if (trimmed.startsWith("--")) continue;
                    all.append(line).append('\n');
                }
                return splitOnSemicolons(all.toString());
            }
        }
    }

    /**
     * Splits a SQL script on semicolons that are NOT inside single-quoted
     * string literals. MySQL escapes a quote by doubling it (e.g. India''s),
     * which we handle by simply toggling a quote flag — two consecutive
     * single-quotes flip the flag twice and net out to "still in string".
     */
    static List<String> splitOnSemicolons(String script) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inString = false;
        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);
            if (c == '\'') {
                inString = !inString;
                cur.append(c);
            } else if (c == ';' && !inString) {
                String stmt = cur.toString().trim();
                if (!stmt.isEmpty()) out.add(stmt);
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        String tail = cur.toString().trim();
        if (!tail.isEmpty()) out.add(tail);
        return out;
    }

    /* ====================================================================
     * Progress dialog
     * ==================================================================*/

    private static class ProgressDialog extends JDialog {
        ProgressDialog(String title, String message) {
            super((Frame) null, title, true);
            setSize(440, 180);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            getContentPane().setBackground(UI.BG);
            setLayout(new GridBagLayout());

            JPanel card = UI.card();
            card.setLayout(new BorderLayout(0, UI.PAD_M));
            card.setPreferredSize(new Dimension(380, 120));

            JLabel head = UI.h3(title);
            JTextArea body = new JTextArea(message);
            body.setEditable(false);
            body.setLineWrap(true);
            body.setWrapStyleWord(true);
            body.setOpaque(false);
            body.setFont(UI.BODY);
            body.setForeground(UI.TEXT_MUTED);

            JProgressBar bar = new JProgressBar();
            bar.setIndeterminate(true);
            bar.setForeground(UI.PRIMARY);
            bar.setBackground(UI.SURFACE_ALT);
            bar.setBorderPainted(false);

            card.add(head, BorderLayout.NORTH);
            card.add(body, BorderLayout.CENTER);
            card.add(bar,  BorderLayout.SOUTH);
            add(card);
        }
    }
}
