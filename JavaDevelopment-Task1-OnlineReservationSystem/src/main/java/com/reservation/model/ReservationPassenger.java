
package com.reservation.model;

public class ReservationPassenger {

    private int passengerId;
    private int reservationId;
    private int scheduleId;

    private String passengerName;
    private String dateOfBirth;
    private String gender;
    private String seatNumber;

    public ReservationPassenger(
            int passengerId,
            int reservationId,
            int scheduleId,
            String passengerName,
            String dateOfBirth,
            String gender,
            String seatNumber) {

        this.passengerId = passengerId;
        this.reservationId = reservationId;
        this.scheduleId = scheduleId;
        this.passengerName = passengerName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.seatNumber = seatNumber;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getSeatNumber() {
        return seatNumber;
    }
}
