package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Modal dialog shown on first launch (or when the saved credentials stop
 * working). Collects MySQL username + password, tests the connection, and
 * — on success — writes the values to {@code ~/.smartcity/db.properties}.
 *
 * Cancelling exits the app, because the rest of it can't function without
 * a database.
 */
public class ConnectionSetupDialog extends JDialog {

    private final JTextField userField = UI.textField(22);
    private final JPasswordField passField = UI.passwordField(22);
    private final JLabel errorLabel = new JLabel(" ");
    private boolean saved = false;

    public ConnectionSetupDialog() {
        super((Frame) null, "Connect to MySQL", true);
        setSize(520, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UI.BG);
        setLayout(new GridBagLayout());

        JPanel card = UI.card();
        card.setLayout(new BorderLayout(0, UI.PAD_L));
        card.setPreferredSize(new Dimension(460, 380));

        /* ----- Header ----- */
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(UI.h2("Connect to MySQL"));
        header.add(Box.createVerticalStrut(UI.PAD_S));
        JTextArea blurb = new JTextArea(
                "Smart City needs your local MySQL credentials to set up its database. "
              + "Connecting to localhost:3306. We'll save these on your machine so you "
              + "won't be asked again.");
        blurb.setEditable(false);
        blurb.setLineWrap(true);
        blurb.setWrapStyleWord(true);
        blurb.setOpaque(false);
        blurb.setFont(UI.BODY);
        blurb.setForeground(UI.TEXT_MUTED);
        header.add(blurb);
        card.add(header, BorderLayout.NORTH);

        /* ----- Form ----- */
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(UI.formRow("MySQL username", userField));
        form.add(Box.createVerticalStrut(UI.PAD_M));
        form.add(UI.formRow("MySQL password", passField));

        userField.setText("root"); // sensible default
        SwingUtilities.invokeLater(passField::requestFocusInWindow);

        errorLabel.setFont(UI.SMALL);
        errorLabel.setForeground(UI.DANGER);
        errorLabel.setBorder(UI.pad(UI.PAD_M, 0, 0, 0));
        form.add(errorLabel);

        card.add(form, BorderLayout.CENTER);

        /* ----- Footer ----- */
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, UI.PAD_M, 0));
        footer.setOpaque(false);
        JButton cancel = UI.ghostButton("Cancel");
        JButton submit = UI.primaryButton("Test & Save");
        cancel.addActionListener(e -> { saved = false; dispose(); });
        submit.addActionListener(e -> attemptSave());

        // Enter key submits
        java.awt.event.ActionListener enterSubmit = e -> submit.doClick();
        userField.addActionListener(enterSubmit);
        passField.addActionListener(enterSubmit);

        footer.add(cancel);
        footer.add(submit);
        card.add(footer, BorderLayout.SOUTH);

        add(card);
        getRootPane().setDefaultButton(submit);
    }

    private void attemptSave() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        if (user.isEmpty()) {
            showError("Enter your MySQL username.");
            return;
        }

        showError(""); // clear
        setBusy(true);

        // Test in a worker so the UI doesn't freeze if MySQL is unreachable.
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                return DBConnection.testServerConnection(user, pass);
            }
            @Override protected void done() {
                setBusy(false);
                boolean ok;
                try { ok = get(); } catch (Exception e) { ok = false; }
                if (!ok) {
                    showError("Couldn't connect. Check that MySQL is running on localhost:3306 "
                            + "and the password is correct.");
                    passField.selectAll();
                    passField.requestFocusInWindow();
                    return;
                }
                try {
                    DBConnection.saveCredentials(user, pass);
                    saved = true;
                    dispose();
                } catch (IOException io) {
                    showError("Connected, but couldn't save credentials: " + io.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void setBusy(boolean busy) {
        setCursor(Cursor.getPredefinedCursor(busy ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
        userField.setEnabled(!busy);
        passField.setEnabled(!busy);
    }

    private void showError(String msg) {
        errorLabel.setText(msg.isEmpty() ? " " : msg);
    }

    /** True if the user successfully tested + saved credentials. */
    public boolean isSaved() { return saved; }
}
