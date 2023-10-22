public class Place {
    private float longitude;
    private float latitude;
    Place(float lo, float la){
        longitude = lo;
        latitude = la;
    }

    double degreesToRadian(float deg){
        return deg * 3.14159 / 180;
    }

    double calcDistance(Place p1, Place p2){
        double latdiff = degreesToRadian(p1.latitude - p2.latitude);
        double lngdiff = degreesToRadian(p1.longitude - p2.longitude);

        float radius = 6371;        //estimation of earth's radius

        double a = Math.sin(latdiff / 2);    //shorthands
        double b = Math.sin(lngdiff / 2);

        return 2 * radius * Math.asin(
                Math.sqrt((a * a + Math.cos(degreesToRadian(p1.latitude)) * Math.cos(degreesToRadian(p2.latitude)) * b * b))
            );
    }
}
