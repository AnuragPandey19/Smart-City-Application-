package com.smartcity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Holds the active MySQL credentials and hands out JDBC connections.
 *
 * Two modes:
 *   - {@link #getServerConnection()} — connects to the MySQL <i>server</i> with
 *     no specific schema selected. Used during bootstrap to test the password
 *     and to create the database.
 *   - {@link #getConnection()} — connects to the {@code smartcity} database.
 *     Used by every panel at runtime.
 *
 * Credential file location: {@code ~/.smartcity/db.properties}. The file is
 * created on first successful sign-in via {@link #saveCredentials}.
 *
 * For dev work an alternate {@code db.properties} in the working directory
 * is also honored — handy for IDE runs.
 */
public final class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    public static final String DATABASE = "smartcity";
    public static final String HOST     = "localhost";
    public static final int    PORT     = 3306;

    /** Where saved credentials live in production. */
    public static final Path CREDENTIALS_PATH =
            Paths.get(System.getProperty("user.home"), ".smartcity", "db.properties");

    /** Optional dev override — when you run from the IDE with a db.properties next to the project. */
    private static final Path DEV_OVERRIDE = Paths.get("db.properties");

    private static volatile String username;
    private static volatile String password;

    private DBConnection() {}

    /* =====================  CREDENTIAL MANAGEMENT  ===================== */

    /**
     * Tries to load credentials from disk. Honours the dev override
     * (./db.properties) first, then the user-home file.
     * @return true if credentials are now in memory; false if no file was found.
     */
    public static synchronized boolean tryLoadCredentials() {
        for (Path p : new Path[]{DEV_OVERRIDE, CREDENTIALS_PATH}) {
            if (p == null || !Files.exists(p)) continue;
            try (InputStream in = Files.newInputStream(p)) {
                Properties props = new Properties();
                props.load(in);
                String u = props.getProperty("db.user");
                String pw = props.getProperty("db.password");
                if (u != null && pw != null) {
                    username = u;
                    password = pw;
                    return true;
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to read " + p, e);
            }
        }
        return false;
    }

    /** Persist credentials to ~/.smartcity/db.properties. Also sets them in-memory. */
    public static synchronized void saveCredentials(String user, String pass) throws IOException {
        Files.createDirectories(CREDENTIALS_PATH.getParent());
        Properties props = new Properties();
        props.setProperty("db.user", user);
        props.setProperty("db.password", pass);
        try (OutputStream out = Files.newOutputStream(CREDENTIALS_PATH)) {
            props.store(out, "Smart City — saved MySQL credentials");
        }
        // Best-effort tighten file permissions on POSIX systems (no-op on Windows).
        try {
            Files.setPosixFilePermissions(CREDENTIALS_PATH,
                    java.util.EnumSet.of(
                        java.nio.file.attribute.PosixFilePermission.OWNER_READ,
                        java.nio.file.attribute.PosixFilePermission.OWNER_WRITE));
        } catch (UnsupportedOperationException | IOException ignored) { }

        username = user;
        password = pass;
    }

    public static String getUsername() { return username; }

    /* =====================  CONNECTIONS  ===================== */

    /** Connect to the MySQL server with no schema selected. */
    public static Connection getServerConnection() throws SQLException {
        loadDriver();
        return DriverManager.getConnection(serverUrl(), username, password);
    }

    /** Connect to the {@code smartcity} schema. Returns null on failure. */
    public static Connection getConnection() {
        try {
            loadDriver();
            return DriverManager.getConnection(databaseUrl(), username, password);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection to smartcity DB failed", e);
            return null;
        }
    }

    /* =====================  BOOTSTRAP HELPERS  ===================== */

    /** Quick "can these credentials reach the server?" probe. */
    public static boolean testServerConnection(String user, String pass) {
        try {
            loadDriver();
            try (Connection c = DriverManager.getConnection(serverUrl(), user, pass)) {
                return c != null;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /** Quick "can the currently-loaded credentials reach the server?" probe. */
    public static boolean testServerConnection() {
        if (username == null || password == null) return false;
        return testServerConnection(username, password);
    }

    /** Does the smartcity database exist on the server? */
    public static boolean databaseExists() {
        try (Connection c = getServerConnection()) {
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT 1 FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = ?")) {
                ps.setString(1, DATABASE);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "databaseExists check failed", e);
            return false;
        }
    }

    /* =====================  URLs / DRIVER  ===================== */

    private static String serverUrl() {
        return "jdbc:mysql://" + HOST + ":" + PORT
             + "/?useSSL=false&serverTimezone=UTC&allowMultiQueries=true";
    }

    private static String databaseUrl() {
        return "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
             + "?useSSL=false&serverTimezone=UTC";
    }

    private static void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "MySQL JDBC driver not on classpath — bundle mysql-connector-j with the app.", e);
        }
    }
}
