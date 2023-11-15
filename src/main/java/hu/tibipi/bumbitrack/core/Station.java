package hu.tibipi.bumbitrack.core;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Station {
    private final Place place;
    private final boolean isBike;
    private final int numberID;
    private final String name;
    private final int bikeCapacity;
    private final int bikesAvailable;
    private ArrayList<Bike> bikes;

    Station(JSONObject json){
        place = new Place(json.getFloat("lng"), json.getFloat("lat"));
        isBike = json.getBoolean("bike");
        numberID = json.getInt("number");
        name = json.getString("name");

        bikeCapacity = json.getInt("bike_racks");
        bikesAvailable = json.getInt("bikes_available_to_rent");

        JSONArray bikeArray = json.getJSONArray("bike_list");
        bikes = new ArrayList<>();
        for(int i = 0; i < bikeArray.length(); i++){
            bikes.add(new Bike(bikeArray.getJSONObject(i)));
        }
    }

    //creates a virtual station that shouldn't be used
    Station(String name){
        this.name = name;

        place = null;
        isBike = false;
        numberID = -1;
        bikeCapacity = -1;
        bikesAvailable = -1;
        bikes = null;
    }

    public Place getPlace() {
        return place;
    }

    public boolean isBike() {
        return isBike;
    }

    public int getNumberID() {
        return numberID;
    }

    public String getName() {
        return name;
    }

    public int getBikeCapacity() {
        return bikeCapacity;
    }

    public int getBikesAvailable() {
        return bikesAvailable;
    }

    public List<Bike> getBikes() {
        return new ArrayList<>(bikes);
    }

    public int getBikesNumber() {
        return bikes.size();
    }

    public void setBikes(List<Bike> nBikes){
        bikes = new ArrayList<>(nBikes);
    }
}
