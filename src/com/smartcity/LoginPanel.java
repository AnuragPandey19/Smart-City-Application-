package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sign-in screen. Verifies credentials against the users table, then
 * routes the user into the main app via {@link MainFrame#onLoginSuccess}.
 */
public class LoginPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(LoginPanel.class.getName());

    private final JTextField userField = UI.textField(20);
    private final JPasswordField passField = UI.passwordField(20);

    public LoginPanel(MainFrame frame) {
        setBackground(UI.BG);
        setLayout(new GridBagLayout());

        JPanel card = UI.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(440, 420));

        JLabel title = UI.h2("Sign in to your account");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = UI.muted("Use the credentials you signed up with.");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(UI.formRow("Username", userField));
        form.add(Box.createVerticalStrut(UI.PAD_M));
        form.add(UI.formRow("Password", passField));
        form.setAlignmentX(Component.CENTER_ALIGNMENT);

        JCheckBox showPwd = new JCheckBox("Show password");
        showPwd.setOpaque(false);
        showPwd.setFont(UI.SMALL);
        showPwd.setForeground(UI.TEXT_MUTED);
        showPwd.setFocusPainted(false);
        showPwd.setAlignmentX(Component.CENTER_ALIGNMENT);
        showPwd.addActionListener(e -> passField.setEchoChar(showPwd.isSelected() ? (char) 0 : '•'));

        JButton signIn = UI.primaryButton("Sign In");
        signIn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signIn.addActionListener(e -> attemptLogin(frame));

        JButton goSignUp = UI.ghostButton("New here? Create an account");
        goSignUp.setAlignmentX(Component.CENTER_ALIGNMENT);
        goSignUp.addActionListener(e -> frame.show(MainFrame.SIGNUP));

        // Enter key submits
        getRootPaneSubmit(signIn);

        card.add(title);
        card.add(Box.createVerticalStrut(UI.PAD_S));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(UI.PAD_L));
        card.add(form);
        card.add(Box.createVerticalStrut(UI.PAD_S));
        card.add(showPwd);
        card.add(Box.createVerticalStrut(UI.PAD_L));
        card.add(signIn);
        card.add(Box.createVerticalStrut(UI.PAD_M));
        card.add(goSignUp);

        add(card);
    }

    /** Make Enter trigger the primary button while a field is focused. */
    private void getRootPaneSubmit(JButton signIn) {
        java.awt.event.ActionListener submit = e -> signIn.doClick();
        userField.addActionListener(submit);
        passField.addActionListener(submit);
    }

    private void attemptLogin(MainFrame frame) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter both your username and password.",
                    "Missing fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                JOptionPane.showMessageDialog(this,
                        "Couldn't reach the database. Check your connection and try again.",
                        "Connection error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (PreparedStatement pst = con.prepareStatement(
                    "SELECT name, password FROM users WHERE username = ?")) {
                pst.setString(1, username);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next() && PasswordUtil.matches(password, rs.getString("password"))) {
                        String displayName = rs.getString("name");
                        passField.setText("");
                        frame.onLoginSuccess(displayName == null ? username : displayName, username);
                        return;
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Login DB error", ex);
            JOptionPane.showMessageDialog(this,
                    "Something went wrong on our side. Please try again.",
                    "Server error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "That username and password don't match. Please try again.",
                "Incorrect credentials", JOptionPane.ERROR_MESSAGE);
    }
}
