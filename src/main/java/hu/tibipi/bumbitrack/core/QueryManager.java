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

        appUI.setQueryResults(stations);

        return null;
    }

    Void testBikeUIGeneratedQuery(AppUI appUI){
        Query<Bike> query = new Query<>();
        List<Filter<Bike>> filters = appUI.createFiltersFromQueryLines();
        query.setFilters(filters);

        List<Station> stations = query.executeForBikes(Main.currentSnap.getCity().getStations());

        appUI.setQueryResults(stations);

        return null;
    }

    Void routeQuery(AppUI appUI){
        Query<Bike> query = new Query<>();
        String bikeName = appUI.getBikeNameToFollow();

        Function<Bike, String> getterFunction = Main.getBikeGetterFunction("getName");

        List<Filter<Bike>> filters = new ArrayList<>();
        filters.add(new NameFilter<>(getterFunction, bikeName));

        query.setFilters(filters);

        List<Station> resultStations = new ArrayList<>();
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
                resultStations.add(new Station(""));
            } else {
                resultStations.add(result.get(0));
            }
            times.add(snap.getDateTime());
        }

        Route route = new Route(resultStations, times);

        appUI.setRouteResults(route);

        return null;
    }

    Void statisticsQuery(AppUI appUI){
        Query<Station> query = new Query<>();
        List<Filter<Station>> filters = appUI.createFiltersFromQueryLines();
        query.setFilters(filters);

        //only integer attributes for now, booleans later
        Function<Station, ?> getterFunction = Main.getStationGetterFunction(appUI.getChosenStatisticsGetter());

        int firstIndex = SnapshotManager.getSnapshots().size() - appUI.getRouteLimit();
        List<Snapshot> snapshots =
                SnapshotManager.getSnapshots()
                        .subList(Math.max(firstIndex, 0), SnapshotManager.getSnapshots().size());

        List<Integer> dataPoints = new ArrayList<>();
        for(Snapshot snap : snapshots) {
            List<Station> result = query.executeForStations(snap.getCity().getStations());

            int localSum = 0;
            for(Station station : result){
                localSum += (int) getterFunction.apply(station);
            }

            dataPoints.add(localSum);
        }

        appUI.setStatisticsData(dataPoints);

        return null;
    }

    static class TooManyQueryResultsException extends RuntimeException{

    }
}
