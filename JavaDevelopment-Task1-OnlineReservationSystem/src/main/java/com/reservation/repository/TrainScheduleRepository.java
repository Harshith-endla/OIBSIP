
package com.reservation.repository;

import com.reservation.database.DatabaseConnection;
import com.reservation.model.TrainSchedule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrainScheduleRepository {

    // Add a new train schedule.
    public boolean addSchedule(TrainSchedule schedule) throws SQLException {

        String sql = """
                INSERT INTO Train_Schedules
                (train_id, journey_date, departure_time, arrival_time,
                 total_seats, available_seats, fare_per_seat)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, schedule.getTrainId());
            statement.setString(2, schedule.getJourneyDate());
            statement.setString(3, schedule.getDepartureTime());
            statement.setString(4, schedule.getArrivalTime());
            statement.setInt(5, schedule.getTotalSeats());
            statement.setInt(6, schedule.getTotalSeats());
            statement.setInt(7, schedule.getFarePerSeat());

            return statement.executeUpdate() == 1;
        }
    }

    // Retrieve all train schedules with their train details.
    public List<TrainSchedule> getAllSchedules() throws SQLException {

        List<TrainSchedule> schedules = new ArrayList<>();

        String sql = """
                SELECT ts.*, t.train_number, t.train_name
                FROM Train_Schedules ts
                INNER JOIN Trains t ON ts.train_id = t.train_id
                ORDER BY ts.journey_date, ts.departure_time
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                schedules.add(mapSchedule(resultSet));
            }
        }

        return schedules;
    }

    // Retrieve future train schedules that have available seats for booking.
    public List<TrainSchedule> getAvailableSchedulesForBooking()
            throws SQLException {

        List<TrainSchedule> schedules = new ArrayList<>();

        String sql = """
                SELECT
                    ts.schedule_id,
                    ts.train_id,
                    ts.journey_date,
                    ts.departure_time,
                    ts.arrival_time,
                    ts.total_seats,
                    ts.available_seats,
                    ts.fare_per_seat,
                    t.train_number,
                    t.train_name
                FROM Train_Schedules ts
                INNER JOIN Trains t
                    ON ts.train_id = t.train_id
                WHERE ts.journey_date >= date('now', 'localtime')
                  AND ts.available_seats > 0
                ORDER BY ts.journey_date, ts.departure_time
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                schedules.add(mapSchedule(resultSet));
            }
        }

        return schedules;
    }

    // Find a schedule using its ID.
    public TrainSchedule getScheduleById(int scheduleId) throws SQLException {

        String sql = """
                SELECT ts.*, t.train_number, t.train_name
                FROM Train_Schedules ts
                INNER JOIN Trains t ON ts.train_id = t.train_id
                WHERE ts.schedule_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, scheduleId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapSchedule(resultSet);
                }
            }
        }

        return null;
    }

    // Update an existing train schedule.
    public boolean updateSchedule(TrainSchedule schedule) throws SQLException {

        String sql = """
                UPDATE Train_Schedules
                SET train_id = ?,
                    journey_date = ?,
                    departure_time = ?,
                    arrival_time = ?,
                    total_seats = ?,
                    available_seats = ?,
                    fare_per_seat = ?
                WHERE schedule_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, schedule.getTrainId());
            statement.setString(2, schedule.getJourneyDate());
            statement.setString(3, schedule.getDepartureTime());
            statement.setString(4, schedule.getArrivalTime());
            statement.setInt(5, schedule.getTotalSeats());

            // Preserve the current available-seat count.
            statement.setInt(6, schedule.getAvailableSeats());

            statement.setInt(7, schedule.getFarePerSeat());
            statement.setInt(8, schedule.getScheduleId());

            return statement.executeUpdate() == 1;
        }
    }

    // Delete a schedule using its ID.
    public boolean deleteSchedule(int scheduleId) throws SQLException {

        String sql = """
                DELETE FROM Train_Schedules
                WHERE schedule_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, scheduleId);

            return statement.executeUpdate() == 1;
        }
    }

    // Convert a database row into a TrainSchedule object.
    private TrainSchedule mapSchedule(ResultSet resultSet) throws SQLException {

        return new TrainSchedule(
                resultSet.getInt("schedule_id"),
                resultSet.getInt("train_id"),
                resultSet.getString("train_number"),
                resultSet.getString("train_name"),
                resultSet.getString("journey_date"),
                resultSet.getString("departure_time"),
                resultSet.getString("arrival_time"),
                resultSet.getInt("total_seats"),
                resultSet.getInt("available_seats"),
                resultSet.getInt("fare_per_seat"));
    }
}
