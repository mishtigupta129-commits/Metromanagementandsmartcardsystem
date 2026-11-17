package com.metromanagement.model;

public class Journey {

    private String journeyId;
    private String passengerId;
    private String source;
    private String destination;
    private String date;
    private double fare;

    public Journey(
            String journeyId,
            String passengerId,
            String source,
            String destination,
            String date,
            double fare) {

        this.journeyId = journeyId;
        this.passengerId = passengerId;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.fare = fare;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public double getFare() {
        return fare;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }
}