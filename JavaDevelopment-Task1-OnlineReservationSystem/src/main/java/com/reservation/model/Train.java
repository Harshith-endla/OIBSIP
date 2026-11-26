
package com.reservation.model;

public class Train {

    private int trainId;
    private String trainNumber;
    private String trainName;
    private String source;
    private String destination;

    public Train(
            int trainId,
            String trainNumber,
            String trainName,
            String source,
            String destination) {

        this.trainId = trainId;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
    }

    public Train(
            String trainNumber,
            String trainName,
            String source,
            String destination) {

        this(0, trainNumber, trainName, source, destination);
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

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    @Override
    public String toString() {
        return trainNumber + " - " + trainName;
    }
}
