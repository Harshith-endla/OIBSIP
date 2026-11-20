
package com.reservation.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
