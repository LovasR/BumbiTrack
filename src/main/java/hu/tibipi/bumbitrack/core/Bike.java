package hu.tibipi.bumbitrack.core;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonValue;

/**
 * Represents a Bike with its number and active status.
 */
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

    /**
     * Retrieves the number associated with the bike.
     *
     * @return The number of the bike
     */
    public int getNumber() {
        return number;
    }

    /**
     * Retrieves the name of the bike (which is its number as a string).
     *
     * @return The name of the bike
     */
    public String getName() {
        return String.valueOf(number);
    }

    /**
     * Retrieves the active status of the bike.
     *
     * @return True if the bike is active, false otherwise
     */
    public boolean getIsActive() {
        return isActive;
    }

    @CompiledJson
    public static class BikeDTO {

        @JsonValue
        public final int number; // The number associated with the bike
        @JsonValue
        public final boolean isActive; // The active status of the bike

        BikeDTO(int number, boolean isActive) {
            this.number = number;
            this.isActive = isActive;
        }
    }
}
