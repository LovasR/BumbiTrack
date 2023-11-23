package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.List;

@CompiledJson
public class Country {
    @JsonAttribute (name = "cities")
    public final List<City> city;
    @JsonAttribute (name = "name")
    private final String countryName;

    Country(List<City> city, String countryName){
        this.city = city;
        this.countryName = countryName;
    }

    public City getCity() {
        return city.get(0);
    }

    public String getCountryName() {
        return countryName;
    }
}
