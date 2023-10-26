import org.json.JSONObject;

public class Bike {
    private final int number;
    private final boolean isActive;

    Bike(JSONObject json){
        number = json.getInt("number");
        isActive = json.getBoolean("active");
    }


    public int getNumber() {
        return number;
    }

    public boolean getIsActive() {
        return isActive;
    }
}
