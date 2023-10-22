import org.json.JSONObject;

public class Bike {
    int number;
    boolean isActive;

    Bike(JSONObject json){
        number = json.getInt("number");
        isActive = json.getBoolean("active");
    }
}
