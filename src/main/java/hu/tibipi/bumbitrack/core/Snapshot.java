package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.time.LocalDateTime;
import java.util.List;

@CompiledJson
public class Snapshot {
    @JsonAttribute (name = "snapshot_time", nullable = false)
    private final LocalDateTime dateTime;
    @JsonAttribute (name = "countries")
    public final List<Country> countries;
    @JsonAttribute (ignore = true)
    private final String ID;


    Snapshot(List<Country> countries){
        dateTime = LocalDateTime.now();
        this.countries = countries;
        this.ID = createID(countries.get(0).getCountryName(), countries.get(0).getCity().getName());
    }

    Snapshot(List<Country> countries, LocalDateTime dateTime){
        this.dateTime = dateTime;
        this.countries = countries;
        this.ID = createID(countries.get(0).getCountryName(), countries.get(0).getCity().getName());
    }

    public static String createID(String countryName, String cityName){
        countryName = countryName.toLowerCase().replace(" ", "_");
        cityName = cityName.toLowerCase().replace(" ", "_");

        return countryName + "_" + cityName;
    }

    public LocalDateTime getDateTime(){
        return dateTime;
    }

    public City getCity() {
        return countries.get(0).getCity();
    }

    public String getID(){
        return ID;
    }
}
