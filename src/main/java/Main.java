import org.json.JSONObject;

import java.io.IOException;

public class Main {

    public static void main(String[] args){
        System.out.println("Initialized");
        String url = "https://maps.nextbike.net/maps/nextbike-live.json?domains=bh";    //this is hardcoded bh, meaning Budapest, Hungary
        String jsonString = null;
        try {
            jsonString = NetUtils.downloadJsonFromURL(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject rootJSON = new JSONObject(jsonString);

        try{
            City bp = new City(rootJSON, "MOL Bubi", "Budapest");
        } catch (CountryNotFoundException e){
            System.err.println("Specified country not found in file");
        } catch (CityNotFoundException e){
            System.err.println("Specified city not found in file");
        }

    }
}
