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
    private final ArrayList<Bike> bikes;

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
}
