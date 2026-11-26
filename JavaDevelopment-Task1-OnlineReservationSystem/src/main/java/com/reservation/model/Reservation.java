
package com.reservation.model;

public class Reservation {

    private int reservationId;
    private String pnr;
    private int userId;
    private int scheduleId;

    private String passengerName;
    private String trainNumber;
    private String trainName;

    private String source;
    private String destination;

    private String journeyDate;
    private String departureTime;
    private String arrivalTime;

    private String bookingDate;
    private int passengerCount;
    private int totalFare;
    private String status;

    public Reservation(
            int reservationId,
            String pnr,
            int userId,
            int scheduleId,
            String passengerName,
            String trainNumber,
            String trainName,
            String source,
            String destination,
            String journeyDate,
            String departureTime,
            String arrivalTime,
            String bookingDate,
            int passengerCount,
            int totalFare,
            String status) {

        this.reservationId = reservationId;
        this.pnr = pnr;
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.journeyDate = journeyDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.bookingDate = bookingDate;
        this.passengerCount = passengerCount;
        this.totalFare = totalFare;
        this.status = status;
    }

    public int getReservationId() {
        return reservationId;
    }

    public String getPnr() {
        return pnr;
    }

    public int getUserId() {
        return userId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public int getTotalFare() {
        return totalFare;
    }

    public String getStatus() {
        return status;
    }
}
