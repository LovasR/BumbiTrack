package hu.tibipi.bumbitrack.core;

public class Place {
    private final double longitude;
    private final double latitude;
    Place(double lo, double la){
        longitude = lo;
        latitude = la;
    }

    static double degreesToRadian(double deg){
        return deg * 3.14159 / 180;
    }

    double calcDistanceTo(Place p){
        double latdiff = degreesToRadian(latitude - p.latitude);
        double lngdiff = degreesToRadian(longitude - p.longitude);

        double radius = 6371;        //estimation of earth's radius

        double a = Math.sin(latdiff / 2);    //shorthands
        double b = Math.sin(lngdiff / 2);

        return 2 * radius * Math.asin(
                Math.sqrt((a * a + Math.cos(degreesToRadian(latitude)) * Math.cos(degreesToRadian(p.latitude)) * b * b))
            );
    }
}
