package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.dslplatform.json.JsonValue;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a station object with specific details.
 */
public class Station {
    private final double lat; // Latitude coordinate of the station.
    private final double lng; // Longitude coordinate of the station.
    private final boolean isBike; // Indicates if the station is associated with bikes.
    private final int numberID; // Unique number ID of the station.
    private final String name; // Name of the station.
    private final int bikeCapacity; // Capacity of bike racks at the station.
    private final int bikesAvailable; // Number of bikes currently available at the station.
    private List<Bike> bikes; // List of bikes available at the station.

    /**
     * Constructs a Station object from the StationDTO.
     *
     * @param dto StationDTO object to create a Station from.
     */
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

    /**
     * Retrieves the location of the station as a Place object.
     *
     * @return Place object representing the station's location.
     */
    public Place getPlace() {
        return new Place(lng, lat);
    }
    /**
     * Checks if the station is associated with bikes.
     * @return True if the station is related to bikes, otherwise false.
     */
    public boolean isBike() {
        return isBike;
    }

    /**
     * Retrieves the unique number ID of the station.
     * @return The number ID of the station.
     */
    public int getNumberID() {
        return numberID;
    }

    /**
     * Retrieves the name of the station.
     * @return The name of the station.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the capacity of bike racks at the station.
     * @return The capacity of bike racks at the station.
     */
    public int getBikeCapacity() {
        return bikeCapacity;
    }

    /**
     * Retrieves the number of bikes currently available at the station.
     * @return The number of bikes available at the station.
     */
    public int getBikesAvailable() {
        return bikesAvailable;
    }

    /**
     * Retrieves the list of bikes available at the station.
     * @return A new list containing the bikes available at the station.
     */
    public List<Bike> getBikes() {
        return new ArrayList<>(bikes);
    }

    /**
     * Retrieves the number of bikes available at the station.
     *
     * @return The number of bikes available.
     */
    public int getBikesNumber() {
        return bikes.size();
    }

    public void setBikes(List<Bike> nBikes){
        bikes = new ArrayList<>(nBikes);
    }

    /**
     * DTO class representing Station data.
     */
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
