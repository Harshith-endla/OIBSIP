
package com.reservation.repository;

import com.reservation.database.DatabaseSchema;
import com.reservation.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private Connection connection;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws SQLException {

        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        DatabaseSchema.createTables(connection);

        userRepository = new UserRepository(connection);
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        char[] password = "SecurePassword123".toCharArray();

        User user = userRepository.registerUser(
                "Harshith Varma",
                "harshith@example.com",
                "9876543210",
                password);

        assertNotNull(user);
        assertTrue(user.getUserId() > 0);
        assertEquals("Harshith Varma", user.getFullName());
        assertEquals("harshith@example.com", user.getEmail());
        assertEquals("USER", user.getRole());
    }

    @Test
    void shouldStoreHashedPasswordInsteadOfPlaintext()
            throws Exception {

        char[] password = "SecurePassword123".toCharArray();

        User user = userRepository.registerUser(
                "Test User",
                "test@example.com",
                "9876543210",
                password);

        assertNotEquals(
                "SecurePassword123",
                user.getPasswordHash());
    }

    @Test
    void shouldFindUserByEmail() throws Exception {

        char[] password = "SecurePassword123".toCharArray();

        userRepository.registerUser(
                "Test User",
                "test@example.com",
                "9876543210",
                password);

        User foundUser = userRepository.findByEmail("test@example.com");

        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
        assertEquals("Test User", foundUser.getFullName());
        assertEquals("USER", foundUser.getRole());
    }

    @Test
    void shouldRetrieveStoredAdminRole() throws Exception {

        char[] password = "SecurePassword123".toCharArray();

        userRepository.registerUser(
                "Admin User",
                "admin@example.com",
                "9876543210",
                password);

        // Simulate an administrator role assigned through
        // the authorized admin provisioning process.
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    UPDATE Users
                    SET role = 'ADMIN'
                    WHERE email = 'admin@example.com'
                    """);
        }

        User foundUser = userRepository.findByEmail("admin@example.com");

        assertNotNull(foundUser);
        assertEquals("ADMIN", foundUser.getRole());
    }

    @Test
    void shouldReturnNullForUnknownEmail() throws Exception {

        User foundUser = userRepository.findByEmail("unknown@example.com");

        assertNull(foundUser);
    }

    @Test
    void shouldRejectDuplicateEmailRegistration() throws Exception {

        char[] firstPassword = "FirstPassword123".toCharArray();
        char[] secondPassword = "SecondPassword123".toCharArray();

        userRepository.registerUser(
                "First User",
                "duplicate@example.com",
                "9876543210",
                firstPassword);

        assertThrows(
                SQLException.class,
                () -> userRepository.registerUser(
                        "Second User",
                        "duplicate@example.com",
                        "9876543211",
                        secondPassword));
    }
}
