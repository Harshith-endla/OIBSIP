
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
    void shouldMigratePassengerAgeToDateOfBirth() throws Exception {

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {

            // Enable foreign keys for this test connection.
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");

                // Simulate the old passenger table structure.
                statement.execute("""
                        CREATE TABLE Reservation_Passengers (
                            passenger_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            reservation_id INTEGER NOT NULL,
                            schedule_id INTEGER NOT NULL,
                            passenger_name TEXT NOT NULL,
                            age INTEGER NOT NULL,
                            gender TEXT NOT NULL
                                CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
                            seat_number TEXT,

                            UNIQUE (schedule_id, seat_number),

                            FOREIGN KEY (reservation_id, schedule_id)
                                REFERENCES Reservations(
                                    reservation_id, schedule_id)
                        )
                        """);
            }

            // Run the schema initialization and migration.
            DatabaseSchema.createTables(connection);

            Set<String> actualColumns = new HashSet<>();

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(
                            "PRAGMA table_info(Reservation_Passengers)")) {

                while (resultSet.next()) {
                    actualColumns.add(resultSet.getString("name"));
                }
            }

            // Verify that the new DOB column exists.
            assertTrue(actualColumns.contains("date_of_birth"));

            // Verify that the old age column no longer exists.
            assertFalse(actualColumns.contains("age"));

            // Verify that the other passenger columns remain.
            assertTrue(actualColumns.contains("passenger_id"));
            assertTrue(actualColumns.contains("reservation_id"));
            assertTrue(actualColumns.contains("schedule_id"));
            assertTrue(actualColumns.contains("passenger_name"));
            assertTrue(actualColumns.contains("gender"));
            assertTrue(actualColumns.contains("seat_number"));

            // Verify that foreign-key enforcement is restored.
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
}
