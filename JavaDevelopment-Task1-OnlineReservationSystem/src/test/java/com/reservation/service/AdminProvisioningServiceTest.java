
package com.reservation.service;

import com.reservation.database.DatabaseSchema;
import com.reservation.model.User;
import com.reservation.repository.UserRepository;
import com.reservation.security.PasswordUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdminProvisioningServiceTest {

    private Connection connection;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {

        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        DatabaseSchema.createTables(connection);

        userRepository = new UserRepository(connection);
    }

    private Map<String, String> validEnvironment() {

        Map<String, String> environment = new HashMap<>();

        environment.put("RESERVATION_ADMIN_NAME", "System Administrator");
        environment.put("RESERVATION_ADMIN_EMAIL", "admin@example.com");
        environment.put("RESERVATION_ADMIN_PHONE", "9876543210");
        environment.put("RESERVATION_ADMIN_PASSWORD", "SecureAdmin123!");

        return environment;
    }

    @Test
    void shouldSkipProvisioningWhenAllVariablesAreAbsent() throws Exception {

        AdminProvisioningService service = new AdminProvisioningService(userRepository, Map.of());

        service.provisionAdmin();

        assertNull(userRepository.findByEmail("admin@example.com"));
    }

    @Test
    void shouldRejectIncompleteConfiguration() {

        Map<String, String> environment = validEnvironment();

        environment.remove("RESERVATION_ADMIN_PASSWORD");

        AdminProvisioningService service = new AdminProvisioningService(userRepository, environment);

        assertThrows(
                IllegalStateException.class,
                service::provisionAdmin);
    }

    @Test
    void shouldCreateAdministratorWithAdminRole() throws Exception {

        AdminProvisioningService service = new AdminProvisioningService(
                userRepository, validEnvironment());

        service.provisionAdmin();

        User admin = userRepository.findByEmail("admin@example.com");

        assertNotNull(admin);
        assertEquals("System Administrator", admin.getFullName());
        assertEquals("ADMIN", admin.getRole());
    }

    @Test
    void shouldNotCreateDuplicateAdministrator() throws Exception {

        AdminProvisioningService service = new AdminProvisioningService(
                userRepository, validEnvironment());

        service.provisionAdmin();
        service.provisionAdmin();

        User admin = userRepository.findByEmail("admin@example.com");

        assertNotNull(admin);
        assertEquals("ADMIN", admin.getRole());

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Users WHERE email = ?")) {

            statement.setString(1, "admin@example.com");

            try (var resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(1, resultSet.getInt(1));
            }
        }
    }

    @Test
    void shouldRejectExistingRegularUserEmail() throws Exception {

        userRepository.registerUser(
                "Regular User",
                "admin@example.com",
                "9876543210",
                "UserPassword123!".toCharArray());

        AdminProvisioningService service = new AdminProvisioningService(
                userRepository, validEnvironment());

        assertThrows(
                IllegalStateException.class,
                service::provisionAdmin);

        User existingUser = userRepository.findByEmail("admin@example.com");

        assertEquals("USER", existingUser.getRole());
    }

    @Test
    void shouldStoreAdministratorPasswordAsHash() throws Exception {

        AdminProvisioningService service = new AdminProvisioningService(
                userRepository, validEnvironment());

        service.provisionAdmin();

        User admin = userRepository.findByEmail("admin@example.com");

        assertNotEquals("SecureAdmin123!", admin.getPasswordHash());

        assertTrue(
                PasswordUtil.verifyPassword(
                        "SecureAdmin123!".toCharArray(),
                        admin.getPasswordHash()));
    }

    @Test
    void shouldRejectNullEnvironmentConfiguration() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new AdminProvisioningService(userRepository, null));
    }
}
