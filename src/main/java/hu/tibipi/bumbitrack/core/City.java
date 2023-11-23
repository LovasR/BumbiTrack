package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import com.dslplatform.json.JsonValue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@CompiledJson
public class City {
    @JsonValue
    private final String name;
    @JsonValue
    public final double lat;
    @JsonValue
    public final double lng;
    @JsonAttribute (name = "places")
    public final List<Station> stations;


    City(JSONObject json, String countryName, String cityName) throws CityNotFoundException, CountryNotFoundException {
        JSONObject cityJSON = findCityInJSON(json, countryName, cityName);
        name = cityJSON.getString("name");
        lat = 0;
        lng = 0;

        JSONArray stationArray = cityJSON.getJSONArray("places");
        stations = new ArrayList<>();
        for(int i = 0; i < stationArray.length(); i++){
            stations.add(new Station(stationArray.getJSONObject(i)));
        }
    }

    City(String name, double lat, double lng, List<Station> stations){
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.stations = stations;
    }

    private JSONObject findCityInJSON(JSONObject json, String countryName, String cityName)
            throws CityNotFoundException, CountryNotFoundException {
        JSONArray countries = json.getJSONArray("countries");

        JSONObject countryJSON = null;
        for(Object country : countries){
            if(((JSONObject) country).getString("name").equals(countryName)){
                countryJSON = (JSONObject) country;
            }
        }
        if(countryJSON == null){
            throw new CountryNotFoundException();
        }


        JSONArray cities = countryJSON.getJSONArray("cities");
        for(Object city : cities){
            if(((JSONObject) city).getString("name").equals(cityName)){
                return (JSONObject) city;
            }
        }
        throw new CityNotFoundException();
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


    public static class CityNotFoundException extends Exception{}
    public static class CountryNotFoundException extends Exception{}
}

