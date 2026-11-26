
package com.reservation.service;

import com.reservation.model.Train;
import com.reservation.repository.TrainRepository;

import java.sql.SQLException;
import java.util.List;

public class TrainService {

    private final TrainRepository trainRepository;

    public TrainService() {
        this(new TrainRepository());
    }

    public TrainService(TrainRepository trainRepository) {

        if (trainRepository == null) {
            throw new IllegalArgumentException(
                    "Train repository cannot be null.");
        }

        this.trainRepository = trainRepository;
    }

    // Add a new train after validation.
    public boolean addTrain(
            String trainNumber,
            String trainName,
            String source,
            String destination) throws SQLException {

        validateTrainDetails(
                trainNumber, trainName, source, destination);

        Train train = new Train(
                trainNumber.trim(),
                trainName.trim(),
                source.trim(),
                destination.trim());

        return trainRepository.addTrain(train);
    }

    // Retrieve all trains.
    public List<Train> getAllTrains() throws SQLException {
        return trainRepository.getAllTrains();
    }

    // Retrieve a train by ID.
    public Train getTrainById(int trainId) throws SQLException {

        if (trainId <= 0) {
            throw new IllegalArgumentException("Invalid train ID.");
        }

        return trainRepository.getTrainById(trainId);
    }

    // Update an existing train.
    public boolean updateTrain(
            int trainId,
            String trainNumber,
            String trainName,
            String source,
            String destination) throws SQLException {

        if (trainId <= 0) {
            throw new IllegalArgumentException("Invalid train ID.");
        }

        validateTrainDetails(
                trainNumber, trainName, source, destination);

        Train train = new Train(
                trainId,
                trainNumber.trim(),
                trainName.trim(),
                source.trim(),
                destination.trim());

        return trainRepository.updateTrain(train);
    }

    // Delete a train.
    public boolean deleteTrain(int trainId) throws SQLException {

        if (trainId <= 0) {
            throw new IllegalArgumentException("Invalid train ID.");
        }

        return trainRepository.deleteTrain(trainId);
    }

    // Validate train information.
    private void validateTrainDetails(
            String trainNumber,
            String trainName,
            String source,
            String destination) {

        if (isBlank(trainNumber)
                || isBlank(trainName)
                || isBlank(source)
                || isBlank(destination)) {

            throw new IllegalArgumentException(
                    "All train details are required.");
        }

        if (source.trim().equalsIgnoreCase(destination.trim())) {

            throw new IllegalArgumentException(
                    "Source and destination cannot be the same.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
