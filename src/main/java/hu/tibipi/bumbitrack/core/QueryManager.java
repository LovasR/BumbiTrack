package hu.tibipi.bumbitrack.core;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

public class QueryManager {

    Void testStationUIGeneratedQuery(AppUI appUI){
        Query<Station> query = new Query<>();
        List<Filter<Station>> filters = appUI.createFiltersFromQueryLines();
        query.setFilters(filters);

        List<Station> stations = query.executeForStations(Main.currentSnap.getCity().getStations());

        appUI.setResultsToCurrent(stations, null);

        return null;
    }

    Void testBikeUIGeneratedQuery(AppUI appUI){
        Query<Bike> query = new Query<>();
        List<Filter<Bike>> filters = appUI.createFiltersFromQueryLines();
        query.setFilters(filters);

        List<Station> stations = query.executeForBikes(Main.currentSnap.getCity().getStations());

        appUI.setResultsToCurrent(stations, null);

        return null;
    }

    Void routeQuery(AppUI appUI){
        Query<Bike> query = new Query<>();
        String bikeName = appUI.getBikeNameToFollow();

        Function<Bike, String> getterFunction = Main.getBikeGetterFunction("getName");

        List<Filter<Bike>> filters = new ArrayList<>();
        filters.add(new NameFilter<>(getterFunction, bikeName));

        query.setFilters(filters);

        List<Station> route = new ArrayList<>();
        List<LocalDateTime> times = new ArrayList<>();

        int firstIndex = SnapshotManager.getSnapshots().size() - appUI.getRouteLimit();
        List<Snapshot> snapshots =
                SnapshotManager.getSnapshots()
                .subList(Math.max(firstIndex, 0), SnapshotManager.getSnapshots().size());

        for(Snapshot snap : snapshots){
            List<Station> result = query.executeForBikes(snap.getCity().getStations());
            if(result.size() > 1){
                throw new TooManyQueryResultsException();
            }
            if(result.isEmpty()){
                //create a virtual station that is actually just a placeholder
                route.add(new Station(""));
            } else {
                route.add(result.get(0));
            }
            times.add(snap.getDateTime());
        }

        appUI.setResultsToCurrent(route, times);

        return null;
    }

    static class TooManyQueryResultsException extends RuntimeException{

    }
}
