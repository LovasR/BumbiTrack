package hu.tibipi.bumbitrack.core;

import java.util.List;
import java.util.function.Function;

/**
 * Interface defining methods for managing the application's user interface.
 */
public interface AppUI {

    /**
     * Method to start the application UI.
     */
    void start();

    /**
     * Method to remove the loading screen from the UI.
     */
    void deleteLoadingScreen();

    /**
     * Method to update the loading status in the UI.
     *
     * @param progress The progress status to be updated
     */
    void updateLoadingStatus(int progress);

    /**
     * Sets the query runners for station and bike-related queries.
     *
     * @param stationQuery The query runner function for stations
     * @param bikeQuery    The query runner function for bikes
     */
    void setQueryRunners(Function<QueryManager, Object> stationQuery, Function<QueryManager, Object> bikeQuery);

    /**
     * Creates filters from query lines for a generic type.
     *
     * @param <T> The generic type for creating filters
     * @return A list of filters created from query lines
     */
    <T> List<Filter<T>> createFiltersFromQueryLines();

    /**
     * Retrieves the name of the bike to follow from the UI.
     *
     * @return The name of the bike to follow
     */
    String getBikeNameToFollow();

    /**
     * Sets the query runner for retrieving route-related information.
     *
     * @param routeQuery The query runner function for routes
     */
    void setRouteQueryRunner(Function<QueryManager, Void> routeQuery);

    /**
     * Retrieves the limit set for route-related information.
     *
     * @return The limit set for route information
     */
    int getRouteLimit();

    /**
     * Sets the results of station-related queries to the UI.
     *
     * @param results The list of stations obtained from the query
     */
    void setQueryResults(List<Station> results);

    /**
     * Sets the results of route-related queries to the UI.
     *
     * @param resultRoute The route obtained from the query
     */
    void setRouteResults(Route resultRoute);

    /**
     * Sets the statistics query runner for obtaining statistics-related information.
     *
     * @param statisticsQuery The query runner function for statistics
     */
    void setStatisticsQueryRunner(Function<QueryManager, Void> statisticsQuery);

    /**
     * Retrieves the chosen attribute for statistics from the UI.
     *
     * @return The chosen attribute for statistics
     */
    String getChosenStatisticsGetter();

    /**
     * Retrieves the limit set for statistics-related information from the UI.
     *
     * @return The limit set for statistics
     */
    int getStatisticsLimit();

    /**
     * Sets the statistics data to be displayed in the UI.
     *
     * @param data The list of integers representing statistics data
     */
    void setStatisticsData(List<Integer> data);
}
