package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Country containing a list of cities and a country name.
 */
public class Country {

    private final List<City> cities; // List of cities in the country
    private final String countryName; // Name of the country

    /**
     * Constructs a Country object using a Data Transfer Object (DTO).
     *
     * @param dataTransferObject The DTO containing country information
     */
    Country(CountryDTO dataTransferObject) {
        cities = new ArrayList<>();
        for (City.CityDTO cityDTO : dataTransferObject.cities) {
            cities.add(new City(cityDTO));
        }
        this.countryName = dataTransferObject.countryName;
    }

    /**
     * Retrieves the first city from the list of cities in the country.
     *
     * @return The first city in the country's list
     */
    public City getCity() {
        return cities.get(0);
    }

    /**
     * Retrieves the name of the country.
     *
     * @return The name of the country
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Data Transfer Object (DTO) representing country details.
     */
    @CompiledJson
    static class CountryDTO {
        @JsonAttribute(name = "cities")
        public final List<City.CityDTO> cities; // List of city DTOs
        @JsonAttribute(name = "name")
        public final String countryName; // Name of the country

        /**
         * Constructs a CountryDTO object with cities and a country name.
         *
         * @param cities      List of city DTOs
         * @param countryName Name of the country
         */
        CountryDTO(List<City.CityDTO> cities, String countryName) {
            this.cities = cities;
            this.countryName = countryName;
        }
    }
}
