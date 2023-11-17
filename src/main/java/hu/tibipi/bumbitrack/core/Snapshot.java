package hu.tibipi.bumbitrack.core;

import java.time.LocalDateTime;

public class Snapshot {
    private final LocalDateTime dateTime;
    private final City city;

    private final String ID;


    Snapshot(City city, String countryName){
        dateTime = LocalDateTime.now();
        this.city = city;
        this.ID = createID(countryName, city.getName());
    }
    Snapshot(City city, String countryName, LocalDateTime dateTime){
        this.dateTime = dateTime;
        this.city = city;
        this.ID = createID(countryName, city.getName());
    }

    public static String createID(String countryName, String cityName){
        countryName = countryName.toLowerCase().replace(" ", "_");
        cityName = cityName.toLowerCase().replace(" ", "_");

        return countryName + "_" + cityName;
    }

    public LocalDateTime getDateTime(){
        return dateTime;
    }

    public City getCity() {
        return city;
    }

    public String getID(){
        return ID;
    }
}
