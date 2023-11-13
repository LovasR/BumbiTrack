package hu.tibipi.bumbitrack.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

public class QueryManager {

    Void testStationQuery(){
        Query<Station> query = new Query<>();
        Function<Station, String> getNameFunctor = Station::getName;
        query.addFilter(
                new GeneralFilter<>(getNameFunctor, "0213-Millenáris", null));
        ArrayList<Station> stations = query.executeForStations(Main.currentSnap.getCity().getStations());
        for(Station s : stations){
            Main.log.log(Level.INFO, s.getName());
        }
        return null;
    }

    Void testStationUIGeneratedQuery(AppUI appUI){
        Query<Station> query = new Query<>();
        List<Filter<Station>> filters = appUI.createFiltersFromQueryLines();
        query.setFilters(filters);

        List<Station> stations = query.executeForStations(Main.currentSnap.getCity().getStations());
        for(Station station : stations){
            Main.log.log(Level.INFO, station.getName());
        }

        return null;
    }
}
