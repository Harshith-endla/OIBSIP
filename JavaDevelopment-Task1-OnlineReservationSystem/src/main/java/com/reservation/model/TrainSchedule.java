
package com.reservation.model;

public class TrainSchedule {

    private int scheduleId;
    private int trainId;
    private String trainNumber;
    private String trainName;
    private String journeyDate;
    private String departureTime;
    private String arrivalTime;
    private int totalSeats;
    private int availableSeats;
    private int farePerSeat;

    public TrainSchedule(
            int scheduleId,
            int trainId,
            String trainNumber,
            String trainName,
            String journeyDate,
            String departureTime,
            String arrivalTime,
            int totalSeats,
            int availableSeats,
            int farePerSeat) {

        this.scheduleId = scheduleId;
        this.trainId = trainId;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.journeyDate = journeyDate;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.farePerSeat = farePerSeat;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public int getTrainId() {
        return trainId;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
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

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public int getFarePerSeat() {
        return farePerSeat;
    }
}
