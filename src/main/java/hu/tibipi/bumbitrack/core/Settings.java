package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Settings {

    private final String usedUrl;
    private final String countryName;
    private final String cityName;
    private final int snapshotLimit;
    private final int updateCycle;

    public static Settings initSettingsDirectory(DslJson<Object> dslJson) {
        Settings out;

        File dataDirectory = new File(Paths.get("data").toUri());
        if(dataDirectory.mkdir()){
            Main.log.info("Created data folder");
            Settings newSettings = new Settings();
            writeSettings(newSettings, dslJson);
            out = newSettings;
        } else {
            try {
                out = new Settings(
                        Objects.requireNonNull(dslJson.deserialize(
                                SettingsDTO.class,
                                Files.newInputStream(Paths.get("data", SETTINGS_FILENAME)))));
            } catch (IOException e){
                Main.log.severe("Couldn't load settings, proceeding with default settings " + e.getLocalizedMessage());
                out = new Settings();
            }
        }
        return out;
    }

    Settings(SettingsDTO dto){
        this.usedUrl = dto.usedUrl;
        this.countryName = dto.countryName;
        this.cityName = dto.cityName;
        this.snapshotLimit = dto.snapshotLimit;
        this.updateCycle = dto.updateCycle;
    }

    private Settings(){
        updateCycle = 30;
        usedUrl = "https://maps.nextbike.net/maps/nextbike-live.json?domains=bh";
        countryName = "MOL Bubi";
        cityName = "Budapest";
        snapshotLimit = 1000;
    }

    public static void writeSettings(Settings settings, DslJson<Object> dslJson){
        try {
            PrettifyOutputStream prettifyOutputStream = new PrettifyOutputStream(
                    Files.newOutputStream(Paths.get("data", SETTINGS_FILENAME)));
            dslJson.serialize(settings, prettifyOutputStream);
        } catch (IOException e) {
            Main.log.severe("Couldn't write a basic settings file");
        }
        Main.log.info("Settings wrote");
    }

    private static final String SETTINGS_FILENAME = "settings.json";

    public String getCountryName() {
        return countryName;
    }

    public int getSnapshotLimit() {
        return snapshotLimit;
    }

    public int getUpdateCycle() {
        return updateCycle;
    }

    public String getCityName() {
        return cityName;
    }

    public String getUsedUrl() {
        return usedUrl;
    }

    @CompiledJson
    static class SettingsDTO{
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

        SettingsDTO(String usedUrl, String countryName, String cityName, int snapshotLimit, int updateCycle){
            this.usedUrl = usedUrl;
            this.countryName = countryName;
            this.cityName = cityName;
            this.snapshotLimit = snapshotLimit;
            this.updateCycle = updateCycle;
        }
    }
}
