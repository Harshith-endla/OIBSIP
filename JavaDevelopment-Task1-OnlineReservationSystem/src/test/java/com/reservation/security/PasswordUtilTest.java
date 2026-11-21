
package com.reservation.security;

import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void shouldGeneratePasswordHash() throws GeneralSecurityException {

        char[] password = "SecurePassword123".toCharArray();

        String hashedPassword = PasswordUtil.hashPassword(password);

        assertNotNull(hashedPassword);
        assertFalse(hashedPassword.isBlank());
        assertNotEquals("SecurePassword123", hashedPassword);
    }

    @Test
    void shouldVerifyCorrectPassword() throws GeneralSecurityException {

        char[] password = "SecurePassword123".toCharArray();

        String hashedPassword = PasswordUtil.hashPassword(password);

        assertTrue(
                PasswordUtil.verifyPassword(password, hashedPassword));
    }

    @Test
    void shouldRejectIncorrectPassword() throws GeneralSecurityException {

        char[] password = "SecurePassword123".toCharArray();

        String hashedPassword = PasswordUtil.hashPassword(password);

        char[] incorrectPassword = "WrongPassword123".toCharArray();

        assertFalse(
                PasswordUtil.verifyPassword(
                        incorrectPassword, hashedPassword));
    }

    @Test
    void shouldGenerateDifferentHashesForSamePassword()
            throws GeneralSecurityException {

        char[] password = "SecurePassword123".toCharArray();

        String firstHash = PasswordUtil.hashPassword(password);
        String secondHash = PasswordUtil.hashPassword(password);

        assertNotEquals(firstHash, secondHash);
    }

    @Test
    void shouldRejectInvalidStoredHash() throws GeneralSecurityException {

        char[] password = "SecurePassword123".toCharArray();

        assertFalse(
                PasswordUtil.verifyPassword(
                        password, "invalid-stored-hash"));
    }

    @Test
    void shouldRejectNullStoredHash() throws GeneralSecurityException {

        char[] password = "SecurePassword123".toCharArray();

        assertFalse(
                PasswordUtil.verifyPassword(password, null));
    }
}
