package hu.tibipi.bumbitrack.core;

import java.util.List;
import java.util.function.Function;

public interface AppUI {
    void start();

    void setQueryRunners(Function<QueryManager, Object> stationQuery, Function<QueryManager, Object> bikeQuery);

    <T> List<Filter<T>> createFiltersFromQueryLines();

    String getBikeNameToFollow();

    void setRouteQueryRunner(Function<QueryManager, Void> routeQuery);

    int getRouteLimit();

    void setQueryResults(List<Station> results);

    void setRouteResults(Route resultRoute);
}
