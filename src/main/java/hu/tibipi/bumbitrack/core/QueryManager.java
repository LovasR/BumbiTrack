package hu.tibipi.bumbitrack.core;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.logging.Level;

public class QueryManager {

    Object testStationQuery(){
        Query<Station> query = new Query<>();
        Function<Station, String> getNameFunctor = Station::getName;
        query.addFilter(
                new GeneralFilter<>(getNameFunctor, "0213-Millenáris", null));
        ArrayList<Station> stations = query.executeForStations(Main.currentSnap.getCity().getStations());
        for(Station s : stations){
            Main.log.log(Level.INFO, s.getName());
        }
        return new Object();
    }
}
