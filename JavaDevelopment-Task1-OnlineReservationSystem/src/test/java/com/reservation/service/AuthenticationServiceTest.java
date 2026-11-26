
package com.reservation.service;

import com.reservation.database.DatabaseSchema;
import com.reservation.model.User;
import com.reservation.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {

    private Connection connection;
    private UserRepository userRepository;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() throws SQLException {

        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        DatabaseSchema.createTables(connection);

        userRepository = new UserRepository(connection);

        authenticationService = new AuthenticationService(userRepository);
    }

    @AfterEach
    void tearDown() throws SQLException {

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Registers a test user before authentication tests.
    private void registerTestUser() throws Exception {

        userRepository.registerUser(
                "Test User",
                "test@example.com",
                "9876543210",
                "SecurePassword123".toCharArray());
    }

    // ---------------- AUTHENTICATION TESTS ----------------

    @Test
    void shouldLoginWithCorrectCredentials() throws Exception {

        registerTestUser();

        User authenticatedUser = authenticationService.login(
                "test@example.com",
                "SecurePassword123".toCharArray());

        assertNotNull(authenticatedUser);
        assertEquals("Test User", authenticatedUser.getFullName());
        assertEquals("test@example.com", authenticatedUser.getEmail());
        assertEquals("USER", authenticatedUser.getRole());
    }

    @Test
    void shouldLoginAdministratorWithCorrectCredentials() throws Exception {

        registerTestUser();

        // Simulate an administrator account created by
        // the authorized admin provisioning process.
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    UPDATE Users
                    SET role = 'ADMIN'
                    WHERE email = 'test@example.com'
                    """);
        }

        User authenticatedUser = authenticationService.login(
                "test@example.com",
                "SecurePassword123".toCharArray());

        assertNotNull(authenticatedUser);
        assertEquals("ADMIN", authenticatedUser.getRole());
    }

    @Test
    void shouldRejectIncorrectPassword() throws Exception {

        registerTestUser();

        User authenticatedUser = authenticationService.login(
                "test@example.com",
                "WrongPassword123".toCharArray());

        assertNull(authenticatedUser);
    }

    @Test
    void shouldRejectUnknownEmail() throws Exception {

        User authenticatedUser = authenticationService.login(
                "unknown@example.com",
                "SecurePassword123".toCharArray());

        assertNull(authenticatedUser);
    }

    @Test
    void shouldRejectEmptyEmail() throws Exception {

        User authenticatedUser = authenticationService.login(
                " ",
                "SecurePassword123".toCharArray());

        assertNull(authenticatedUser);
    }

    @Test
    void shouldRejectEmptyPassword() throws Exception {

        User authenticatedUser = authenticationService.login(
                "test@example.com",
                new char[0]);

        assertNull(authenticatedUser);
    }

    @Test
    void shouldRejectNullCredentials() throws Exception {

        assertNull(authenticationService.login(
                null,
                "SecurePassword123".toCharArray()));

        assertNull(authenticationService.login(
                "test@example.com",
                null));

        assertNull(authenticationService.login(
                null,
                null));
    }

    // ---------------- REGISTRATION TESTS ----------------

    @Test
    void shouldRegisterUserWithValidDetails() throws Exception {

        User registeredUser = authenticationService.registerUser(
                "New Test User",
                "newuser@example.com",
                "9876543210",
                "SecurePassword123".toCharArray());

        assertNotNull(registeredUser);
        assertTrue(registeredUser.getUserId() > 0);
        assertEquals("New Test User", registeredUser.getFullName());
        assertEquals("newuser@example.com", registeredUser.getEmail());
        assertEquals("9876543210", registeredUser.getPhone());
        assertEquals("USER", registeredUser.getRole());

        User savedUser = userRepository.findByEmail("newuser@example.com");

        assertNotNull(savedUser);
        assertEquals(registeredUser.getUserId(), savedUser.getUserId());
        assertEquals("USER", savedUser.getRole());
    }

    @Test
    void shouldTrimRegistrationTextFields() throws Exception {

        User registeredUser = authenticationService.registerUser(
                "  New Test User  ",
                "  newuser@example.com  ",
                "  9876543210  ",
                "SecurePassword123".toCharArray());

        assertEquals("New Test User", registeredUser.getFullName());
        assertEquals("newuser@example.com", registeredUser.getEmail());
        assertEquals("9876543210", registeredUser.getPhone());
    }

    @Test
    void shouldRejectBlankFullName() {

        assertThrows(IllegalArgumentException.class, () -> authenticationService.registerUser(
                " ",
                "newuser@example.com",
                "9876543210",
                "SecurePassword123".toCharArray()));
    }

    @Test
    void shouldRejectBlankEmail() {

        assertThrows(IllegalArgumentException.class, () -> authenticationService.registerUser(
                "New Test User",
                " ",
                "9876543210",
                "SecurePassword123".toCharArray()));
    }

    @Test
    void shouldRejectBlankPhone() {

        assertThrows(IllegalArgumentException.class, () -> authenticationService.registerUser(
                "New Test User",
                "newuser@example.com",
                " ",
                "SecurePassword123".toCharArray()));
    }

    @Test
    void shouldRejectNullPassword() {

        assertThrows(IllegalArgumentException.class, () -> authenticationService.registerUser(
                "New Test User",
                "newuser@example.com",
                "9876543210",
                null));
    }

    @Test
    void shouldRejectEmptyPasswordDuringRegistration() {

        assertThrows(IllegalArgumentException.class, () -> authenticationService.registerUser(
                "New Test User",
                "newuser@example.com",
                "9876543210",
                new char[0]));
    }

    @Test
    void shouldRejectDuplicateEmailDuringRegistration() throws Exception {

        registerTestUser();

        assertThrows(SQLException.class, () -> authenticationService.registerUser(
                "Another User",
                "test@example.com",
                "9876543211",
                "AnotherPassword123".toCharArray()));
    }
}
