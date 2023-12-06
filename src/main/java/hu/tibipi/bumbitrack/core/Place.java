package hu.tibipi.bumbitrack.core;

/**
 * The Place class represents a geographical location with longitude and latitude coordinates.
 */
public class Place {
    private final double longitude;
    private final double latitude;

    /**
     * Constructs a Place object with specified longitude and latitude.
     *
     * @param lo The longitude of the place.
     * @param la The latitude of the place.
     */
    Place(double lo, double la) {
        longitude = lo;
        latitude = la;
    }

    /**
     * Converts degrees to radians.
     *
     * @param deg The value in degrees to be converted to radians.
     * @return The value in radians.
     */
    static double degreesToRadian(double deg) {
        return deg * Math.PI / 180;
    }

    /**
     * Calculates the distance between two places using their coordinates.
     *
     * @param p The Place object representing the other location.
     * @return The distance between the current Place and the provided Place in kilometers.
     */
    double calcDistanceTo(Place p) {
        double latdiff = degreesToRadian(latitude - p.latitude);
        double lngdiff = degreesToRadian(longitude - p.longitude);

        double radius = 6371; // Estimation of Earth's radius in kilometers

        double a = Math.sin(latdiff / 2); // Shorthand variables for trigonometric calculations
        double b = Math.sin(lngdiff / 2);

        return 2 * radius * Math.asin(
                Math.sqrt((a * a + Math.cos(degreesToRadian(latitude)) * Math.cos(degreesToRadian(p.latitude)) * b * b))
        );
    }
}
