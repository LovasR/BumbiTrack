package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
/**
 * Settings class containing configuration parameters for the application.
 */
public class Settings {

    private final String usedUrl; // URL used for data retrieval
    private final String countryName; // Name of the country
    private final String cityName; // Name of the city
    private final int snapshotLimit; // Limit for the number of snapshots
    private final int updateCycle; // Update cycle duration

    /**
     * Initializes the settings directory and returns the settings.
     *
     * @param dslJson DslJson instance for deserialization
     * @return The initialized settings object
     */
    public static Settings initSettingsDirectory(DslJson<Object> dslJson) {
        Settings out;

        File dataDirectory = new File(Paths.get("data").toUri());
        if (dataDirectory.mkdir()) {
            Main.log.info("Created data folder");
            Settings newSettings = new Settings();
            writeSettings(newSettings, dslJson);
            out = newSettings;
        } else {
            try {
                out = new Settings(Objects.requireNonNull(dslJson.deserialize(
                        SettingsDTO.class,
                        Files.newInputStream(Paths.get("data", SETTINGS_FILENAME)))));
            } catch (IOException e) {
                Main.log.severe("Couldn't load settings, proceeding with default settings " + e.getLocalizedMessage());
                out = new Settings();
            }
        }
        return out;
    }

    /**
     * Constructs a Settings object based on the provided DTO.
     *
     * @param dto The DTO containing settings information
     */
    Settings(SettingsDTO dto) {
        this.usedUrl = dto.usedUrl;
        this.countryName = dto.countryName;
        this.cityName = dto.cityName;
        this.snapshotLimit = dto.snapshotLimit;
        this.updateCycle = dto.updateCycle;
    }

    /**
     * Constructs default settings.
     */
    private Settings() {
        updateCycle = 30; // Default update cycle duration
        usedUrl = "https://maps.nextbike.net/maps/nextbike-live.json?domains=bh"; // Default URL
        countryName = "MOL Bubi"; // Default country name
        cityName = "Budapest"; // Default city name
        snapshotLimit = 1000; // Default snapshot limit
    }

    /**
     * Writes the provided settings to a settings file.
     *
     * @param settings The settings object to write
     * @param dslJson  DslJson instance for serialization
     */
    public static void writeSettings(Settings settings, DslJson<Object> dslJson) {
        try {
            PrettifyOutputStream prettifyOutputStream = new PrettifyOutputStream(
                    Files.newOutputStream(Paths.get("data", SETTINGS_FILENAME)));
            dslJson.serialize(settings, prettifyOutputStream);
        } catch (IOException e) {
            Main.log.severe("Couldn't write a basic settings file");
        }
        Main.log.info("Settings wrote");
    }

    /**
     * Constant representing the filename for settings data.
     */
    private static final String SETTINGS_FILENAME = "settings.json";

    /**
     * Retrieves the country name from the settings.
     *
     * @return The country name
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Retrieves the snapshot limit from the settings.
     *
     * @return The snapshot limit
     */
    public int getSnapshotLimit() {
        return snapshotLimit;
    }

    /**
     * Retrieves the update cycle duration from the settings.
     *
     * @return The update cycle duration
     */
    public int getUpdateCycle() {
        return updateCycle;
    }

    /**
     * Retrieves the city name from the settings.
     *
     * @return The city name
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Retrieves the URL used from the settings.
     *
     * @return The used URL
     */
    public String getUsedUrl() {
        return usedUrl;
    }

    /**
     * DTO class representing settings information.
     */
    @CompiledJson
    static class SettingsDTO {
        @JsonAttribute(name = "usedURL")
        public final String usedUrl;
        @JsonValue
        public final String countryName;
        @JsonValue
        public final String cityName;
        @JsonValue
        public final int snapshotLimit;
        @JsonValue
        public final int updateCycle;

        /**
         * Constructs a SettingsDTO object with provided values.
         *
         * @param usedUrl      The used URL
         * @param countryName  The country name
         * @param cityName     The city name
         * @param snapshotLimit The snapshot limit
         * @param updateCycle  The update cycle duration
         */
        SettingsDTO(String usedUrl, String countryName, String cityName, int snapshotLimit, int updateCycle) {
            this.usedUrl = usedUrl;
            this.countryName = countryName;
            this.cityName = cityName;
            this.snapshotLimit = snapshotLimit;
            this.updateCycle = updateCycle;
        }
    }
}
