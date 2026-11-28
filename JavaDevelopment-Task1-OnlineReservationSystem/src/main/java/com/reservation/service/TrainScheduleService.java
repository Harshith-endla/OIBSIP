
package com.reservation.service;

import com.reservation.model.Train;
import com.reservation.model.TrainSchedule;
import com.reservation.repository.TrainRepository;
import com.reservation.repository.TrainScheduleRepository;
import com.reservation.util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TrainScheduleService {

    private final TrainScheduleRepository scheduleRepository;
    private final TrainRepository trainRepository;

    public TrainScheduleService() {
        this(new TrainScheduleRepository(), new TrainRepository());
    }

    public TrainScheduleService(
            TrainScheduleRepository scheduleRepository,
            TrainRepository trainRepository) {

        if (scheduleRepository == null) {
            throw new IllegalArgumentException(
                    "Schedule repository cannot be null.");
        }

        if (trainRepository == null) {
            throw new IllegalArgumentException(
                    "Train repository cannot be null.");
        }

        this.scheduleRepository = scheduleRepository;
        this.trainRepository = trainRepository;
    }

    // Add a new train schedule after validation.
    public boolean addSchedule(
            int trainId,
            String journeyDate,
            String departureTime,
            String arrivalTime,
            int totalSeats,
            int farePerSeat) throws SQLException {

        validateScheduleDetails(
                trainId,
                journeyDate,
                departureTime,
                arrivalTime,
                totalSeats,
                farePerSeat);

        Train train = trainRepository.getTrainById(trainId);

        if (train == null) {
            throw new IllegalArgumentException(
                    "Selected train does not exist.");
        }

        TrainSchedule schedule = new TrainSchedule(
                0,
                trainId,
                train.getTrainNumber(),
                train.getTrainName(),
                journeyDate.trim(),
                departureTime.trim(),
                arrivalTime.trim(),
                totalSeats,
                totalSeats,
                farePerSeat);

        return scheduleRepository.addSchedule(schedule);
    }

    // Retrieve all train schedules.
    public List<TrainSchedule> getAllSchedules() throws SQLException {
        return scheduleRepository.getAllSchedules();
    }

    // Retrieve future schedules with available seats for ticket booking.
    public List<TrainSchedule> getAvailableSchedulesForBooking()
            throws SQLException {

        return scheduleRepository.getAvailableSchedulesForBooking();
    }

    // Retrieve a schedule by ID.
    public TrainSchedule getScheduleById(int scheduleId)
            throws SQLException {

        if (scheduleId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid schedule ID.");
        }

        return scheduleRepository.getScheduleById(scheduleId);
    }

    // Update an existing schedule while preserving booked seats.
    public boolean updateSchedule(
            int scheduleId,
            int trainId,
            String journeyDate,
            String departureTime,
            String arrivalTime,
            int totalSeats,
            int farePerSeat) throws SQLException {

        if (scheduleId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid schedule ID.");
        }

        validateScheduleDetails(
                trainId,
                journeyDate,
                departureTime,
                arrivalTime,
                totalSeats,
                farePerSeat);

        TrainSchedule existingSchedule = scheduleRepository.getScheduleById(scheduleId);

        if (existingSchedule == null) {
            throw new IllegalArgumentException(
                    "Schedule does not exist.");
        }

        Train train = trainRepository.getTrainById(trainId);

        if (train == null) {
            throw new IllegalArgumentException(
                    "Selected train does not exist.");
        }

        int bookedSeats = existingSchedule.getTotalSeats()
                - existingSchedule.getAvailableSeats();

        if (totalSeats < bookedSeats) {
            throw new IllegalArgumentException(
                    "Total seats cannot be less than the number "
                            + "of already booked seats (" + bookedSeats + ").");
        }

        int availableSeats = totalSeats - bookedSeats;

        TrainSchedule updatedSchedule = new TrainSchedule(
                scheduleId,
                trainId,
                train.getTrainNumber(),
                train.getTrainName(),
                journeyDate.trim(),
                departureTime.trim(),
                arrivalTime.trim(),
                totalSeats,
                availableSeats,
                farePerSeat);

        return scheduleRepository.updateSchedule(updatedSchedule);
    }

    // Delete a schedule.
    public boolean deleteSchedule(int scheduleId) throws SQLException {

        if (scheduleId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid schedule ID.");
        }

        TrainSchedule schedule = scheduleRepository.getScheduleById(scheduleId);

        if (schedule == null) {
            throw new IllegalArgumentException(
                    "Schedule does not exist.");
        }

        return scheduleRepository.deleteSchedule(scheduleId);
    }

    // Validate schedule information.
    private void validateScheduleDetails(
            int trainId,
            String journeyDate,
            String departureTime,
            String arrivalTime,
            int totalSeats,
            int farePerSeat) {

        if (trainId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid train.");
        }

        if (ValidationUtil.isBlank(journeyDate)
                || ValidationUtil.isBlank(departureTime)
                || ValidationUtil.isBlank(arrivalTime)) {

            throw new IllegalArgumentException(
                    "Journey date and both timings are required.");
        }

        if (totalSeats <= 0) {
            throw new IllegalArgumentException(
                    "Total seats must be greater than zero.");
        }

        if (farePerSeat < 0) {
            throw new IllegalArgumentException(
                    "Fare cannot be negative.");
        }

        validateDate(journeyDate.trim());
        validateTime(departureTime.trim(), "Departure");
        validateTime(arrivalTime.trim(), "Arrival");
    }

    // Validate ISO date format (yyyy-MM-dd).
    private void validateDate(String journeyDate) {

        try {
            LocalDate.parse(journeyDate);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Journey date must be in yyyy-MM-dd format.");
        }
    }

    // Validate time format (HH:mm or HH:mm:ss).
    private void validateTime(String time, String fieldName) {

        try {
            LocalTime.parse(time);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    fieldName + " time must be in HH:mm format.");
        }
    }
}
