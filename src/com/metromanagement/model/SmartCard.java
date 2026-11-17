package com.metromanagement.model;

public class SmartCard {

    private String cardNumber;
    private String passengerId;
    private double balance;

    public SmartCard(String cardNumber, String passengerId, double balance) {
        this.cardNumber = cardNumber;
        this.passengerId = passengerId;
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public double getBalance() {
        return balance;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}