package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.ArrayList;
import java.util.List;

public class Country {
    private final List<City> cities;
    private final String countryName;

    Country(CountryDTO dataTransferObject){
        cities = new ArrayList<>();
        for(CityDTO cityDTO : dataTransferObject.cities){
            cities.add(new City(cityDTO));
        }
        this.countryName = dataTransferObject.countryName;
    }

    public City getCity() {
        return cities.get(0);
    }

    public String getCountryName() {
        return countryName;
    }

    @CompiledJson
    static class CountryDTO {
        @JsonAttribute(name = "cities")
        public final List<CityDTO> cities;
        @JsonAttribute(name = "name")
        public final String countryName;

        CountryDTO(List<CityDTO> cities, String countryName) {
            this.cities = cities;
            this.countryName = countryName;
        }
    }
}
