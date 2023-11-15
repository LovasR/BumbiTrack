package hu.tibipi.bumbitrack.core;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public interface AppUI {
    void start();

    void setQueryRunners(Function<QueryManager, Object> stationQuery, Function<QueryManager, Object> bikeQuery);

    <T> List<Filter<T>> createFiltersFromQueryLines();

    String getBikeNameToFollow();

    void setRouteQueryRunner(Function<QueryManager, Void> routeQuery);

    void setResultsToCurrent(List<Station> results, List<LocalDateTime> times);
}
