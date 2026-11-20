
package com.reservation.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatabaseConstraintTest {

    @Test
    void shouldRejectDuplicateUserEmail() throws Exception {

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {

            DatabaseSchema.createTables(connection);

            String sql = """
                    INSERT INTO Users
                        (full_name, email, phone, password_hash)
                    VALUES (?, ?, ?, ?)
                    """;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, "Test User");
                statement.setString(2, "test@example.com");
                statement.setString(3, "9876543210");
                statement.setString(4, "sample_hash");

                statement.executeUpdate();

                statement.setString(1, "Another User");
                statement.setString(2, "test@example.com");
                statement.setString(3, "9876543211");
                statement.setString(4, "another_hash");

                assertThrows(
                        SQLException.class,
                        statement::executeUpdate);
            }
        }
    }

    @Test
    void shouldRejectDuplicateTrainNumber() throws Exception {

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {

            DatabaseSchema.createTables(connection);

            String sql = """
                    INSERT INTO Trains
                        (train_number, train_name, source, destination)
                    VALUES (?, ?, ?, ?)
                    """;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setString(1, "12701");
                statement.setString(2, "Test Express");
                statement.setString(3, "Hyderabad");
                statement.setString(4, "Chennai");

                statement.executeUpdate();

                statement.setString(1, "12701");
                statement.setString(2, "Another Express");
                statement.setString(3, "Hyderabad");
                statement.setString(4, "Bangalore");

                assertThrows(
                        SQLException.class,
                        statement::executeUpdate);
            }
        }
    }

    @Test
    void shouldRejectZeroTotalSeats() throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            assertInvalidSchedule(connection, "2026-12-01", 0, 0);
        }
    }

    @Test
    void shouldRejectNegativeAvailableSeats() throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            assertInvalidSchedule(connection, "2026-12-02", 100, -1);
        }
    }

    @Test
    void shouldRejectAvailableSeatsGreaterThanTotalSeats()
            throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            assertInvalidSchedule(connection, "2026-12-03", 100, 101);
        }
    }

    @Test
    void shouldRejectPassengerCountBelowOne() throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            assertInvalidReservation(connection, 0, 10000, "BOOKED");
        }
    }

    @Test
    void shouldRejectPassengerCountAboveSeven() throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            assertInvalidReservation(connection, 8, 10000, "BOOKED");
        }
    }

    @Test
    void shouldRejectNegativeTotalFare() throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            assertInvalidReservation(connection, 2, -1, "BOOKED");
        }
    }

    @Test
    void shouldRejectInvalidReservationStatus() throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            assertInvalidReservation(connection, 2, 10000, "PENDING");
        }
    }

    @Test
    void shouldRejectPassengerAgeBelowMinimum() throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            createValidReservation(connection, 1);

            assertInvalidPassenger(
                    connection, "Test Passenger", 0, "MALE", "S1");
        }
    }

    @Test
    void shouldRejectPassengerAgeAboveMaximum() throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            createValidReservation(connection, 1);

            assertInvalidPassenger(
                    connection, "Test Passenger", 121, "MALE", "S1");
        }
    }

    @Test
    void shouldRejectInvalidPassengerGender() throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            createValidReservation(connection, 1);

            assertInvalidPassenger(
                    connection, "Test Passenger", 25, "INVALID", "S1");
        }
    }

    @Test
    void shouldRejectDuplicateSeatNumberForSameSchedule()
            throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            createValidReservation(connection, 2);

            insertPassenger(
                    connection, "First Passenger", 25, "MALE", "S1");

            assertThrows(
                    SQLException.class,
                    () -> insertPassenger(
                            connection, "Second Passenger", 30, "FEMALE", "S1"));
        }
    }

    @Test
    void shouldAcceptValidPassengerRecord() throws Exception {

        try (Connection connection = createConnectionWithTestData()) {

            createValidReservation(connection, 1);

            int rowsInserted = insertPassenger(
                    connection, "Test Passenger", 25, "MALE", "S1");

            assertEquals(1, rowsInserted);
        }
    }

    // Creates an in-memory database with a test user, train, and schedule.
    private Connection createConnectionWithTestData()
            throws SQLException {

        Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try {

            DatabaseSchema.createTables(connection);

            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }

            String userSql = """
                    INSERT INTO Users
                        (user_id, full_name, email, phone, password_hash)
                    VALUES
                        (1, 'Test User', 'test@example.com',
                         '9876543210', 'sample_hash')
                    """;

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(userSql);
            }

            String trainSql = """
                    INSERT INTO Trains
                        (train_id, train_number, train_name, source, destination)
                    VALUES
                        (1, '12701', 'Test Express', 'Hyderabad', 'Chennai')
                    """;

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(trainSql);
            }

            String scheduleSql = """
                    INSERT INTO Train_Schedules
                        (schedule_id, train_id, journey_date,
                         departure_time, arrival_time, total_seats,
                         available_seats, fare_per_seat)
                    VALUES
                        (1, 1, '2026-11-30', '08:00', '12:00',
                         100, 100, 10000)
                    """;

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(scheduleSql);
            }

            return connection;

        } catch (SQLException e) {

            connection.close();
            throw e;
        }
    }

    // Attempts to insert a schedule with the supplied seat values.
    private void assertInvalidSchedule(
            Connection connection,
            String journeyDate,
            int totalSeats,
            int availableSeats) throws SQLException {

        String sql = """
                INSERT INTO Train_Schedules
                    (train_id, journey_date, departure_time, arrival_time,
                     total_seats, available_seats, fare_per_seat)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, 1);
            statement.setString(2, journeyDate);
            statement.setString(3, "08:00");
            statement.setString(4, "12:00");
            statement.setInt(5, totalSeats);
            statement.setInt(6, availableSeats);
            statement.setInt(7, 10000);

            assertThrows(
                    SQLException.class,
                    statement::executeUpdate);
        }
    }

    // Attempts to insert a reservation with the supplied values.
    private void assertInvalidReservation(
            Connection connection,
            int passengerCount,
            int totalFare,
            String status) throws SQLException {

        String sql = """
                INSERT INTO Reservations
                    (pnr, user_id, schedule_id, booking_date,
                     passenger_count, total_fare, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "PNR123456");
            statement.setInt(2, 1);
            statement.setInt(3, 1);
            statement.setString(4, "2026-11-19");
            statement.setInt(5, passengerCount);
            statement.setInt(6, totalFare);
            statement.setString(7, status);

            assertThrows(
                    SQLException.class,
                    statement::executeUpdate);
        }
    }

    // Creates a valid reservation for passenger constraint tests.
    private void createValidReservation(
            Connection connection,
            int passengerCount) throws SQLException {

        String sql = """
                INSERT INTO Reservations
                    (pnr, user_id, schedule_id, booking_date,
                     passenger_count, total_fare, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "PNR123456");
            statement.setInt(2, 1);
            statement.setInt(3, 1);
            statement.setString(4, "2026-11-19");
            statement.setInt(5, passengerCount);
            statement.setInt(6, passengerCount * 10000);
            statement.setString(7, "BOOKED");

            statement.executeUpdate();
        }
    }

    // Inserts a passenger and returns the number of inserted rows.
    private int insertPassenger(
            Connection connection,
            String passengerName,
            int age,
            String gender,
            String seatNumber) throws SQLException {

        String sql = """
                INSERT INTO Reservation_Passengers
                    (reservation_id, schedule_id, passenger_name,
                     age, gender, seat_number)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, 1);
            statement.setInt(2, 1);
            statement.setString(3, passengerName);
            statement.setInt(4, age);
            statement.setString(5, gender);
            statement.setString(6, seatNumber);

            return statement.executeUpdate();
        }
    }

    // Attempts to insert a passenger with invalid values.
    private void assertInvalidPassenger(
            Connection connection,
            String passengerName,
            int age,
            String gender,
            String seatNumber) {

        assertThrows(
                SQLException.class,
                () -> insertPassenger(
                        connection,
                        passengerName,
                        age,
                        gender,
                        seatNumber));
    }
}
