package hu.tibipi.bumbitrack.core;

import java.util.List;
import java.util.function.Function;

public interface AppUI {
    void start();

    void setQueryRunners(Function<QueryManager, Object> stationQuery, Function<QueryManager, Object> bikeQuery);

    <T> List<Filter<T>> createFiltersFromQueryLines();

    void setResultsToCurrent(List<Station> results);
}
