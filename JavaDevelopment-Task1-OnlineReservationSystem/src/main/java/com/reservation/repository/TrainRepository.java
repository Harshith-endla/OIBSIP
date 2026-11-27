
package com.reservation.repository;

import com.reservation.database.DatabaseConnection;
import com.reservation.model.Train;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrainRepository {

    // Add a new train.
    public boolean addTrain(Train train) throws SQLException {

        String sql = """
                INSERT INTO Trains
                (train_number, train_name, source, destination)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, train.getTrainNumber());
            statement.setString(2, train.getTrainName());
            statement.setString(3, train.getSource());
            statement.setString(4, train.getDestination());

            return statement.executeUpdate() == 1;
        }
    }

    // Retrieve all trains.
    public List<Train> getAllTrains() throws SQLException {

        List<Train> trains = new ArrayList<>();

        String sql = """
                SELECT train_id, train_number, train_name, source, destination
                FROM Trains
                ORDER BY train_number
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                trains.add(mapTrain(resultSet));
            }
        }

        return trains;
    }

    // Find a train using its ID.
    public Train getTrainById(int trainId) throws SQLException {

        String sql = """
                SELECT train_id, train_number, train_name, source, destination
                FROM Trains
                WHERE train_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, trainId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapTrain(resultSet);
                }
            }
        }

        return null;
    }

    // Check whether a train has any associated schedules.
    public boolean hasSchedulesForTrain(int trainId) throws SQLException {

        String sql = """
                SELECT 1
                FROM Train_Schedules
                WHERE train_id = ?
                LIMIT 1
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, trainId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    // Update existing train details.
    public boolean updateTrain(Train train) throws SQLException {

        String sql = """
                UPDATE Trains
                SET train_number = ?,
                    train_name = ?,
                    source = ?,
                    destination = ?
                WHERE train_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, train.getTrainNumber());
            statement.setString(2, train.getTrainName());
            statement.setString(3, train.getSource());
            statement.setString(4, train.getDestination());
            statement.setInt(5, train.getTrainId());

            return statement.executeUpdate() == 1;
        }
    }

    // Delete a train using its ID.
    public boolean deleteTrain(int trainId) throws SQLException {

        String sql = "DELETE FROM Trains WHERE train_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, trainId);

            return statement.executeUpdate() == 1;
        }
    }

    // Convert a database row into a Train object.
    private Train mapTrain(ResultSet resultSet) throws SQLException {

        return new Train(
                resultSet.getInt("train_id"),
                resultSet.getString("train_number"),
                resultSet.getString("train_name"),
                resultSet.getString("source"),
                resultSet.getString("destination"));
    }
}
