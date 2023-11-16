package hu.tibipi.bumbitrack.core;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Settings {
    private JSONObject settingsObject;

    Settings(){
        File dataDirectory = new File(Paths.get("data").toUri());
        if(dataDirectory.mkdir()){
            Main.log.info("Created data folder");
            createSettings();
        } else {
            loadSettings();
        }
    }

    private void loadSettings(){
        String jsonString = null;

        try {
            jsonString = new String(Files.readAllBytes(Paths.get("data", SETTINGS_FILENAME)));
        } catch (IOException e) {
            Main.log.warning("Settings file not found");
            createSettings();
        }

        assert jsonString != null;
        settingsObject = new JSONObject(jsonString);
        Main.log.info("Loaded settings");
    }

    private void createSettings(){
        JSONObject settingsJSONObject = new JSONObject();

        settingsJSONObject.put(UPDATE_FREQUENCY, 30);
        settingsJSONObject.put(URL, "https://maps.nextbike.net/maps/nextbike-live.json?domains=bh");
        settingsJSONObject.put(COUNTRY_NAME, "MOL Bubi");
        settingsJSONObject.put(CITY_NAME, "Budapest");

        try {
            Files.writeString(Paths.get("data", SETTINGS_FILENAME), settingsJSONObject.toString(2));
        } catch (IOException e) {
            Main.log.severe("Couldn't write a basic settings file");
        }

        settingsObject = settingsJSONObject;
        Main.log.info("Created settings");
    }

    public <T> T getSetting(String key){
        return (T) settingsObject.get(key);
    }

    private static final String SETTINGS_FILENAME = "settings.json";

    public static final String URL = "usedURL";
    public static final String UPDATE_FREQUENCY = "updateFrequency";
    public static final String COUNTRY_NAME = "countryName";
    public static final String CITY_NAME = "cityName";

}
