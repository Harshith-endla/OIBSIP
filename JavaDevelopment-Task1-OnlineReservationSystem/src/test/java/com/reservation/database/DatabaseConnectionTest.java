
package com.reservation.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseConnectionTest {

    @Test
    void shouldEstablishConnectionAndEnableForeignKeys()
            throws Exception {

        try (Connection connection = DatabaseConnection.getConnection()) {

            // Verify that the connection was established.
            assertNotNull(connection);

            // Check whether foreign-key enforcement is enabled.
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("PRAGMA foreign_keys")) {

                resultSet.next();

                assertEquals(1, resultSet.getInt(1));
            }
        }
    }
}
