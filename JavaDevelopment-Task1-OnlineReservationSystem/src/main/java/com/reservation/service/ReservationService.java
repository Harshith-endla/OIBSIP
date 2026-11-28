
package com.reservation.service;

import com.reservation.model.Reservation;
import com.reservation.model.ReservationPassenger;
import com.reservation.model.Train;
import com.reservation.model.TrainSchedule;
import com.reservation.model.User;
import com.reservation.repository.ReservationRepository;
import com.reservation.repository.TrainRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TrainScheduleService scheduleService;
    private final TrainRepository trainRepository;

    public ReservationService() {
        this(
                new ReservationRepository(),
                new TrainScheduleService(),
                new TrainRepository());
    }

    public ReservationService(
            ReservationRepository reservationRepository) {

        this(
                reservationRepository,
                new TrainScheduleService(),
                new TrainRepository());
    }

    public ReservationService(
            ReservationRepository reservationRepository,
            TrainScheduleService scheduleService,
            TrainRepository trainRepository) {

        if (reservationRepository == null) {
            throw new IllegalArgumentException(
                    "Reservation repository cannot be null.");
        }

        if (scheduleService == null) {
            throw new IllegalArgumentException(
                    "Schedule service cannot be null.");
        }

        if (trainRepository == null) {
            throw new IllegalArgumentException(
                    "Train repository cannot be null.");
        }

        this.reservationRepository = reservationRepository;
        this.scheduleService = scheduleService;
        this.trainRepository = trainRepository;
    }

    // Creates a reservation after validating all booking details.
    public Reservation createReservation(
            User user,
            int scheduleId,
            List<ReservationPassenger> passengers)
            throws SQLException {

        validateBookingRequest(user, scheduleId, passengers);

        List<ReservationPassenger> validatedPassengers = validatePassengers(passengers);

        TrainSchedule schedule = scheduleService.getScheduleById(scheduleId);

        if (schedule == null) {
            throw new IllegalArgumentException(
                    "Selected train schedule does not exist.");
        }

        LocalDate journeyDate;

        try {
            journeyDate = LocalDate.parse(schedule.getJourneyDate());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Invalid journey date in train schedule.");
        }

        if (journeyDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Cannot book tickets for a past journey date.");
        }

        int passengerCount = validatedPassengers.size();

        if (schedule.getAvailableSeats() < passengerCount) {
            throw new IllegalArgumentException(
                    "Only " + schedule.getAvailableSeats()
                            + " seats are currently available.");
        }

        Train train = trainRepository.getTrainById(
                schedule.getTrainId());

        if (train == null) {
            throw new IllegalArgumentException(
                    "Train associated with this schedule does not exist.");
        }

        int totalFare;

        try {
            totalFare = Math.multiplyExact(
                    schedule.getFarePerSeat(),
                    passengerCount);
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                    "Total fare exceeds the supported amount.");
        }

        String pnr = generatePnr();
        String bookingDate = LocalDate.now().toString();

        List<String> passengerNames = new ArrayList<>();

        for (ReservationPassenger passenger : validatedPassengers) {
            passengerNames.add(passenger.getPassengerName());
        }

        Reservation reservation = new Reservation(
                0,
                pnr,
                user.getUserId(),
                scheduleId,
                String.join(", ", passengerNames),
                schedule.getTrainNumber(),
                schedule.getTrainName(),
                train.getSource(),
                train.getDestination(),
                schedule.getJourneyDate(),
                schedule.getDepartureTime(),
                schedule.getArrivalTime(),
                bookingDate,
                passengerCount,
                totalFare,
                "BOOKED");

        boolean created = reservationRepository.createReservation(
                reservation,
                validatedPassengers);

        if (!created) {
            throw new SQLException(
                    "Unable to complete the reservation.");
        }

        return reservation;
    }

    // Validates the booking request.
    private void validateBookingRequest(
            User user,
            int scheduleId,
            List<ReservationPassenger> passengers) {

        if (user == null || user.getUserId() <= 0) {
            throw new IllegalArgumentException(
                    "A valid logged-in user is required.");
        }

        if (!"USER".equals(user.getRole())) {
            throw new IllegalArgumentException(
                    "Only registered users can book tickets.");
        }

        if (scheduleId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a valid train schedule.");
        }

        if (passengers == null
                || passengers.isEmpty()
                || passengers.size() > 7) {

            throw new IllegalArgumentException(
                    "A reservation must contain between 1 and 7 passengers.");
        }
    }

    // Validates and normalizes individual passenger details.
    private List<ReservationPassenger> validatePassengers(
            List<ReservationPassenger> passengers) {

        List<ReservationPassenger> validatedPassengers = new ArrayList<>();

        for (ReservationPassenger passenger : passengers) {

            if (passenger == null) {
                throw new IllegalArgumentException(
                        "Passenger details cannot be empty.");
            }

            String name = passenger.getPassengerName();

            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException(
                        "Passenger name is required.");
            }

            String dateOfBirth = passenger.getDateOfBirth();

            if (dateOfBirth == null || dateOfBirth.isBlank()) {
                throw new IllegalArgumentException(
                        "Passenger date of birth is required.");
            }

            LocalDate birthDate;

            try {
                birthDate = LocalDate.parse(dateOfBirth.trim());
            } catch (DateTimeParseException exception) {
                throw new IllegalArgumentException(
                        "Date of birth must be in yyyy-MM-dd format.");
            }

            if (birthDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Date of birth cannot be in the future.");
            }

            String gender = passenger.getGender();

            if (gender == null || gender.isBlank()) {
                throw new IllegalArgumentException(
                        "Passenger gender is required.");
            }

            gender = gender.trim().toUpperCase(Locale.ROOT);

            if (!gender.equals("MALE")
                    && !gender.equals("FEMALE")
                    && !gender.equals("OTHER")) {

                throw new IllegalArgumentException(
                        "Gender must be MALE, FEMALE, or OTHER.");
            }

            validatedPassengers.add(
                    new ReservationPassenger(
                            0,
                            0,
                            0,
                            name.trim(),
                            birthDate.toString(),
                            gender,
                            null));
        }

        return validatedPassengers;
    }

    // Generates a unique booking reference.
    private String generatePnr() {

        return "PNR" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase(Locale.ROOT);
    }

    // Retrieves all reservations for the administrator.
    public List<Reservation> getAllReservations()
            throws SQLException {

        return reservationRepository.getAllReservations();
    }
}
