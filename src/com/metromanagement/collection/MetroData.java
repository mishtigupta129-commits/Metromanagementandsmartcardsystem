package com.metromanagement.collection;

import java.util.ArrayList;
import java.util.HashMap;

import com.metromanagement.model.Passenger;
import com.metromanagement.model.SmartCard;
import com.metromanagement.model.Station;
import com.metromanagement.model.Journey;

public class MetroData {

    public static ArrayList<Passenger> passengers =
            new ArrayList<>();

    public static HashMap<String, SmartCard> smartCards =
            new HashMap<>();

    public static ArrayList<Station> stations =
            new ArrayList<>();

    public static ArrayList<Journey> journeys =
            new ArrayList<>();
}