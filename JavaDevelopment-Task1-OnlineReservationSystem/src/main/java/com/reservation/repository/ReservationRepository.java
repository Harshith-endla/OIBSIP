
package com.reservation.repository;

import com.reservation.database.DatabaseConnection;
import com.reservation.model.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {

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
