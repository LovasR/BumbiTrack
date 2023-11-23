package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonValue;
import org.json.JSONObject;

@CompiledJson
public class Bike {
    @JsonValue
    private final int number;
    @JsonValue
    private final boolean isActive;

    Bike(JSONObject json){
        number = json.getInt("number");
        isActive = json.getBoolean("active");
    }

    Bike(int number, boolean isActive){
        this.number = number;
        this.isActive = isActive;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return String.valueOf(number);
    }

    public boolean getIsActive() {
        return isActive;
    }
}
