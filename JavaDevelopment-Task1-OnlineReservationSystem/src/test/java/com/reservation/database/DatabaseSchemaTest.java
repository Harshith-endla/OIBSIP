
package com.reservation.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseSchemaTest {

    @Test
    void shouldCreateAllFiveTables() throws Exception {

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {

            // Enable foreign keys for this test connection.
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }

            // Use the actual production database schema.
            DatabaseSchema.createTables(connection);

            Set<String> actualTables = new HashSet<>();

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("""
                            SELECT name
                            FROM sqlite_master
                            WHERE type = 'table'
                            AND name NOT LIKE 'sqlite_%'
                            """)) {

                while (resultSet.next()) {
                    actualTables.add(resultSet.getString("name"));
                }
            }

            Set<String> expectedTables = Set.of(
                    "Users",
                    "Trains",
                    "Train_Schedules",
                    "Reservations",
                    "Reservation_Passengers");

            assertEquals(expectedTables, actualTables);
        }
    }

    @Test
    void shouldCreatePassengerTableWithDateOfBirth() throws Exception {

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {

            // Enable foreign keys for this test connection.
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }

            // Create tables using the current database schema.
            DatabaseSchema.createTables(connection);

            Set<String> actualColumns = new HashSet<>();

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(
                            "PRAGMA table_info(Reservation_Passengers)")) {

                while (resultSet.next()) {
                    actualColumns.add(resultSet.getString("name"));
                }
            }

            // Verify the current passenger table structure.
            assertTrue(actualColumns.contains("date_of_birth"));
            assertFalse(actualColumns.contains("age"));

            assertTrue(actualColumns.contains("passenger_id"));
            assertTrue(actualColumns.contains("reservation_id"));
            assertTrue(actualColumns.contains("schedule_id"));
            assertTrue(actualColumns.contains("passenger_name"));
            assertTrue(actualColumns.contains("gender"));
            assertTrue(actualColumns.contains("seat_number"));

            // Verify that foreign-key enforcement is enabled.
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(
                            "PRAGMA foreign_keys")) {

                assertTrue(resultSet.next());
                assertEquals(1, resultSet.getInt(1));
            }

            // Verify that no foreign-key violations exist.
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(
                            "PRAGMA foreign_key_check")) {

                assertFalse(resultSet.next());
            }
        }
    }

    @Test
    void shouldMigrateExistingUsersToDefaultUserRole() throws Exception {

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {

            // Simulate the old Users table without a role column.
            try (Statement statement = connection.createStatement()) {

                statement.execute("""
                        CREATE TABLE Users (
                            user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            full_name TEXT NOT NULL,
                            email TEXT NOT NULL UNIQUE,
                            phone TEXT NOT NULL,
                            password_hash TEXT NOT NULL
                        )
                        """);

                // Insert a user into the old table.
                statement.execute("""
                        INSERT INTO Users (
                            full_name,
                            email,
                            phone,
                            password_hash
                        )
                        VALUES (
                            'Test User',
                            'test@example.com',
                            '9876543210',
                            'test-password-hash'
                        )
                        """);
            }

            // Run schema initialization and role migration.
            DatabaseSchema.createTables(connection);

            // Verify that the existing user was preserved.
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("""
                            SELECT user_id, full_name, email, phone, password_hash, role
                            FROM Users
                            WHERE email = 'test@example.com'
                            """)) {

                assertTrue(resultSet.next());

                assertEquals(1, resultSet.getInt("user_id"));
                assertEquals("Test User", resultSet.getString("full_name"));
                assertEquals("test@example.com", resultSet.getString("email"));
                assertEquals("9876543210", resultSet.getString("phone"));
                assertEquals("test-password-hash",
                        resultSet.getString("password_hash"));
                assertEquals("USER", resultSet.getString("role"));

                assertFalse(resultSet.next());
            }

            // Run schema initialization again to verify migration safety.
            DatabaseSchema.createTables(connection);

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("""
                            SELECT COUNT(*)
                            FROM Users
                            WHERE email = 'test@example.com'
                            AND role = 'USER'
                            """)) {

                assertTrue(resultSet.next());
                assertEquals(1, resultSet.getInt(1));
            }
        }
    }
}
