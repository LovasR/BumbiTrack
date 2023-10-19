import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Station {
    Place place;
    boolean isBike;
    int numberID;
    String name;
    int bikeCapacity;
    int bikesAvailable;
    ArrayList<Bike> bikes;

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
}
