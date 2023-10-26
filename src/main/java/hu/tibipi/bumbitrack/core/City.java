package hu.tibipi.bumbitrack.core;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class City {
    private final String name;
    private final Place place;
    private final ArrayList<Station> stations;

    City(JSONObject json, String countryName, String cityName) throws CityNotFoundException, CountryNotFoundException {
        JSONObject cityJSON = findCityInJSON(json, countryName, cityName);
        name = cityJSON.getString("name");
        place = new Place(cityJSON.getFloat("lng"), cityJSON.getFloat("lat"));

        JSONArray stationArray = cityJSON.getJSONArray("places");
        stations = new ArrayList<>();
        for(int i = 0; i < stationArray.length(); i++){
            stations.add(new Station(stationArray.getJSONObject(i)));
        }
    }

    private JSONObject findCityInJSON(JSONObject json, String countryName, String cityName) throws CityNotFoundException, CountryNotFoundException {
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
        return place;
    }

    public ArrayList<Station> getStations() {
        return stations;
    }
}

class CityNotFoundException extends Exception{

}
class CountryNotFoundException extends Exception{

}
