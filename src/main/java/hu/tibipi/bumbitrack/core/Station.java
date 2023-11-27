package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.dslplatform.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class Station {
    private final double lat;
    private final double lng;
    private final boolean isBike;
    private final int numberID;
    private final String name;
    private final int bikeCapacity;
    private final int bikesAvailable;
    private List<Bike> bikes;

    public Station(StationDTO dto){
        this.name = dto.name;
        this.isBike = dto.isBike;
        this.numberID = dto.numberID;
        this.bikeCapacity = dto.bikeCapacity;
        this.bikesAvailable = dto.bikesAvailable;
        this.lat = dto.lat;
        this.lng = dto.lng;

        bikes = new ArrayList<>();
        for(Bike.BikeDTO bikeDTO : dto.bikes){
            bikes.add(new Bike(bikeDTO));
        }
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

    @CompiledJson
    public static class StationDTO {
        @JsonValue
        public final double lat;
        @JsonValue
        public final double lng;
        @JsonAttribute(name = "bike")
        public final boolean isBike;
        @JsonAttribute(name = "number")
        public final int numberID;
        @JsonValue
        public final String name;
        @JsonAttribute(name = "bike_racks")
        public final int bikeCapacity;
        @JsonAttribute(name = "bikes_available_to_rent")
        public final int bikesAvailable;
        @JsonAttribute(name = "bike_list")
        public List<Bike.BikeDTO> bikes;

        StationDTO(String name, boolean isBike, int numberID, int bikeCapacity, int bikesAvailable, double lat, double lng, List<Bike.BikeDTO> bikes){
            this.name = name;
            this.isBike = isBike;
            this.numberID = numberID;
            this.bikeCapacity = bikeCapacity;
            this.bikesAvailable = bikesAvailable;
            this.lat = lat;
            this.lng = lng;
            this.bikes = bikes;
        }
    }
}
