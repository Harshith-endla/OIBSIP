
package com.reservation.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DATABASE_URL = "jdbc:sqlite:database/reservation.db";

    public static Connection getConnection() throws SQLException {

        Connection connection = DriverManager.getConnection(DATABASE_URL);

        try (Statement statement = connection.createStatement()) {

            // Enable foreign-key enforcement for every connection.
            statement.execute("PRAGMA foreign_keys = ON");

            return connection;

        } catch (SQLException e) {

            connection.close();
            throw e;
        }
    }
}
