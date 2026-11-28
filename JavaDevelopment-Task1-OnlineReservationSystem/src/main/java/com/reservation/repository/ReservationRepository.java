
package com.reservation.repository;

import com.reservation.database.DatabaseConnection;
import com.reservation.model.Reservation;
import com.reservation.model.ReservationPassenger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {

    // Creates a reservation and its passenger records
    // within a single database transaction.
    public boolean createReservation(
            Reservation reservation,
            List<ReservationPassenger> passengers)
            throws SQLException {

        if (reservation == null || passengers == null
                || passengers.isEmpty()) {
            throw new IllegalArgumentException(
                    "Reservation and passenger details are required.");
        }

        if (reservation.getPassengerCount() != passengers.size()) {
            throw new IllegalArgumentException(
                    "Passenger count does not match passenger details.");
        }

        String reservationSql = """
                INSERT INTO Reservations
                (pnr, user_id, schedule_id, booking_date,
                 passenger_count, total_fare, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        String passengerSql = """
                INSERT INTO Reservation_Passengers
                (reservation_id, schedule_id, passenger_name,
                 date_of_birth, gender, seat_number)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String seatUpdateSql = """
                UPDATE Train_Schedules
                SET available_seats = available_seats - ?
                WHERE schedule_id = ?
                AND available_seats >= ?
                """;

        String lastSeatSql = """
                SELECT COALESCE(
                    MAX(CAST(SUBSTR(seat_number, 2) AS INTEGER)),
                    0
                )
                FROM Reservation_Passengers
                WHERE schedule_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                // Reserve the required seats only if enough remain.
                try (PreparedStatement seatStatement = connection.prepareStatement(seatUpdateSql)) {

                    int passengerCount = passengers.size();

                    seatStatement.setInt(1, passengerCount);
                    seatStatement.setInt(2, reservation.getScheduleId());
                    seatStatement.setInt(3, passengerCount);

                    if (seatStatement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Not enough seats are available "
                                        + "for this booking.");
                    }
                }

                // Find the highest seat number already assigned
                // to this train schedule.
                int lastSeatNumber = 0;

                try (PreparedStatement seatNumberStatement = connection.prepareStatement(lastSeatSql)) {

                    seatNumberStatement.setInt(
                            1, reservation.getScheduleId());

                    try (ResultSet resultSet = seatNumberStatement.executeQuery()) {

                        if (resultSet.next()) {
                            lastSeatNumber = resultSet.getInt(1);
                        }
                    }
                }

                // Insert the reservation.
                int reservationId;

                try (PreparedStatement reservationStatement = connection.prepareStatement(
                        reservationSql,
                        Statement.RETURN_GENERATED_KEYS)) {

                    reservationStatement.setString(
                            1, reservation.getPnr());

                    reservationStatement.setInt(
                            2, reservation.getUserId());

                    reservationStatement.setInt(
                            3, reservation.getScheduleId());

                    reservationStatement.setString(
                            4, reservation.getBookingDate());

                    reservationStatement.setInt(
                            5, reservation.getPassengerCount());

                    reservationStatement.setInt(
                            6, reservation.getTotalFare());

                    reservationStatement.setString(
                            7, "BOOKED");

                    if (reservationStatement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Unable to create reservation.");
                    }

                    try (ResultSet keys = reservationStatement.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException(
                                    "Unable to retrieve reservation ID.");
                        }

                        reservationId = keys.getInt(1);
                    }
                }

                // Insert all passenger records with unique seat numbers.
                try (PreparedStatement passengerStatement = connection.prepareStatement(passengerSql)) {

                    for (int i = 0; i < passengers.size(); i++) {

                        ReservationPassenger passenger = passengers.get(i);

                        passengerStatement.setInt(1, reservationId);

                        passengerStatement.setInt(
                                2, reservation.getScheduleId());

                        passengerStatement.setString(
                                3, passenger.getPassengerName());

                        passengerStatement.setString(
                                4, passenger.getDateOfBirth());

                        passengerStatement.setString(
                                5, passenger.getGender());

                        int seatNumber = lastSeatNumber + i + 1;

                        passengerStatement.setString(
                                6, String.format("S%02d", seatNumber));

                        passengerStatement.addBatch();
                    }

                    passengerStatement.executeBatch();
                }

                connection.commit();

                return true;

            } catch (SQLException | RuntimeException exception) {

                connection.rollback();

                throw exception;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }

    // Retrieves all reservations for the administrator.
    public List<Reservation> getAllReservations()
            throws SQLException {

        List<Reservation> reservations = new ArrayList<>();

        String sql = """
                SELECT
                    r.reservation_id,
                    r.pnr,
                    r.user_id,
                    r.schedule_id,

                    GROUP_CONCAT(
                        rp.passenger_name, ', '
                    ) AS passenger_name,

                    t.train_number,
                    t.train_name,
                    t.source,
                    t.destination,

                    ts.journey_date,
                    ts.departure_time,
                    ts.arrival_time,

                    r.booking_date,
                    r.passenger_count,
                    r.total_fare,
                    r.status

                FROM Reservations r

                INNER JOIN Users u
                    ON r.user_id = u.user_id

                INNER JOIN Train_Schedules ts
                    ON r.schedule_id = ts.schedule_id

                INNER JOIN Trains t
                    ON ts.train_id = t.train_id

                LEFT JOIN Reservation_Passengers rp
                    ON r.reservation_id = rp.reservation_id
                    AND r.schedule_id = rp.schedule_id

                GROUP BY r.reservation_id

                ORDER BY
                    r.booking_date DESC,
                    r.reservation_id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Reservation reservation = new Reservation(
                        resultSet.getInt("reservation_id"),
                        resultSet.getString("pnr"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("schedule_id"),
                        resultSet.getString("passenger_name"),
                        resultSet.getString("train_number"),
                        resultSet.getString("train_name"),
                        resultSet.getString("source"),
                        resultSet.getString("destination"),
                        resultSet.getString("journey_date"),
                        resultSet.getString("departure_time"),
                        resultSet.getString("arrival_time"),
                        resultSet.getString("booking_date"),
                        resultSet.getInt("passenger_count"),
                        resultSet.getInt("total_fare"),
                        resultSet.getString("status"));

                reservations.add(reservation);
            }
        }

        return reservations;
    }
}
