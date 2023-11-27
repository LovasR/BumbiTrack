package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.dslplatform.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class City {
    private final String name;
    private final double lat;
    private final double lng;
    private final List<Station> stations;

    City(CityDTO dto){
        this.name = dto.name;
        this.lat = dto.lat;
        this.lng = dto.lng;
        stations = new ArrayList<>();
        for(Station.StationDTO stationDTO : dto.stations){
            stations.add(new Station(stationDTO));
        }
    }

    public String getName() {
        return name;
    }

    public Place getPlace() {
        return new Place(lng, lat);
    }

    public List<Station> getStations() {
        return stations;
    }
}


@CompiledJson
class CityDTO {
    @JsonValue
    public final String name;
    @JsonValue
    public final double lat;
    @JsonValue
    public final double lng;
    @JsonAttribute(name = "places")
    public final List<Station.StationDTO> stations;


    CityDTO(String name, double lat, double lng, List<Station.StationDTO> stations){
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.stations = stations;
    }
}

