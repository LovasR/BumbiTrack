package hu.tibipi.bumbitrack.core;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * The QueryManager class manages various types of queries and their execution based on the provided filters.
 */
public class QueryManager {

    /**
     * Generates and executes a query based on user-generated filters for stations and updates the AppUI with query results.
     *
     * @param appUI The user interface where the results will be displayed.
     * @return Always returns null.
     */
    public Void testStationUIGeneratedQuery(AppUI appUI) {
        // Generates a query for stations based on user-generated filters
        Query<Station> query = new Query<>();
        List<Filter<Station>> filters = appUI.createFiltersFromQueryLines();
        query.setFilters(filters);

        // Executes the query for stations and updates the UI with the query results
        List<Station> stations = query.executeForStations(Main.currentSnap.getCity().getStations());
        appUI.setQueryResults(stations);

        return null;
    }
    /**
     * Executes a query for bikes based on user-generated filters and updates the AppUI with query results.
     *
     * @param appUI The user interface where the results will be displayed.
     * @return Always returns null.
     */
    public Void testBikeUIGeneratedQuery(AppUI appUI) {
        // Generates a query for bikes based on user-generated filters
        Query<Bike> query = new Query<>();
        List<Filter<Bike>> filters = appUI.createFiltersFromQueryLines();
        query.setFilters(filters);

        // Executes the query for bikes and updates the UI with the query results
        List<Station> stations = query.executeForBikes(Main.currentSnap.getCity().getStations());
        appUI.setQueryResults(stations);

        return null;
    }

    /**
     * Executes a route query based on a bike name provided by the user interface and updates the AppUI with route results.
     *
     * @param appUI The user interface where the results will be displayed.
     * @return Always returns null.
     * @throws TooManyQueryResultsException if multiple results are obtained during the route query.
     */
    public Void routeQuery(AppUI appUI) {
        // Generates a query for bikes based on the provided bike name
        Query<Bike> query = new Query<>();
        String bikeName = appUI.getBikeNameToFollow();

        Function<Bike, String> getterFunction = Main.getBikeGetterFunction("getName");

        List<Filter<Bike>> filters = new ArrayList<>();
        filters.add(new NameFilter<>(getterFunction, bikeName));

        query.setFilters(filters);

        // Executes the route query based on the bike name and updates the UI with the route results
        List<Station> resultStations = new ArrayList<>();
        List<LocalDateTime> times = new ArrayList<>();

        int firstIndex = SnapshotManager.getSnapshots().size() - appUI.getRouteLimit();
        List<Snapshot> snapshots =
                SnapshotManager.getSnapshots()
                        .subList(Math.max(firstIndex, 0), SnapshotManager.getSnapshots().size());

        for (Snapshot snap : snapshots) {
            List<Station> result = query.executeForBikes(snap.getCity().getStations());
            if (result.size() > 1) {
                throw new TooManyQueryResultsException();
            }
            if (result.isEmpty()) {
                // Create a virtual station that is actually just a placeholder
                resultStations.add(new Station(""));
            } else {
                resultStations.add(result.get(0));
            }
            times.add(snap.getDateTime());
        }

        // Create a route based on the obtained stations and times, then update the UI with the route results
        Route route = new Route(resultStations, times);
        appUI.setRouteResults(route);

        return null;
    }
    /**
     * Executes a statistics query based on user-generated filters and chosen attributes, then updates the AppUI
     * with the resulting statistics data.
     *
     * @param appUI The user interface where the statistics data will be displayed.
     * @return Always returns null.
     */
    public Void statisticsQuery(AppUI appUI) {
        // Generates a statistics query based on user-generated filters and chosen attributes
        Query<Station> query = new Query<>();
        List<Filter<Station>> filters = appUI.createFiltersFromQueryLines();
        query.setFilters(filters);

        // Retrieves the chosen attribute getter function for the statistics query
        Function<Station, ?> getterFunction = Main.getStationGetterFunction(appUI.getChosenStatisticsGetter());

        // Obtains a subset of snapshots based on the statistics limit
        int firstIndex = SnapshotManager.getSnapshots().size() - appUI.getStatisticsLimit();
        List<Snapshot> snapshots =
                SnapshotManager.getSnapshots()
                        .subList(Math.max(firstIndex, 0), SnapshotManager.getSnapshots().size());

        // Executes the statistics query on obtained stations from snapshots and calculates data points
        List<Integer> dataPoints = new ArrayList<>();
        for (Snapshot snap : snapshots) {
            List<Station> result = query.executeForStations(snap.getCity().getStations());

            int localSum = 0;
            for (Station station : result) {
                // Calculates statistics data based on the chosen attribute for each station
                localSum += (int) getterFunction.apply(station);
            }

            dataPoints.add(localSum);
        }

        // Updates the UI with the generated statistics data points
        appUI.setStatisticsData(dataPoints);

        return null;
    }

    /**
     * Exception class to handle situations when there are too many query results.
     */
    static class TooManyQueryResultsException extends RuntimeException {

    }
}
