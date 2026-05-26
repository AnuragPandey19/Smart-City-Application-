package com.smartcity;

import javax.swing.*;

/**
 * First-run / every-run startup flow.
 *
 *   1. Try to load saved MySQL credentials.
 *   2. If they don't work, pop the {@link ConnectionSetupDialog} until the
 *      user either enters valid ones or cancels.
 *   3. If the {@code smartcity} database doesn't exist yet, run
 *      {@link SchemaInitializer} to create + seed it.
 *
 * Returns true when the app is ready to show its main window.
 * Returns false if the user cancelled or something fatal went wrong.
 */
public final class Bootstrap {

    private Bootstrap() {}

    public static boolean run() {
        // 1) Load whatever we have on disk (may be nothing on first launch).
        DBConnection.tryLoadCredentials();

        // 2) Make sure the loaded creds actually work.
        while (!DBConnection.testServerConnection()) {
            ConnectionSetupDialog dialog = new ConnectionSetupDialog();
            dialog.setVisible(true);
            if (!dialog.isSaved()) {
                return false; // user cancelled
            }
            // loop and re-test — saveCredentials() updated the in-memory pair
        }

        // 3) Ensure the smartcity schema is present + seeded.
        if (!DBConnection.databaseExists()) {
            boolean ok = SchemaInitializer.initialize();
            if (!ok) {
                JOptionPane.showMessageDialog(null,
                        "Couldn't create the smartcity database.\n"
                      + "Check that your MySQL user has CREATE DATABASE privilege.",
                        "Setup failed", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }
}
