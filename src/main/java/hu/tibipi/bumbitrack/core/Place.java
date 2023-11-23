package hu.tibipi.bumbitrack.core;

public class Place {
    private final double longitude;
    private final double latitude;
    Place(double lo, double la){
        longitude = lo;
        latitude = la;
    }

    double degreesToRadian(double deg){
        return deg * 3.14159 / 180;
    }

    double calcDistance(Place p1, Place p2){
        double latdiff = degreesToRadian(p1.latitude - p2.latitude);
        double lngdiff = degreesToRadian(p1.longitude - p2.longitude);

        double radius = 6371;        //estimation of earth's radius

        double a = Math.sin(latdiff / 2);    //shorthands
        double b = Math.sin(lngdiff / 2);

        return 2 * radius * Math.asin(
                Math.sqrt((a * a + Math.cos(degreesToRadian(p1.latitude)) * Math.cos(degreesToRadian(p2.latitude)) * b * b))
            );
    }
}
