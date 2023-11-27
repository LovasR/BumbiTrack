package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonValue;

@CompiledJson
public class Bike {
    private final int number; // The number associated with the bike
    private final boolean isActive; // The active status of the bike

    /**
     * Constructs a Bike object with a number and active status.
     *
     *
     */
    public Bike(BikeDTO dto) {
        this.number = dto.number;
        this.isActive = dto.isActive;
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
