package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a snapshot of data at a specific date and time.
 */
public class Snapshot {
    private final LocalDateTime dateTime;
    private final List<Country> countries;
    private final String ID;

    /**
     * Constructs a Snapshot instance based on a list of countries.
     *
     * @param countries The list of countries to create the snapshot
     */
    Snapshot(List<Country> countries) {
        dateTime = LocalDateTime.now();
        this.countries = countries;
        this.ID = createID(countries.get(0).getCountryName(), countries.get(0).getCity().getName());
    }

    /**
     * Constructs a Snapshot instance from a data transfer object.
     *
     * @param dataTransferObject The data transfer object representing the snapshot
     */
    public Snapshot(SnapshotDTO dataTransferObject) {
        this.dateTime = dataTransferObject.dateTime;
        countries = new ArrayList<>();
        for (Country.CountryDTO countryDTO : dataTransferObject.countries) {
            countries.add(new Country(countryDTO));
        }
        this.ID = createID(countries.get(0).getCountryName(), countries.get(0).getCity().getName());
    }

    /**
     * Creates a unique identifier for the snapshot based on country and city names.
     *
     * @param countryName The name of the country
     * @param cityName    The name of the city
     * @return The unique identifier
     */
    public static String createID(String countryName, String cityName) {
        countryName = countryName.toLowerCase().replace(" ", "_");
        cityName = cityName.toLowerCase().replace(" ", "_");
        return countryName + "_" + cityName;
    }

    /**
     * Retrieves the date and time of the snapshot.
     *
     * @return The date and time of the snapshot
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Retrieves the city associated with the snapshot.
     *
     * @return The city of the snapshot
     */
    public City getCity() {
        return countries.get(0).getCity();
    }

    /**
     * Retrieves the unique identifier of the snapshot.
     *
     * @return The unique identifier of the snapshot
     */
    public String getID() {
        return ID;
    }

    /**
     * Represents the data transfer object for Snapshot.
     */
    @CompiledJson
    public static class SnapshotDTO {
        @JsonAttribute(name = "snapshot_time", nullable = false)
        public LocalDateTime dateTime;
        @JsonAttribute(name = "countries")
        public List<Country.CountryDTO> countries;

        /**
         * Constructs a SnapshotDTO instance from a list of countries and a date and time.
         *
         * @param countries The list of countries
         * @param dateTime  The date and time of the snapshot
         */
        SnapshotDTO(List<Country.CountryDTO> countries, LocalDateTime dateTime) {
            this.dateTime = dateTime;
            this.countries = countries;
        }
    }
}
