package hu.tibipi.bumbitrack.core;

import java.util.*;

public class QueryManager {

    Void testStationUIGeneratedQuery(AppUI appUI){
        Query<Station> query = new Query<>();
        List<Filter<Station>> filters = appUI.createFiltersFromQueryLines();
        query.setFilters(filters);

        List<Station> stations = query.executeForStations(Main.currentSnap.getCity().getStations());

        appUI.setResultsToCurrent(stations);

        return null;
    }

    Void testBikeUIGeneratedQuery(AppUI appUI){
        Query<Bike> query = new Query<>();
        List<Filter<Bike>> filters = appUI.createFiltersFromQueryLines();
        query.setFilters(filters);

        List<Station> stations = query.executeForBikes(Main.currentSnap.getCity().getStations());

        appUI.setResultsToCurrent(stations);

        return null;
    }
}
