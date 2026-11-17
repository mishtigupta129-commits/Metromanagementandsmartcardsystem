package com.metromanagement.model;

public class Passenger {

    private String passengerId;
    private String name;
    private String email;
    private String phone;
    private String username;
    private String password;

    // Constructor for Registration / Login
    public Passenger(
            String name,
            String email,
            String phone,
            String username,
            String password) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.password = password;
    }

    // Constructor for Passenger Management
    public Passenger(
            String passengerId,
            String name,
            String phone,
            String email) {

        this.passengerId = passengerId;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Getters

    public String getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setters

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
