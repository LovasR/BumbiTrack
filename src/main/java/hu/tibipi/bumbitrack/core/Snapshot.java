package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Snapshot {
    private final LocalDateTime dateTime;
    private final List<Country> countries;
    private final String ID;


    Snapshot(List<Country> countries){
        dateTime = LocalDateTime.now();
        this.countries = countries;
        this.ID = createID(countries.get(0).getCountryName(), countries.get(0).getCity().getName());
    }

    public Snapshot(SnapshotDTO dataTransferObject){
        this.dateTime = dataTransferObject.dateTime;
        countries = new ArrayList<>();
        for(Country.CountryDTO countryDTO : dataTransferObject.countries){
            countries.add(new Country(countryDTO));
        }
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

    @CompiledJson
    public static class SnapshotDTO{
        @JsonAttribute (name = "snapshot_time", nullable = false)
        public LocalDateTime dateTime;
        @JsonAttribute (name = "countries")
        public List<Country.CountryDTO> countries;


        SnapshotDTO(List<Country.CountryDTO> countries, LocalDateTime dateTime){
            this.dateTime = dateTime;
            this.countries = countries;
        }
    }
}
