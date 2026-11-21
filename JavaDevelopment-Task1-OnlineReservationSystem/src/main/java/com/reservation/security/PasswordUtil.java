package com.reservation.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import java.util.Base64;

public class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    private static final int ITERATIONS = 210000;

    private static final int SALT_LENGTH = 16;

    private static final int KEY_LENGTH = 256;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Converts a password into a salted hash.
    public static String hashPassword(char[] password)
            throws GeneralSecurityException {

        byte[] salt = new byte[SALT_LENGTH];

        SECURE_RANDOM.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                ITERATIONS,
                KEY_LENGTH);

        try {

            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

            byte[] hash = factory.generateSecret(spec).getEncoded();

            String encodedSalt = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(salt);

            String encodedHash = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

            return ALGORITHM + "$"
                    + ITERATIONS + "$"
                    + encodedSalt + "$"
                    + encodedHash;

        } finally {

            spec.clearPassword();
        }
    }

    // Checks whether the supplied password matches the stored hash.
    public static boolean verifyPassword(
            char[] password,
            String storedHash) throws GeneralSecurityException {

        if (password == null || storedHash == null) {
            return false;
        }

        String[] parts = storedHash.split("\\$", -1);

        if (parts.length != 4 || !ALGORITHM.equals(parts[0])) {
            return false;
        }

        try {

            int iterations = Integer.parseInt(parts[1]);

            if (iterations <= 0) {
                return false;
            }

            byte[] salt = Base64.getUrlDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getUrlDecoder().decode(parts[3]);

            if (salt.length != SALT_LENGTH
                    || expectedHash.length != KEY_LENGTH / 8) {
                return false;
            }

            PBEKeySpec spec = new PBEKeySpec(
                    password,
                    salt,
                    iterations,
                    KEY_LENGTH);

            try {

                SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);

                byte[] actualHash = factory.generateSecret(spec).getEncoded();

                return MessageDigest.isEqual(expectedHash, actualHash);

            } finally {

                spec.clearPassword();
            }

        } catch (IllegalArgumentException e) {

            return false;
        }
    }
}
