package com.metromanagement.model;

public class Station {

    private String stationId;
    private String stationName;
    private String line;
    private String location;

    public Station(String stationId, String stationName,
                   String line, String location) {

        this.stationId = stationId;
        this.stationName = stationName;
        this.line = line;
        this.location = location;
    }

    public String getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public String getLine() {
        return line;
    }

    public String getLocation() {
        return location;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
