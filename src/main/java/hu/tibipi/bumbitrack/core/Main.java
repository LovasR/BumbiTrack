package hu.tibipi.bumbitrack.core;

import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    static Logger log;
    public static void main(String[] args){
        log = Logger.getLogger(Main.class.getName());
        log.log(Level.INFO, "Initialized");

        String url = "https://maps.nextbike.net/maps/nextbike-live.json?domains=bh";    //this is hardcoded bh, meaning Budapest, Hungary
        String jsonString = null;
        try {
            jsonString = NetUtils.downloadJsonFromURL(url);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Couldnt load .json from web");
            throw new RuntimeException(e);
        }

        JSONObject rootJSON = new JSONObject(jsonString);

        try{
            City bp = new City(rootJSON, "MOL Bubi", "Budapest");
        } catch (CountryNotFoundException e){
            log.log(Level.SEVERE, "Specified country not found in file");
        } catch (CityNotFoundException e){
            log.log(Level.SEVERE, "Specified city not found in file");
        }

    }
}
