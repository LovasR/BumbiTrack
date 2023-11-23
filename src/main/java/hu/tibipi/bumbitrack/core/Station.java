package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.dslplatform.json.JsonValue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@CompiledJson
public class Station {
    @JsonValue
    public final double lat;
    @JsonValue
    public final double lng;
    @JsonAttribute (name = "bike")
    private final boolean isBike;
    @JsonAttribute (name = "number")
    private final int numberID;
    @JsonValue
    private final String name;
    @JsonAttribute (name = "bike_racks")
    private final int bikeCapacity;
    @JsonAttribute (name = "bikes_available_to_rent")
    private final int bikesAvailable;
    @JsonAttribute (name = "bike_list")
    public List<Bike> bikes;

    Station(JSONObject json){
        lat = 0;
        lng = 0;
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

    Station(String name, boolean isBike, int numberID, int bikeCapacity, int bikesAvailable, double lat, double lng, List<Bike> bikes){
        this.name = name;
        this.isBike = isBike;
        this.numberID = numberID;
        this.bikeCapacity = bikeCapacity;
        this.bikesAvailable = bikesAvailable;
        this.lat = lat;
        this.lng = lng;
        this.bikes = bikes;
    }

    //creates a virtual station that shouldn't be used
    Station(String name){
        this.name = name;

        lat = 0;
        lng = 0;
        isBike = false;
        numberID = -1;
        bikeCapacity = -1;
        bikesAvailable = -1;
        bikes = null;
    }

    Station(Station stationToCopy){
        this.name = stationToCopy.name;
        this.lat = stationToCopy.lat;
        this.lng = stationToCopy.lng;
        this.bikesAvailable = stationToCopy.bikesAvailable;
        this.numberID = stationToCopy.numberID;
        this.isBike = stationToCopy.isBike;
        this.bikeCapacity = stationToCopy.bikeCapacity;

        this.bikes = new ArrayList<>(stationToCopy.bikes);
    }

    public Place getPlace() {
        return new Place(lng, lat);
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
