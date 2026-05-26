package com.smartcity;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Password hashing helper using PBKDF2-HMAC-SHA256 (no external deps).
 *
 * Stored format in the DB (single string in the password column):
 *   pbkdf2_sha256${iterations}${base64Salt}${base64Hash}
 *
 * Verify with {@link #matches(String, String)}.
 */
public final class PasswordUtil {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000; // OWASP 2023 floor
    private static final int KEY_LENGTH = 256;     // bits
    private static final int SALT_LENGTH = 16;     // bytes
    private static final String SCHEME = "pbkdf2_sha256";

    private static final SecureRandom RNG = new SecureRandom();

    private PasswordUtil() {}

    /** Hash a fresh plaintext password for storage. */
    public static String hash(String plaintext) {
        if (plaintext == null) throw new IllegalArgumentException("password is null");
        byte[] salt = new byte[SALT_LENGTH];
        RNG.nextBytes(salt);
        byte[] hash = pbkdf2(plaintext.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        return SCHEME + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Constant-time check of plaintext against a stored hash.
     * Returns false if the stored value is malformed or the password is wrong.
     */
    public static boolean matches(String plaintext, String stored) {
        if (plaintext == null || stored == null) return false;
        String[] parts = stored.split("\\$");
        if (parts.length != 4 || !SCHEME.equals(parts[0])) return false;
        try {
            int iters = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = pbkdf2(plaintext.toCharArray(), salt, iters, expected.length * 8);
            return constantTimeEquals(expected, actual);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] pw, byte[] salt, int iters, int keyLenBits) {
        try {
            KeySpec spec = new PBEKeySpec(pw, salt, iters, keyLenBits);
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 unavailable on this JVM", e);
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) diff |= a[i] ^ b[i];
        return diff == 0;
    }
}
