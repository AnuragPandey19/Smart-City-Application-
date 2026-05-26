package com.smartcity;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Account creation. Inputs are validated locally before we touch the DB,
 * and passwords are stored as PBKDF2-SHA256 hashes via {@link PasswordUtil}.
 */
public class SignUpPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(SignUpPanel.class.getName());
    private static final DateTimeFormatter DOB_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JTextField nameField    = UI.textField(20);
    private final JTextField userField    = UI.textField(20);
    private final JPasswordField passField = UI.passwordField(20);
    private final JTextField dobField     = UI.textField(20);
    private final JTextField countryField = UI.textField(20);
    private final JTextField stateField   = UI.textField(20);
    private final JTextField pinField     = UI.textField(20);
    private final JTextField mobileField  = UI.textField(20);

    public SignUpPanel(MainFrame frame) {
        setBackground(UI.BG);
        setLayout(new GridBagLayout());

        JPanel card = UI.card();
        card.setLayout(new BorderLayout(0, UI.PAD_L));
        card.setPreferredSize(new Dimension(560, 600));

        /* Header */
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(UI.h2("Create your account"), BorderLayout.NORTH);
        header.add(UI.muted("Takes less than a minute. We only ask for what we need."),
                BorderLayout.SOUTH);
        card.add(header, BorderLayout.NORTH);

        /* Form body */
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(UI.formRow("Full name",  nameField));
        form.add(Box.createVerticalStrut(UI.PAD_S));
        form.add(UI.formRow("Username",   userField));
        form.add(Box.createVerticalStrut(UI.PAD_S));
        form.add(UI.formRow("Password",   passField));
        form.add(Box.createVerticalStrut(UI.PAD_S));

        JLabel pwdHint = UI.muted("Use at least 8 characters.");
        pwdHint.setFont(UI.SMALL);
        pwdHint.setBorder(UI.pad(0, 152, 0, 0));
        form.add(pwdHint);
        form.add(Box.createVerticalStrut(UI.PAD_M));

        form.add(UI.formRow("Date of birth (YYYY-MM-DD)", dobField));
        form.add(Box.createVerticalStrut(UI.PAD_S));
        form.add(UI.formRow("Country", countryField));
        form.add(Box.createVerticalStrut(UI.PAD_S));
        form.add(UI.formRow("State",   stateField));
        form.add(Box.createVerticalStrut(UI.PAD_S));
        form.add(UI.formRow("Pincode (6 digits)", pinField));
        form.add(Box.createVerticalStrut(UI.PAD_S));
        form.add(UI.formRow("Mobile (10 digits)", mobileField));

        JScrollPane scroll = new JScrollPane(form,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        card.add(scroll, BorderLayout.CENTER);

        /* Footer */
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, UI.PAD_M, 0));
        footer.setOpaque(false);
        JButton back   = UI.ghostButton("Back to Sign In");
        JButton submit = UI.accentButton("Create account");
        back.addActionListener(e -> frame.show(MainFrame.LOGIN));
        submit.addActionListener(e -> attemptRegister(frame));
        footer.add(back);
        footer.add(submit);
        card.add(footer, BorderLayout.SOUTH);

        add(card);
    }

    private void attemptRegister(MainFrame frame) {
        String name     = nameField.getText().trim();
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());
        String dob      = dobField.getText().trim();
        String country  = countryField.getText().trim();
        String state    = stateField.getText().trim();
        String pincode  = pinField.getText().trim();
        String mobile   = mobileField.getText().trim();

        String error = validate(name, username, password, dob, country, state, pincode, mobile);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Check your details", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                JOptionPane.showMessageDialog(this,
                        "Couldn't reach the database. Try again in a moment.",
                        "Connection error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Reject duplicate usernames cleanly instead of leaning on a SQL constraint error.
            try (PreparedStatement check = con.prepareStatement(
                    "SELECT 1 FROM users WHERE username = ?")) {
                check.setString(1, username);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this,
                                "That username is already taken. Try another.",
                                "Username unavailable", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            String hashed = PasswordUtil.hash(password);
            try (PreparedStatement ins = con.prepareStatement(
                    "INSERT INTO users (name, username, password, dob, country, state, pincode, mobile) "
                  + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                ins.setString(1, name);
                ins.setString(2, username);
                ins.setString(3, hashed);
                ins.setString(4, dob);
                ins.setString(5, country);
                ins.setString(6, state);
                ins.setString(7, pincode);
                ins.setString(8, mobile);
                if (ins.executeUpdate() > 0) {
                    clearFields();
                    JOptionPane.showMessageDialog(this,
                            "Account created. You can sign in now.",
                            "All set", JOptionPane.INFORMATION_MESSAGE);
                    frame.show(MainFrame.LOGIN);
                    return;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Signup DB error", ex);
            JOptionPane.showMessageDialog(this,
                    "Couldn't create your account. Please try again.",
                    "Server error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Couldn't create your account. Please try again.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String validate(String name, String username, String password, String dob,
                            String country, String state, String pincode, String mobile) {
        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || dob.isEmpty()
                || country.isEmpty() || state.isEmpty() || pincode.isEmpty() || mobile.isEmpty()) {
            return "All fields are required.";
        }
        if (username.length() < 3 || !username.matches("[A-Za-z0-9_.-]+")) {
            return "Username must be at least 3 characters and contain only letters, digits, '.', '_' or '-'.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters.";
        }
        try {
            LocalDate parsed = LocalDate.parse(dob, DOB_FMT);
            if (parsed.isAfter(LocalDate.now())) {
                return "Date of birth can't be in the future.";
            }
        } catch (DateTimeParseException ex) {
            return "Date of birth must be in the format YYYY-MM-DD (e.g., 1998-04-21).";
        }
        if (!pincode.matches("\\d{6}")) {
            return "Pincode must be exactly 6 digits.";
        }
        if (!mobile.matches("\\d{10}")) {
            return "Mobile number must be exactly 10 digits.";
        }
        return null;
    }

    private void clearFields() {
        nameField.setText("");
        userField.setText("");
        passField.setText("");
        dobField.setText("");
        countryField.setText("");
        stateField.setText("");
        pinField.setText("");
        mobileField.setText("");
    }
}
