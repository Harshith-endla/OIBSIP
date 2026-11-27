
package com.reservation.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSchema {

    // Creates tables using the application's database connection.
    public static void createTables() throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection()) {
            createTables(connection);
        }
    }

    // Creates tables using a connection supplied by the caller.
    public static void createTables(Connection connection)
            throws SQLException {

        try (Statement statement = connection.createStatement()) {

            // 1. Users table
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS Users (
                        user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        full_name TEXT NOT NULL,
                        email TEXT NOT NULL UNIQUE,
                        phone TEXT NOT NULL,
                        password_hash TEXT NOT NULL,
                        role TEXT NOT NULL DEFAULT 'USER'
                            CHECK (role IN ('ADMIN', 'USER'))
                    )
                    """);

            // 2. Trains table
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS Trains (
                        train_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        train_number TEXT NOT NULL UNIQUE,
                        train_name TEXT NOT NULL,
                        source TEXT NOT NULL,
                        destination TEXT NOT NULL,
                        CHECK (source <> destination)
                    )
                    """);

            // 3. Train_Schedules table
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS Train_Schedules (
                        schedule_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        train_id INTEGER NOT NULL,
                        journey_date TEXT NOT NULL,
                        departure_time TEXT NOT NULL,
                        arrival_time TEXT NOT NULL,
                        total_seats INTEGER NOT NULL CHECK (total_seats > 0),
                        available_seats INTEGER NOT NULL,
                        fare_per_seat INTEGER NOT NULL CHECK (fare_per_seat >= 0),

                        CHECK (
                            available_seats >= 0
                            AND available_seats <= total_seats
                        ),

                        UNIQUE (train_id, journey_date),

                        FOREIGN KEY (train_id)
                            REFERENCES Trains(train_id)
                    )
                    """);

            // 4. Reservations table
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS Reservations (
                        reservation_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        pnr TEXT NOT NULL UNIQUE,
                        user_id INTEGER NOT NULL,
                        schedule_id INTEGER NOT NULL,
                        booking_date TEXT NOT NULL,
                        passenger_count INTEGER NOT NULL
                            CHECK (passenger_count BETWEEN 1 AND 7),
                        total_fare INTEGER NOT NULL CHECK (total_fare >= 0),
                        status TEXT NOT NULL DEFAULT 'BOOKED'
                            CHECK (status IN ('BOOKED', 'CANCELLED')),

                        UNIQUE (reservation_id, schedule_id),

                        FOREIGN KEY (user_id)
                            REFERENCES Users(user_id),

                        FOREIGN KEY (schedule_id)
                            REFERENCES Train_Schedules(schedule_id)
                    )
                    """);

            // 5. Reservation_Passengers table
            // Date of birth is stored instead of age because age changes
            // over time, whereas date of birth remains constant.
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS Reservation_Passengers (
                        passenger_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        reservation_id INTEGER NOT NULL,
                        schedule_id INTEGER NOT NULL,
                        passenger_name TEXT NOT NULL,
                        date_of_birth TEXT NOT NULL,
                        gender TEXT NOT NULL
                            CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
                        seat_number TEXT,

                        UNIQUE (schedule_id, seat_number),

                        FOREIGN KEY (reservation_id, schedule_id)
                            REFERENCES Reservations(reservation_id, schedule_id)
                    )
                    """);
        }

        // Upgrade existing databases when necessary.
        migrateUserRole(connection);
    }

    // Checks whether a table contains a particular column.
    private static boolean hasColumn(
            Connection connection,
            String tableName,
            String columnName) throws SQLException {

        String sql = "PRAGMA table_info(" + tableName + ")";

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                if (columnName.equalsIgnoreCase(
                        resultSet.getString("name"))) {
                    return true;
                }
            }
        }

        return false;
    }

    // Adds the role column to older Users tables.
    private static void migrateUserRole(
            Connection connection) throws SQLException {

        if (hasColumn(connection, "Users", "role")) {
            return;
        }

        try (Statement statement = connection.createStatement()) {

            statement.execute("""
                    ALTER TABLE Users
                    ADD COLUMN role TEXT NOT NULL DEFAULT 'USER'
                    CHECK (role IN ('ADMIN', 'USER'))
                    """);
        }
    }
}
