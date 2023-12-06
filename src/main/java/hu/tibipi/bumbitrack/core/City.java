package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.dslplatform.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a City with its name, latitude, longitude, and stations within the city.
 */
public class City {
    private final String name; // Name of the city
    private final double lat; // Latitude of the city
    private final double lng; // Longitude of the city
    private final List<Station> stations; // List of stations in the city

    /**
     * Constructs a City object using a Data Transfer Object (DTO).
     *
     * @param dto The DTO containing city information
     */
    City(CityDTO dto) {
        this.name = dto.name;
        this.lat = dto.lat;
        this.lng = dto.lng;
        stations = new ArrayList<>();
        for (Station.StationDTO stationDTO : dto.stations) {
            stations.add(new Station(stationDTO));
        }
    }

    /**
     * Retrieves the name of the city.
     *
     * @return The name of the city
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the geographical coordinates of the city as a Place object.
     *
     * @return The Place object representing the city's location
     */
    public Place getPlace() {
        return new Place(lng, lat);
    }

    /**
     * Retrieves the list of stations within the city.
     *
     * @return The list of stations in the city
     */
    public List<Station> getStations() {
        return stations;
    }

    /**
     * Data Transfer Object (DTO) representing city details.
     */
    @CompiledJson
    public static class CityDTO {
        @JsonValue
        public final String name; // Name of the city
        @JsonValue
        public final double lat; // Latitude of the city
        @JsonValue
        public final double lng; // Longitude of the city
        @JsonAttribute(name = "places")
        public final List<Station.StationDTO> stations; // List of station DTOs

        /**
         * Constructs a CityDTO object with city details.
         *
         * @param name     Name of the city
         * @param lat      Latitude of the city
         * @param lng      Longitude of the city
         * @param stations List of station DTOs within the city
         */
        CityDTO(String name, double lat, double lng, List<Station.StationDTO> stations) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
            this.stations = stations;
        }
    }
}
