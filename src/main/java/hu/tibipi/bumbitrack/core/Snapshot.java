package hu.tibipi.bumbitrack.core;

import java.time.LocalDateTime;

public class Snapshot {
    private final LocalDateTime dateTime;
    private final City city;

    Snapshot(City city){
        dateTime = LocalDateTime.now();
        this.city = city;
    }

    LocalDateTime getDateTime(){
        return dateTime;
    }

    public City getCity() {
        return city;
    }
}
