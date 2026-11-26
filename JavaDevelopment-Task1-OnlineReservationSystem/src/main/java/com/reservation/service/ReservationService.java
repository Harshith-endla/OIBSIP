
package com.reservation.service;

import com.reservation.model.Reservation;
import com.reservation.repository.ReservationRepository;

import java.sql.SQLException;
import java.util.List;

public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService() {
        this(new ReservationRepository());
    }

    public ReservationService(
            ReservationRepository reservationRepository) {

        if (reservationRepository == null) {
            throw new IllegalArgumentException(
                    "Reservation repository cannot be null.");
        }

        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getAllReservations()
            throws SQLException {

        return reservationRepository.getAllReservations();
    }
}
