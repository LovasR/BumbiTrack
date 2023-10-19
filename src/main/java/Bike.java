import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Bike {
    int number;
    boolean isActive;

    Bike(JSONObject json){
        number = json.getInt("number");
        isActive = json.getBoolean("active");

        System.out.println("Bike: " + number + " initialized");
    }
}
