package hu.tibipi.bumbitrack.core;

import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Snapshot {
    private final LocalDateTime dateTime;
    private final City city;

    private static final List<Snapshot> snapshots = new ArrayList<>();

    Snapshot(City city){
        dateTime = LocalDateTime.now();
        this.city = city;

        snapshots.add(this);
    }

    LocalDateTime getDateTime(){
        return dateTime;
    }

    public City getCity() {
        return city;
    }

    //Static parts below

    public static List<Snapshot> getSnapshots() {
        return snapshots;
    }

    public static void createNewSnapshot() {
        String url = "https://maps.nextbike.net/maps/nextbike-live.json?domains=bh";    //this is hardcoded bh, meaning Budapest, Hungary
        String jsonString;
        try {
            jsonString = NetUtils.downloadJsonFromURL(url);
        } catch (IOException e) {
            Main.log.log(Level.SEVERE, "Couldnt load .json from web");
            Main.currentSnap = null;
            return;
        }

        JSONObject rootJSON = new JSONObject(jsonString);

        try{
            City bp = new City(rootJSON, "MOL Bubi", "Budapest");
            Main.currentSnap = new Snapshot(bp);
        } catch (CountryNotFoundException e){
            Main.log.log(Level.SEVERE, "Specified country not found in file");
        } catch (CityNotFoundException e){
            Main.log.log(Level.SEVERE, "Specified city not found in file");
        }
    }
}
